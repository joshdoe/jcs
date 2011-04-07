package com.vividsolutions.jcs.conflate.roads;

import java.util.*;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jts.geom.Envelope;

public class RoadMatches
    extends FeatureDataset
{
  public RoadMatches()
  {
    super(RoadEdgeMatch.schema);
  }

}