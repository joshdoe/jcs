package com.vividsolutions.jcs.qa.diff;

import com.vividsolutions.jts.geom.*;

public interface DiffGeometryMatcher
{
  public void setQueryGeometry(Geometry geom);
  public Geometry getQueryGeometry();
  public boolean isMatch(Geometry geom);
}
