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
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jcs.graph.*;
import com.vividsolutions.jts.util.Assert;
import java.util.*;
/**
 */
public class RoadEdge
    extends Edge
{

  private Collection features;
  //private Feature f;
  private Geometry geom;
  private String name;
  private RoadEdge match = null;
  private double matchDistance = 0.0;
  private RoadNode[] nodes;
  private Matches matchList = new Matches(new RoadEdgeComparator(), true);
  //private boolean isMerged = false;

  public RoadEdge(Feature f)
  {
    features = new ArrayList();
    features.add(f);
    geom = f.getGeometry();
  }

  public RoadEdge(Geometry geom, Feature f)
  {
    features = new ArrayList();
    features.add(f);
    this.geom = geom;
  }

  public RoadEdge(Geometry geom, Collection features)
  {
    this.features = features;
    this.geom = geom;
  }

  public Feature getFeature() { return (Feature) features.iterator().next(); }
  public Collection getFeatures() { return features; }
  public Geometry getGeometry() { return geom; }
  /*
  public boolean isMerged() { return isMerged; }
  public void setMerged(boolean isMerged) { this.isMerged = isMerged; }
  */
  public void setDirectedEdges(DirectedEdge de0, DirectedEdge de1)
  {
    super.setDirectedEdges(de0, de1);
    nodes = new RoadNode[] {
      (RoadNode) dirEdge[0].getFromNode(),
      (RoadNode) dirEdge[1].getFromNode() };
  }


  public RoadNode[] getNodes()
  {
    return nodes;
  }
  public RoadNode getOtherNode(RoadNode node)
  {
    if (nodes[0] == node) return nodes[1];
    if (nodes[1] == node) return nodes[0];
    Assert.shouldNeverReachHere("argument is not a node of this edge");
    return null;
  }
  public boolean hasNode(Node n) { return (nodes[0] == n || nodes[1] == n); }


  public String getName() { return name; }
  public void setName(String name)
  {
    this.name = name;
  }
  public boolean hasMatch() { return match != null; }
  public RoadEdge getMatch()  {    return match;  }

  public void setMutualMatch(RoadEdge matchEdge)
  {
    setMatch(matchEdge);
    matchEdge.setMatch(this);
  }

  public void setMatch(RoadEdge matchEdge)
  {
    match = matchEdge;
    matchDistance = 0.0;
  }

  public void setMatch(RoadEdge matchEdge, double matchDistance)
  {
    if (match == null || matchDistance < this.matchDistance) {
      match = matchEdge;
      this.matchDistance= matchDistance;
    }
  }
  public void addMatch(RoadEdge matchEdge, double matchDistance)
  {
    matchList.setValue(matchEdge, matchDistance);
  }
  public Matches getMatches() { return matchList; }

  public double getMatchDistance() { return matchDistance; }

  public void clearMatch()
  {
    match = null;
    matchDistance = Double.NEGATIVE_INFINITY;
  }
  public String toString()
  {
    return geom.toString();
  }

  public class RoadEdgeComparator implements Comparator
  {
    public int compare(Object o1, Object o2)
    {
      return ((RoadEdge) o1).getGeometry().compareTo(((RoadEdge) o2).getGeometry());
    }
  }


}
