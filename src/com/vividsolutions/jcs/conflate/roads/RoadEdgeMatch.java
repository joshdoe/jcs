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

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jump.feature.*;

public class RoadEdgeMatch
    extends BasicFeature
{
  public static FeatureSchema createFeatureSchema()
  {
    FeatureSchema featureSchema = new FeatureSchema();
    featureSchema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
    return featureSchema;
  }
  public static final FeatureSchema schema = createFeatureSchema();

  private static final EdgeMatchIndFactory edgeMatchIndFact = new EdgeMatchIndFactory();

  private RoadEdge[] edge = new RoadEdge[2];
  private Geometry geom;

  public RoadEdgeMatch(RoadEdge edge0, RoadEdge edge1)
  {
    super(schema);
    edge[0] = edge0;
    edge[1] = edge1;
    Geometry g = edgeMatchIndFact.getIndicator(
        (LineString) edge[0].getGeometry(),
        (LineString) edge[1].getGeometry());
    setGeometry(g);
  }

  public void clearMatch()
  {
    edge[0].clearMatch();
    edge[1].clearMatch();
  }

  public RoadEdge[] getEdges() {    return edge;  }
}
