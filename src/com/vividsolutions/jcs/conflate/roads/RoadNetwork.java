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

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jcs.graph.*;
import java.util.*;
import com.vividsolutions.jump.geom.EnvelopeUtil;

/**
 * Models a network of roads.
 */
public class RoadNetwork {

  private FeatureCollection edgesFC;
  RoadGraph graph = new RoadGraph();
  private STRtree nodeIndex;
  private STRtree edgeIndex;

  public RoadNetwork(FeatureCollection edgesFC)
  {
    this.edgesFC = edgesFC;
    buildGraph(edgesFC);
    index();
  }

  public List getUnmatchedEdges()
  {
    List unmatchedEdges = new ArrayList();
    for (Iterator i = graph.edgeIterator(); i.hasNext(); ) {
      RoadEdge re = (RoadEdge) i.next();
      if (re.getMatch() == null) {
        unmatchedEdges.add(re);
      }
    }
    return unmatchedEdges;
  }

  public FeatureCollection getUnmatchedEdgeFC()
  {
    FeatureDataset unmatchedFC = new FeatureDataset(edgesFC.getFeatureSchema());
    for (Iterator i = graph.edgeIterator(); i.hasNext(); ) {
      RoadEdge re = (RoadEdge) i.next();
      if (re.getMatch() == null) {
        unmatchedFC.addAll(re.getFeatures());
      }
    }
    return unmatchedFC;
  }

  public void index()
  {
    buildEdgeIndex(graph.edgeIterator());
    buildNodeIndex(graph.nodeIterator());
  }
  private void buildGraph(FeatureCollection edges)
  {
    for (Iterator i = edges.iterator(); i.hasNext(); ) {
      Feature f = (Feature) i.next();
      RoadEdge edge = graph.addEdge(f);
    }
  }

  private void buildEdgeIndex(Iterator edgeIterator)
  {
    edgeIndex = new STRtree();
    while (edgeIterator.hasNext() ) {
      RoadEdge edge = (RoadEdge) edgeIterator.next();
      edgeIndex.insert(edge.getGeometry().getEnvelopeInternal(), edge);
    }
  }

  public Iterator edgeIterator() { return graph.edgeIterator(); }
  public List getEdges() { return graph.getEdges(); }

  private void buildNodeIndex(Iterator nodeIterator)
  {
    nodeIndex = new STRtree();
    while (nodeIterator.hasNext ()) {
      Node node = (Node) nodeIterator.next();
      Coordinate pt = node.getCoordinate();
      nodeIndex.insert(new Envelope(pt), node);
    }
  }

  public List query(RoadEdge edge, double distance)
  {
    Envelope queryEnv = EnvelopeUtil.bufferByFraction(edge.getGeometry().getEnvelopeInternal(), distance);
    return edgeIndex.query(queryEnv);
  }

  public RoadNode closestNodeWithinDistance(Coordinate pt, double maxDist)
  {
    List nodes = nodesWithinDistance(pt, maxDist);

    RoadNode closest = null;
    double closestDist = 0.0;

    for (Iterator i = nodes.iterator(); i.hasNext(); ) {
      RoadNode node = (RoadNode) i.next();
      double dist = node.getCoordinate().distance(pt);
      if (closest == null || dist < closestDist) {
        closest = node;
        closestDist = dist;
      }
    }
    return closest;
  }

  public List nodesWithinDistance(Coordinate pt, double dist)
  {
    Envelope queryEnv = new Envelope(pt.x - dist, pt.x + dist, pt.y - dist, pt.y + dist);
    return nodeIndex.query(queryEnv);
  }
}
