/*
 * The Java Conflation Suite (JCS) is a library of Java classes that
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
import com.vividsolutions.jts.util.Assert;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jcs.debug.DebugFeature;
import com.vividsolutions.jcs.conflate.roads.nodematch.NodeMatching;

public class RoadNodeMatcher {

  public RoadNodeMatcher()
  {
  }

  public void match(RoadMatcher roadMatcher)
  {
    // only allocate return buffers once
    RoadNode[] resultNode = new RoadNode[1];
    NodeMatching[] resultNodeMatching = new NodeMatching[1];

    BufferedIterator i = new BufferedIterator(roadMatcher.getNetwork(RoadMatcher.SUBJECT).graph.nodeIterator());
    while (i.hasNext() ) {
      RoadNode node = (RoadNode) i.next();

      // don't try and match "inline" nodes
      if (node.getOutEdges().getNumEdges() <= 2) continue;

      double queryDist = node.getMaxAdjacentNodeDistance();
      List candidateSet = roadMatcher.getNetwork(RoadMatcher.REFERENCE).nodesWithinDistance(node.getCoordinate(), queryDist);

      double matchValue = findClosestUnmatchedSimilarTopoNode(node, candidateSet, resultNode, resultNodeMatching);
      RoadNode matchNode = resultNode[0];
      NodeMatching nodeMatching = resultNodeMatching[0];


      if (matchNode != null) {
        // if this match is better than the current one, redo the current match
        RoadNode prevMatchNode = matchNode.getMatch();
        double prevMatchValue = matchNode.getMatchValue();
        if (prevMatchValue < matchValue) {
          // displace the lower valued match
          if (prevMatchNode != null) {
              i.putBack(prevMatchNode);
          }

          matchNode.setMatch(node, matchValue);
          matchNode.setMatching(nodeMatching);
          node.setMatch(matchNode, matchValue);
          node.setMatching(nodeMatching);
        }
      }
    }
DebugFeature.saveFeatures(MATCH, "X:\\jcs\\data\\roads\\test\\output\\nodeAllMatches.jml");
  }

/* not used - probably incorrect   MD 28/5/03
  public void clearInconsistentOrientationMatches(RoadMatcher roadMatcher)
  {
    Iterator i = roadMatcher.getNetwork(RoadMatcher.SUBJECT).graph.nodeIterator();
    while (i.hasNext() ) {
      RoadNode node = (RoadNode) i.next();

      NodeMatchOrientationTester tester = new NodeMatchOrientationTester(node);
      if (tester.isInconsistent())
        node.clearMatch();
    }
  }
*/

private static final String MATCH = "Match";

  private double findClosestUnmatchedSimilarTopoNode(RoadNode node, List candidateSet,
      RoadNode[] resultNode,
      NodeMatching[] resultNodeMatching)
  {
    resultNode[0] = null;
    resultNodeMatching[0] = null;
    double maxMatchValue = 0.0;

    Coordinate pt = node.getCoordinate();

    for (Iterator i = candidateSet.iterator(); i.hasNext(); ) {
      RoadNode candidateNode = (RoadNode) i.next();

      // don't try and match "inline" nodes
      if (candidateNode.getOutEdges().getNumEdges() <= 2) continue;

      NodeMatching nodeMatching = new NodeMatching(node, candidateNode);
      double matchValue = nodeMatching.angleDistanceMatchValue(node, candidateNode);
      DebugFeature.addLineSegment(MATCH, pt, candidateNode.getCoordinate(), "val=" + (int) (1000.0 * matchValue));

      // don't match if candidate node already has a better match
      if (candidateNode.getMatchValue() > matchValue) {
        continue;
      }

      if (matchValue > maxMatchValue) {
        resultNode[0] = candidateNode;
        resultNodeMatching[0] = nodeMatching;
        maxMatchValue = matchValue;
      }
    }
    return maxMatchValue;
  }

}
