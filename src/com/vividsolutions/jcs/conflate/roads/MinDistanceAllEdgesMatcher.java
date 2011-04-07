/*
 * The JCS Conflation Suite (JCS) is a library of Java classes that
 * can be used to build automated or semi-automated conflation solutions.
 *
 * Copyright (C) 2003 Vivid Solutions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package com.vividsolutions.jcs.conflate.roads;

import java.util.*;

import com.vividsolutions.jcs.algorithm.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.*;
import com.vividsolutions.jts.index.strtree.*;
import com.vividsolutions.jump.geom.*;

/**
 * Computes matches and match values for all nearby edges to each edge.
 */
public class MinDistanceAllEdgesMatcher
{

  private static final String MATCH = "MinDistanceEdgeMatcher";

  private static final EdgeMatchIndFactory edgeMatchIndFact = new EdgeMatchIndFactory();

  private static double computeAreaHausdorrfDistance(Geometry edge0, Geometry edge1)
  {
/*
    // this has proved not to be very good
    AverageLineDistance avgLineDist = new AverageLineDistance((LineString) edge0, (LineString)edge1);
    double dist2 = avgLineDist.getDistance();
*/
    double len0 = edge0.getLength();
    double len1 = edge1.getLength();
    double edgeSep = edge0.distance(edge1);

    // match value based on area between edges, adjusted by relative length
    Geometry edgeMatchInd = edgeMatchIndFact.getIndicator((LineString) edge0, (LineString) edge1);
    double edgeMatchIndArea = edgeMatchInd.getArea();
    // weight by the difference in length - a greater difference increases the effective distance
    double lenDifferenceFrac = (len0 > len1) ? len0 / len1 : len1 / len0;
    double areaMatchValue = edgeMatchIndArea * lenDifferenceFrac;

    // match value based on MaxVertexDistance
    VertexHausdorffDistance maxVertDist = new VertexHausdorffDistance(edge0, edge1);
    double hausdorrfDist = maxVertDist.distance();
    //double distanceUpperBound = Math.max(len0, len1) + edgeSep;
    double distanceUpperBound = 3 * Math.min(len0, len1);
    // the further the distance the worse the match
    double hausdorrfDistValue = MatchValueCombiner.scale(hausdorrfDist, distanceUpperBound, 0.0);
    //double maxVertDistValue = MatchValueCombiner.scale(maxVertdist, 0.0, len0 + len1);
    // skewing the distance function doesn't seem to make much difference in the best match
    double skewedMaxVertDistValue = 1.0 - (1.0 - hausdorrfDistValue) * (1.0 - hausdorrfDistValue);

    //return skewedMaxVertDistValue * areaMatchValue;
    //return skewedMaxVertDistValue;
    return hausdorrfDistValue;
//System.out.println(dist + "   " + dist2 + "  " + dist3);
  }
  private static double computeDistance(Geometry edge0, Geometry edge1)
  {
    double len0 = edge0.getLength();
    double len1 = edge1.getLength();
    double edgeSep = edge0.distance(edge1);

    // match value based on MaxVertexDistance
    VertexHausdorffDistance maxVertDist = new VertexHausdorffDistance(edge0, edge1);
    double hausdorrfDist = maxVertDist.distance();
    //double distanceUpperBound = Math.max(len0, len1) + edgeSep;
    double distanceUpperBound = 3 * Math.min(len0, len1);
    // the further the distance the worse the match
    double hausdorrfDistValue = MatchValueCombiner.scale(hausdorrfDist, distanceUpperBound, 0.0);
    //double maxVertDistValue = MatchValueCombiner.scale(maxVertdist, 0.0, len0 + len1);
    // skewing the distance function doesn't seem to make much difference in the best match
    double skewedMaxVertDistValue = 1.0 - (1.0 - hausdorrfDistValue) * (1.0 - hausdorrfDistValue);

    //return skewedMaxVertDistValue * areaMatchValue;
    //return skewedMaxVertDistValue;
    return hausdorrfDistValue;
  }

  private static void buildRoadEdgeIndex(Iterator edgeIt, SpatialIndex index)
  {
    while (edgeIt.hasNext()) {
      RoadEdge edge = (RoadEdge) edgeIt.next();
      index.insert(edge.getGeometry().getEnvelopeInternal(), edge);
    }
  }

  public static List query(RoadEdge edge, double queryBufferDistance, SpatialIndex index)
  {
    Envelope queryEnv = EnvelopeUtil.expand(
        edge.getGeometry().getEnvelopeInternal(),
        queryBufferDistance);

    return index.query(queryEnv);
  }

  private List[] edges = new List[2];
  private Iterator[] edgeIt = new Iterator[2];
  private SpatialIndex roadEdgeIndex = new STRtree();

  public MinDistanceAllEdgesMatcher(List edges0, List edges1)
  {
    edges[0] = edges0;
    edges[1] = edges1;
  }

  public void match()
  {
    match(0, 1);
    match(1, 0);
  }

  public void match(int fromIndex, int toIndex)
  {
    roadEdgeIndex = new STRtree();
    buildRoadEdgeIndex(edges[toIndex].iterator(), roadEdgeIndex);

    for (Iterator i = edges[fromIndex].iterator(); i.hasNext(); ) {
      RoadEdge edge = (RoadEdge) i.next();
      double queryBufferDist = edge.getGeometry().getLength();
      List closeEdges = query(edge, queryBufferDist, roadEdgeIndex);
      computeEdgeMatchValues(edge, closeEdges);
    }
  }

  public static double MAX_LENGTH_DIFF_PERCENT = 0.50;

  private static void computeEdgeMatchValues(RoadEdge edge, List closeEdges)
  {
    for (Iterator i = closeEdges.iterator(); i.hasNext(); ) {
      RoadEdge testEdge = (RoadEdge) i.next();

      LineString line = (LineString) edge.getGeometry();
      LineString testLine =  (LineString) testEdge.getGeometry();

      // if lines are roughly straight, they must have similar orientation
      // or else they don't match
      if (LineStringMatcher.isApproximatelyStraight(line)
          && LineStringMatcher.isApproximatelyStraight(testLine)) {
        if (! LineStringMatcher.isOrientationCompatible(line, testLine))
          continue;
      }

      // if the lines are very different in length they don't match
      if (LineStringMatcher.lengthDifferencePercent(line, testLine) < MAX_LENGTH_DIFF_PERCENT)
        continue;

      double dist = computeDistance(edge.getGeometry(), testEdge.getGeometry());

      edge.addMatch(testEdge, dist);
      testEdge.addMatch(edge, dist);
    }
  }

  public void findMutualBestMatches()
  {
    for (Iterator i = edges[0].iterator(); i.hasNext(); ) {
      RoadEdge edge = (RoadEdge) i.next();
      RoadEdge matchEdge = getBestMatchEdge(edge);
      if (matchEdge == null) continue;

      if (edge == getBestMatchEdge(matchEdge)) {
        double value = getBestMatchValue(edge);
        edge.setMatch(matchEdge, value);
        matchEdge.setMatch(edge, value);
      }
    }
  }

  private static double getBestMatchValue(RoadEdge edge)
  {
    Matches matchList = edge.getMatches();
    MatchValue mv = matchList.getBestMatch();
    if (mv == null) return 0.0;
    return mv.getValue();
  }

  private static RoadEdge getBestMatchEdge(RoadEdge edge)
  {
    Matches matchList = edge.getMatches();
    MatchValue mv = matchList.getBestMatch();
    if (mv == null) return null;
    return (RoadEdge) mv.getMatch();
  }
}
