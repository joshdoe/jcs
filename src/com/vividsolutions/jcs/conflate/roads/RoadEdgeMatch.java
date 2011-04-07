package com.vividsolutions.jcs.conflate.roads;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jump.feature.*;

public class RoadEdgeMatch
    extends BasicFeature
{
  public static FeatureSchema matchSchema()
  {
    FeatureSchema featureSchema = new FeatureSchema();
    featureSchema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
    //featureSchema.addAttribute(MESG_ATTR_NAME, AttributeType.STRING);
    return featureSchema;
  }
  public static final FeatureSchema schema = matchSchema();

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
}