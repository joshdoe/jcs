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
import com.vividsolutions.jcs.util.BufferedIterator;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jcs.algorithm.*;
import com.vividsolutions.jts.index.*;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jump.geom.EnvelopeUtil;
import com.vividsolutions.jcs.algorithm.AverageLineDistance;
import com.vividsolutions.jts.util.Debug;
import com.vividsolutions.jcs.debug.DebugFeature;

/**
 * Computes the best match for each edge, based on a distance function.
 */
public class MinDistanceEdgeMatcher {

  private static final String MATCH = "MinDistanceEdgeMatcher";

  private static final EdgeMatchIndFactory edgeMatchIndFact = new EdgeMatchIndFactory();

  private static double computeDistance(Geometry edge0, Geometry edge1)
  {
/*
    // this has proved not to be very good
    AverageLineDistance avgLineDist = new AverageLineDistance((LineString) edge0, (LineString)edge1);
    double dist2 = avgLineDist.getDistance();
*/
    // match value based on area between edges, adjusted by relative length
    Geometry edgeMatchInd = edgeMatchIndFact.getIndicator((LineString) edge0, (LineString) edge1);
    double edgeMatchIndArea = edgeMatchInd.getArea();
    // weight by the difference in length - a greater difference increases the effective distance
    double len0 = edge0.getLength();
    double len1 = edge1.getLength();
    double lenDifferenceFrac = (len0 > len1) ? len0 / len1 : len1 / len0;
    double areaMatchValue = edgeMatchIndArea * lenDifferenceFrac;

    // match value based on MaxVertexDistance
    VertexHausdorffDistance maxVertDist = new VertexHausdorffDistance(edge0, edge1);
    double maxVertdist = maxVertDist.distance();
    double maxVertDistValue = MatchValueCombiner.scale(maxVertdist, 0.0, len0 + len1);

    return maxVertDistValue * areaMatchValue;
//System.out.println(dist + "   " + dist2 + "  " + dist3);

  }
  private static void buildRoadEdgeIndex(List edges, SpatialIndex index)
  {
    for (Iterator i = edges.iterator(); i.hasNext(); ) {
      RoadEdge edge = (RoadEdge) i.next();
      index.insert(edge.getGeometry().getEnvelopeInternal(), edge);
    }
  }

  public static List query(RoadEdge edge, double distance, SpatialIndex index)
  {
    Envelope queryEnv = EnvelopeUtil.expand(edge.getGeometry().getEnvelopeInternal(), distance);
    return index.query(queryEnv);
  }

  private List[] edgeLists = new List[2];
  private SpatialIndex roadEdgeIndex = new STRtree();

  public MinDistanceEdgeMatcher(List edges0, List edges1)
  {
    edgeLists[0] = edges0;
    edgeLists[1] = edges1;
    buildRoadEdgeIndex(edgeLists[1], roadEdgeIndex);
  }

  public void match()
  {
    RoadEdge[] matchEdgeOut = new RoadEdge[1];
    List matches = new ArrayList();
    for (BufferedIterator i = new BufferedIterator(edgeLists[0].iterator()); i.hasNext(); ) {
      RoadEdge edge = (RoadEdge) i.next();
      double queryBufferDist = edge.getGeometry().getLength();
      List closeEdges = query(edge, queryBufferDist, roadEdgeIndex);
      double distance = findMinDistanceEdge(edge, closeEdges, matchEdgeOut);
      RoadEdge matchEdge = matchEdgeOut[0];

      if (matchEdge != null) {
        // if this match is better than the current one, redo the current match
        RoadEdge prevMatch = matchEdge.getMatch();
        double prevMatchDistance = Double.MAX_VALUE;
        if (prevMatch != null) {
          prevMatchDistance = prevMatch.getMatchDistance();
          if (prevMatchDistance > distance) {
          // displace the previous lower valued match, if any
              i.putBack(prevMatch);
          }
        }
        edge.setMatch(matchEdge, distance);
        matchEdge.setMatch(edge, distance);

Geometry edgeMatchInd = edgeMatchIndFact.getIndicator((LineString) edge.getGeometry(), (LineString) matchEdge.getGeometry());
DebugFeature.add(MATCH, edgeMatchInd, "val=" + distance);
      }
    }
DebugFeature.saveFeatures(MATCH, "X:\\jcs\\roads\\test\\output\\edgeMatches.shp");
  }

  private static double findMinDistanceEdge(RoadEdge edge, List closeEdges, RoadEdge[] matchEdgeOut)
  {
    matchEdgeOut[0] = null;
    RoadEdge closestEdge = null;
    double closestDistance = Double.MAX_VALUE;
    for (Iterator i = closeEdges.iterator(); i.hasNext(); ) {
      RoadEdge testEdge = (RoadEdge) i.next();

      double dist = computeDistance(edge.getGeometry(), testEdge.getGeometry());
      if (dist < closestDistance) {
        closestEdge = testEdge;
        closestDistance = dist;
      }
    }
    matchEdgeOut[0] = closestEdge;
    return closestDistance;
  }
}
