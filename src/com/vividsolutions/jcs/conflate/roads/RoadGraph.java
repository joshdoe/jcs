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
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jcs.graph.*;
import com.vividsolutions.jump.feature.*;

public class RoadGraph
    extends PlanarGraph
{

  public RoadGraph() {
  }

  public RoadEdge addEdge(LineString line, Feature f)
  {
    RoadEdge edge = new RoadEdge(line, f);
    addDirectedEdges(edge, line);
    add(edge);
    return edge;
  }

  public RoadEdge addEdge(Feature f)
  {
    RoadEdge edge = new RoadEdge(f);
    LineString line = (LineString) f.getGeometry();
    addDirectedEdges(edge, line);
    add(edge);
    return edge;
  }

  private void addDirectedEdges(RoadEdge edge, LineString line)
  {
    Coordinate startPt = line.getCoordinateN(0);
    Coordinate endPt = line.getCoordinateN(line.getNumPoints() - 1);
    RoadNode nStart = getNode(startPt);
    RoadNode nEnd = getNode(endPt);
    DirectedEdge de0 = new RoadDirectedEdge(nStart, nEnd, line.getCoordinateN(1), true);
    DirectedEdge de1 = new RoadDirectedEdge(nEnd, nStart, line.getCoordinateN(line.getNumPoints() - 2), false);
    edge.setDirectedEdges(de0, de1);
  }

  private RoadNode getNode(Coordinate pt)
  {
    RoadNode node = (RoadNode) findNode(pt);
    if (node == null) {
      // use DEStar subclass here if used
      node = new RoadNode(pt);
      // ensure node is only added once to graph
      add(node);
    }
    return node;
  }
}
