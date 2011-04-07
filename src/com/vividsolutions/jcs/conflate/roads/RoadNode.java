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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jcs.graph.*;
import com.vividsolutions.jump.geom.*;
import com.vividsolutions.jcs.conflate.roads.nodematch.NodeMatching;
import java.util.*;

/**
 * Models a node (intersection of two or more road edges) in a Road network.
 * Note that this includes intersections of degree two with both adjacent
 * edges being the same road.
 */
public class RoadNode
    extends Node
    implements Comparable
{

  public static boolean isNodeSetConsistent(RoadNode[] nodes1, RoadNode[] nodes2)
  {
    if (isNodeConsistent(nodes1[0].getMatch(), nodes2)
     && isNodeConsistent(nodes1[1].getMatch(), nodes2)) return true;
    return false;
  }

  public static boolean isNodeConsistent(RoadNode matchNode, RoadNode[] nodes)
  {
    if (matchNode == null) return true;
    if (matchNode == nodes[0]) return true;
    if (matchNode == nodes[1]) return true;
    return false;
  }

  class RoadNodeComparator implements Comparator
  {
    public int compare(Object o1, Object o2)
    {
      return ((RoadNode) o1).getCoordinate().compareTo(((RoadNode) o2).getCoordinate());
    }
  }

  private RoadNode matchNode = null;
  private double matchValue = Double.MIN_VALUE;
  private double maxAdjacentNodeDistance = -1;// cache this distance, since it's a bit pricey to compute
  private NodeMatching nodeMatching;
  private Matches matchList = new Matches(new RoadNodeComparator());

  public RoadNode(Coordinate pt)
  {
    super(pt);
  }
  public boolean hasMatch() { return matchNode != null; }
  public RoadNode getMatch()
  {
    return matchNode;
  }
  public void setMatch(RoadNode matchNode)
  {
    this.matchNode = matchNode;
    this.matchValue = Double.NEGATIVE_INFINITY;
  }
  public void setMatch(RoadNode matchNode, double matchValue)
  {
    this.matchNode = matchNode;
    this.matchValue = matchValue;
  }
  public void addMatch(RoadNode matchNode, double matchValue)
  {
    matchList.setValue(matchNode, matchValue);
  }
  public Matches getMatches() { return matchList; }

  public void clearMatch()
  {
    this.matchNode = null;
    this.matchValue = Double.NEGATIVE_INFINITY;
  }

  public void setMatchMaximum(RoadNode matchNode, double matchValue)
  {
    if (matchNode == null || matchValue > this.matchValue) {
      this.matchNode = matchNode;
      this.matchValue = matchValue;
    }
  }
  public NodeMatching getMatching()  {    return nodeMatching;  }
  public void setMatching(NodeMatching nodeMatching)  {    this.nodeMatching = nodeMatching;  }

  public double getMatchValue() { return matchValue; }

  public double getMaxAdjacentNodeDistance()
  {
    if (maxAdjacentNodeDistance < 0.0) {
      double maxDist = 0.0;
      for (Iterator i = getOutEdges().iterator(); i.hasNext(); ) {
        DirectedEdge de = (DirectedEdge) i.next();
        double dist = de.getFromNode().getCoordinate().distance(de.getToNode().getCoordinate());
        if (dist > maxDist) maxDist = dist;
      }
      maxAdjacentNodeDistance = maxDist;
    }
    return maxAdjacentNodeDistance;
  }

  public RoadEdge findClosestEdge(double angle, double[] resultAngleDiff)
  {
    DirectedEdge closestDE = null;
    double minAngleDiff = 0.0;
    for (Iterator i = deStar.iterator(); i.hasNext(); ) {
      DirectedEdge de = (DirectedEdge) i.next();
      double deAngle = de.getAngle();
      double angleDiff = Angle.diff(angle, deAngle);
      if (closestDE == null || angleDiff < minAngleDiff) {
        closestDE = de;
        minAngleDiff = angleDiff;
      }
    }
    resultAngleDiff[0] = minAngleDiff;
    return (RoadEdge) closestDE.getEdge();
  }

  public int compareTo(Object o2)
  {
    return getCoordinate().compareTo(((RoadNode) o2).getCoordinate());
  }

}
