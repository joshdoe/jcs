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
import com.vividsolutions.jcs.graph.*;
import com.vividsolutions.jcs.conflate.roads.nodematch.*;

public class RoadNodeTopologyMatcher {

  public static MatchNode createMatchNode(DirectedEdgeStar deStar)
  {
    MatchNode matchNode = new MatchNode(deStar.getNumEdges());
    List edges = deStar.getEdges();
    for (int i = 0; i < deStar.getNumEdges(); i++) {
      DirectedEdge de = (DirectedEdge) edges.get(i);
      matchNode.setEdge(i, de.getAngle());
    }
    return matchNode;
  }

  public RoadNodeTopologyMatcher() {
  }

  public double matchValue(RoadNode node1, RoadNode node2)
  {
    /*
    if (node1.getOutEdges().getNumEdges() != node2.getOutEdges().getNumEdges())
      return 0.0;
    */
    MatchNode matchNode1 = createMatchNode(node1.getOutEdges());
    MatchNode matchNode2 = createMatchNode(node2.getOutEdges());
    NodeMatcher matcher = new NodeMatcher(matchNode1, matchNode2);
    //return matcher.exactTopoMatchValue();
    return matcher.fullMatchValue();
  }
}
