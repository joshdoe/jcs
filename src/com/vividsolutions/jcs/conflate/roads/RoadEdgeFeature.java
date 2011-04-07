package com.vividsolutions.jcs.conflate.roads;

import com.vividsolutions.jump.feature.*;

public class RoadEdgeFeature extends BasicFeature
{
  public static final String ATTR_UNMATCHED = "unmatched";

  //==============  Feature methods
  private static FeatureSchema createFeatureSchema()
  {
    FeatureSchema featureSchema = new FeatureSchema();
    featureSchema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
    featureSchema.addAttribute(ATTR_UNMATCHED, AttributeType.INTEGER);
    return featureSchema;
  }
  public static final FeatureSchema schema = createFeatureSchema();

  private RoadEdge edge;

  public RoadEdgeFeature(RoadEdge edge) {
    super(schema);
    this.edge = edge;
    setMatched(false);
  }

  public void setMatched(boolean isMatched) {
    setAttribute(1, new Integer(isMatched ? 1 : 0));
  }

  public boolean hasMatch()
  {
    return ((Integer) getAttribute(1)).intValue() == 1;
  }

  public RoadEdge getEdge() { return edge; }
}