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