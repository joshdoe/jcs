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

/**
 * Computes a match between two road edges based on their respective endnode matches
 */
public class RoadEdgeAndNodesMatcher {

  private RoadEdge[] edge = new RoadEdge[2];

  public RoadEdgeAndNodesMatcher(RoadEdge edge0, RoadEdge edge1)
  {
    edge[0] = edge0;
    edge[1] = edge1;
  }

  public boolean isMatch()
  {
    RoadNode[] matchNodes = sortNodes(matchedNodes(edge[0].getNodes()));
    RoadNode[] nodes = sortNodes((RoadNode[]) edge[1].getNodes().clone());

    if (matchedNodeCount(matchNodes) != matchedNodeCount(nodes))
      return false;
    if (isEqual(matchNodes, nodes)) return true;
    return false;
  }

  public static boolean isEqual(RoadNode[] nodes0, RoadNode[] nodes1)
  {
    for (int i = 0; i < nodes0.length; i++) {
      if (nodes0[i] != nodes1[i])
        return false;
    }
    return true;
  }

  public RoadNode[] matchedNodes(RoadNode[] nodes)
  {
    RoadNode[] matchedNodes = new RoadNode[2];
    if (nodes[0] != null)
      matchedNodes[0] = nodes[0].getMatch();
    if (nodes[1] != null)
      matchedNodes[1] = nodes[1].getMatch();
    return matchedNodes;
  }


  /**
   * Sorts an array of two nodes into node-order.
   * Null nodes will appear first in the array.
   * @param nodes
   * @return the sorted array
   */
  public RoadNode[] sortNodes(RoadNode[] nodes)
  {
    if (nodes[1] == null)
      flip(nodes);
    if (nodes[0] == null) return nodes;
    if (nodes[0].compareTo(nodes[1]) > 0)
      flip(nodes);
    return nodes;
  }

  public void flip(RoadNode[] nodes)
  {
    RoadNode temp = nodes[0];
    nodes[0] = nodes[1];
    nodes[1] = temp;
  }

  private static int matchedNodeCount(RoadNode[] nodes)
  {
    int count = 0;
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i].getMatch() != null)
        count++;
    }
    return count;
  }

}
