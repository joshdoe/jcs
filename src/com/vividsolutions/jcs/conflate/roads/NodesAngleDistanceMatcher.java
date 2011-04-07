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
import com.vividsolutions.jts.util.Assert;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jcs.debug.DebugFeature;
import com.vividsolutions.jcs.conflate.roads.nodematch.NodeMatching;

public class NodesAngleDistanceMatcher {

  public NodesAngleDistanceMatcher()
  {
  }

  public void match(RoadMatcher roadMatcher)
  {
    for (Iterator i = roadMatcher.getNetwork(RoadMatcher.SUBJECT).graph.nodeIterator();
         i.hasNext(); ) {
      RoadNode node = (RoadNode) i.next();

      // don't try and match "inline" nodes
      if (node.getOutEdges().getNumEdges() <= 2) continue;

      double queryDist = node.getMaxAdjacentNodeDistance();
      List candidateSet = roadMatcher.getNetwork(RoadMatcher.REFERENCE).nodesWithinDistance(node.getCoordinate(), queryDist);

      computeAngleDistanceMatchValues(node, candidateSet);
    }
  }

  private void computeAngleDistanceMatchValues(RoadNode node, List candidateSet
      )
  {

    Coordinate pt = node.getCoordinate();

    for (Iterator i = candidateSet.iterator(); i.hasNext(); ) {
      RoadNode candidateNode = (RoadNode) i.next();

      // don't try and match "inline" nodes
      if (candidateNode.getOutEdges().getNumEdges() <= 2) continue;

      NodeMatching nodeMatching = new NodeMatching(node, candidateNode);
      //double matchValue = nodeMatching.edgeMatchValue();
      double matchValue = nodeMatching.angleDistanceMatchValue(node, candidateNode);

      candidateNode.addMatch(node, matchValue);
      node.addMatch(candidateNode, matchValue);
    }
  }

}
