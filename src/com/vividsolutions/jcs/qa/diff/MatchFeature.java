package com.vividsolutions.jcs.qa.diff;

import com.vividsolutions.jump.feature.*;

public class MatchFeature
{
  private Feature feature;
  private boolean isMatched;

  public MatchFeature(Feature feature)
  {
    this.feature = feature;
  }
  public Feature getFeature() { return feature; }
  public void setMatched(boolean isMatched) { this.isMatched = isMatched; }
  public boolean isMatched() { return isMatched; }


}