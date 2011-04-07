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
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jcs.graph.*;
import com.vividsolutions.jump.feature.*;


public class RoadEdgeMerger {

  /**
   * Merges the linestrings from a list of DirectedEdges
   *
   * @param dirEdges a list of {@link DirectedEdges}
   * @return the LineString that contains the same points as the input
   */
  private static LineString mergeLineStrings(List dirEdges)
  {
    GeometryFactory fact = new GeometryFactory();
    CoordinateList coordList = new CoordinateList();

    for (Iterator i = dirEdges.iterator(); i.hasNext(); ) {
      DirectedEdge de = (DirectedEdge) i.next();
      RoadEdge edge = (RoadEdge) de.getEdge();
      Coordinate[] pts = edge.getGeometry().getCoordinates();
      coordList.add(pts, false, de.getEdgeDirection());
    }
    return fact.createLineString(coordList.toCoordinateArray());
  }

  public RoadEdgeMerger() {
  }

  public void merge(RoadNetwork network)
  {
    EdgeMerger edgeMerger = new EdgeMerger(network.graph);
    List mergedEdgePaths = edgeMerger.getMergedPaths();

    for (Iterator i = mergedEdgePaths.iterator(); i.hasNext(); ) {
      List path = (List) i.next();
      replace(path, network);
    }
    // reindex road network to include new edges, remove merged edges
    network.index();
  }

  private static List getFeatureList(List path)
  {
    List featList = new ArrayList();
    for (Iterator i = path.iterator(); i.hasNext(); ) {
      DirectedEdge de = (DirectedEdge) i.next();
      RoadEdge edge = (RoadEdge) de.getEdge();
      featList.add(edge.getFeature());
    }
    return featList;
  }
  private void replace(List path, RoadNetwork network)
  {
    LineString newLine = mergeLineStrings(path);
    //System.out.println(newLine);

    // remove edges in path from network
    for (Iterator i = path.iterator(); i.hasNext(); ) {
      DirectedEdge de = (DirectedEdge) i.next();
      RoadEdge edge = (RoadEdge) de.getEdge();
      network.remove(edge);
    }

    List featList = getFeatureList(path);
    //TODO: pass in entire list of features here
    Feature firstFeat = (Feature) featList.get(0);
    network.addEdge(newLine, featList);
  }
}
