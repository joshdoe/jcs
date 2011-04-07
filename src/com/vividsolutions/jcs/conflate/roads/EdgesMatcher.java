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
import com.vividsolutions.jts.util.*;
import com.vividsolutions.jump.geom.Angle;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jcs.graph.*;
import com.vividsolutions.jcs.conflate.roads.nodematch.NodeMatching;

public class EdgesMatcher {

  public EdgesMatcher() {
  }

  public void match(RoadMatcher roadMatcher)
  {
    matchEdgesWithBothNodesMatchingSameEdge(roadMatcher);
    //matchEdgesWithNodesMatchingAdjacentEdges(roadMatcher, 0);
    //matchEdgesWithNodesMatchingAdjacentEdges(roadMatcher, 1);
    matchDanglingOrInlineEdges(roadMatcher);
  }

  /**
   * Matches edges for which there is a match for both nodes
   * @param roadMatcher
   */
  public void matchEdgesWithBothNodesMatchingSameEdge(RoadMatcher roadMatcher)
  {
    for (Iterator i = roadMatcher.getNetwork(1).edgeIterator(); i.hasNext(); )
    {
      RoadEdge edge = (RoadEdge) i.next();
      if (edge.hasMatch()) continue;

      matchEdgeWithBothNodesMatchingSameEdge(edge);
    }
  }
  private boolean matchEdgeWithBothNodesMatchingSameEdge(RoadEdge edge)
  {
    RoadNode[] node = edge.getNodes();
    // must have both ends matched
    if (node[0].getMatch() == null || node[1].getMatch() == null)
      return false;
    // ends better not be the same node!
    if (node[0].getMatch() == node[1].getMatch()) return false;

    // find the edges which link these two nodes
    Collection linkEdges = Node.getEdgesBetween(node[0].getMatch(), node[1].getMatch());

    if (linkEdges.size() == 1) {
      RoadEdge matchEdge = (RoadEdge) linkEdges.iterator().next();
      edge.setMutualMatch(matchEdge);
      return true;
    }
    else if (linkEdges.size() > 1) {
      // what do we do if the size > 1 ?!!!!
      // for now just print msg
     Debug.println("found nodes with more than one edge connecting them (between " + node[0].getMatch().getCoordinate() + " and" + node[1].getMatch().getCoordinate());
     for (Iterator i = linkEdges.iterator(); i.hasNext(); ) {
       RoadEdge re = (RoadEdge) i.next();
       Debug.println(re);
     }
    }
    return false;

  }
  /**
   * Matches edges for which there is a match for both nodes
   * @param roadMatcher
   */
  public void matchEdgesWithNodesMatchingAdjacentEdges(RoadMatcher roadMatcher, int index)
  {
    for (Iterator i = roadMatcher.getNetwork(index).edgeIterator(); i.hasNext(); )
    {
      RoadEdge edge = (RoadEdge) i.next();
      if (edge.hasMatch()) continue;

      matchEdgeWithNodesMatchingAdjacentEdges(edge);
    }
  }

  private static RoadEdge getMatchedEdge(RoadNode node, RoadEdge edge)
  {
    NodeMatching nodeMatching = node.getMatching();
    RoadEdge matchEdge = (RoadEdge) nodeMatching.getMatchedEdge(node, edge);
    return matchEdge;
  }
  /*
  private static RoadEdge getOtherNodeForMatchedEdge(RoadNode node, RoadEdge edge)
  {
    NodeMatching nodeMatching = node.getMatching();
    Node matchNode = nodeMatching.getMatchedNode(node);
    RoadEdge matchEdge = (RoadEdge) nodeMatching.getMatchedEdge(node, edge);
    if (matchEdge == null)
      return null;
    RoadNode otherMatchNode = matchEdge.getOtherNode(node.getMatch());
    /*
    int matchEdgeIndex = nodeMatching.getMatchedEdgeIndex(node, node.getIndex(edge));
    RoadNode matchNode = node.getMatch();

    int otherIndex = 1 - nodeMatching.getNodeIndex(node);
    RoadNode otherNode = (RoadNode) nodeMatching.getNode(otherIndex);
    Assert.isTrue(otherNode == matchNode);

    RoadDirectedEdge rde = (RoadDirectedEdge) matchNode.getOutEdges().getEdges().get(matchEdgeIndex);
    return (RoadEdge) rde.getEdge();
    */
  //}

  private boolean matchEdgeWithNodesMatchingAdjacentEdges(RoadEdge edge)
  {
    RoadNode[] node = edge.getNodes();
    // edge must have both nodes matched
    if (node[0].getMatch() == null || node[1].getMatch() == null)
      return false;
    // ends must not match the same node!
    if (node[0].getMatch() == node[1].getMatch()) return false;

    RoadEdge matchEdge0 = getMatchedEdge(node[0], edge);
    if (matchEdge0 == null) return false;
    RoadEdge matchEdge1 = getMatchedEdge(node[1], edge);
    if (matchEdge1 == null) return false;

    if (! matchEdge0.hasNode(node[0].getMatch())) return false;
    if (! matchEdge1.hasNode(node[1].getMatch())) return false;

    RoadNode otherMatchNode0 = matchEdge0.getOtherNode(node[0].getMatch());
    RoadNode otherMatchNode1 = matchEdge1.getOtherNode(node[1].getMatch());

    if (otherMatchNode0 == otherMatchNode1) {
      edge.setMutualMatch(matchEdge0);
      Debug.println("matched adjacent edge: " + edge);
      // how do we match two edges to one?
    }
    return false;

  }

  /**
  * Matches edges which have one matched node and
  * in which the other node is "dangling" or "inline".
  * (Dangling edges have a node of degree 1;
  * inline edges have a node of degree 2)
  *
  */
  public void matchDanglingOrInlineEdges(RoadMatcher roadMatcher)
  {
    for (Iterator i = roadMatcher.getNetwork(1).edgeIterator(); i.hasNext(); )
    {
      RoadEdge edge = (RoadEdge) i.next();
      if (edge.hasMatch()) continue;

      RoadNode[] node = edge.getNodes();

      if (node[0].hasMatch() && node[1].getDegree() <= 2)
        matchDanglingEdge(edge, node[0], node[1]);
      else if (node[1].hasMatch() && node[0].getDegree() <= 2)
        matchDanglingEdge(edge, node[1], node[0]);

    }
  }

  /**
   * Finds a match for an edges which has one matched node and
   * in which the other node is "dangling" or "inline".
   * The match is chosen from the edges incident to the matched base node.
   *
   * @param edge the edge to match
   * @param baseNode the node at the "base" of the dangling edge
   * @param nodeDegree1 the node with degree 1 or 2
   *
   * @return <code>true</code> if a match was found
   */
  private boolean matchDanglingEdge(RoadEdge edge, RoadNode baseNode, RoadNode nodeDegree1)
  {
    RoadNode matchNode = baseNode.getMatch();
    DirectedEdge baseDE = edge.getDirEdge(baseNode);

    double[] angleDiff = new double[1];
    RoadEdge matchEdgeCandidate = matchNode.findClosestEdge(baseDE.getAngle(), angleDiff);

    // if edge is too far away don't match
    if (angleDiff[0] > Angle.PI_OVER_4) return false;

    if (matchEdgeCandidate.hasMatch()) return false;

    edge.setMutualMatch(matchEdgeCandidate);
    return true;
  }



}
