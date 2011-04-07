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

package com.vividsolutions.jcs.conflate.roads.nodematch;

/**
 * An algorithm class which computes match values
 * for various kinds of criteria.
 */
public class NodeMatcher {

  private static double angleMatchValue(double angleDiff)
  {
    double normAng = angleDiff / Math.PI;
    if (normAng > 1.0)
      normAng = 1.0;
    double value = (2.0 * (1.0 - normAng)) - 1.0;
    return value;
  }

  private MatchNode node1;
  private MatchNode node2;

  public NodeMatcher(MatchNode node1, MatchNode node2)
  {
    this.node1 = node1;
    this.node2 = node2;
    //node1.match(node2);
  }

  public double exactTopoMatchValue()
  {
    if (node1.getNumMatchedEdges() != node1.getNumEdges())
      return 0.0;
    if (node2.getNumMatchedEdges() != node2.getNumEdges())
      return 0.0;
    return 1.0;
  }

  public double minAngleMatchValue()
  {
    double minValue = 1.0;
    for (int i = 0; i < node1.getNumEdges(); i++) {
      MatchEdge me = node1.getEdge(i);
      if (me.isMatched()) {
        double value = angleMatchValue(me.getMatchAngle());
        if (value < minValue)
          minValue = value;
      }
    }
    return minValue;
  }

  public double fullMatchValue()
  {
    int totalEdges = node1.getNumEdges() + node2.getNumEdges();
    int totalMatchedEdges = node1.getNumMatchedEdges() + node2.getNumMatchedEdges();
    double matchedFrac = totalMatchedEdges / totalEdges;
    double edgeMatchScore = matchedFrac * matchedFrac;
    double matchValue = edgeMatchScore * minAngleMatchValue();
    return matchValue;
  }

}
