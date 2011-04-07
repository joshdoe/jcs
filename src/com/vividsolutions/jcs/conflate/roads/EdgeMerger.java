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

import com.vividsolutions.jcs.graph.*;
import java.util.*;

public class EdgeMerger {


  public static boolean isEdgeWithSingle2Node(Edge edge)
  {
    int count2Node = 0;

    for (int i = 0; i <= 1; i++) {
      DirectedEdge de = edge.getDirEdge(i);
      Node node = de.getFromNode();
      if (node.getDegree() == 2) count2Node++;
    }
    return count2Node == 1;
  }

  public static List findEdgesWithSingle2Node(Iterator edgeIt)
  {
    List edges = new ArrayList();
    while (edgeIt.hasNext()) {
      Edge edge = (Edge) edgeIt.next();
      if (isEdgeWithSingle2Node(edge))
          edges.add(edge);
    }
    return edges;
  }

  private PlanarGraph graph;
  private Set mergedEdges = new HashSet();

  public EdgeMerger(PlanarGraph graph)
  {
    this.graph = graph;
  }

  public boolean isMerged(Edge edge)
  {
    return mergedEdges.contains(edge);
  }
  /**
   * Compute the merged edges in the graph
   *
   * @return a List of Lists of DirectedEdges, each in the order of the merged edge
   */
  public List getMergedPaths()
  {
    List edgesSingle2Node = findEdgesWithSingle2Node(graph.edgeIterator());

    List mergedEdgePaths = new ArrayList();
    for (Iterator i = edgesSingle2Node.iterator(); i.hasNext(); ) {
      RoadEdge edge = (RoadEdge) i.next();
      if (! isMerged(edge)) {
        mergedEdgePaths.add(findMergedPath(edge));
      }
    }
    return mergedEdgePaths;
  }

  /**
   * Finds the list of DirectedEdges which lead from the given edge via nodes
   * of degree 2.  In order to make this algorithm
   * unambiguous, the edge is assumed to have only a single node of degree 2.
   * @param edge
   * @return a list of DirectedEdges
   */
  private List findMergedPath(Edge edge)
  {
    Node startNode = edge.getDirEdge(0).getFromNode();
    if (startNode.getDegree() == 2)
      startNode = edge.getDirEdge(1).getFromNode();

    List edgePath = new ArrayList();
    DirectedEdge currDirEdge = edge.getDirEdge(startNode);
    do {
      edgePath.add(currDirEdge);
      mergedEdges.add(currDirEdge.getEdge());

      Node nextNode = currDirEdge.getToNode();
      if (nextNode.getDegree() == 2) {
        currDirEdge = nextNode.getOutEdges().getNextEdge(currDirEdge.getSym());
      }
      else {
        currDirEdge = null;// indicates that there is no next edge
      }
      // continue as long as there is a next edge which has not been visited yet
    } while (currDirEdge != null && ! isMerged(currDirEdge.getEdge()));

    return edgePath;
  }

}
