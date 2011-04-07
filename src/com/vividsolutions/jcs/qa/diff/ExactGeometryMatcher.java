package com.vividsolutions.jcs.qa.diff;

import com.vividsolutions.jts.geom.*;

public class ExactGeometryMatcher
    implements DiffGeometryMatcher
{
  private Geometry queryGeom;

  public void setQueryGeometry(Geometry geom)
  {
    queryGeom = geom;
  }
  public Geometry getQueryGeometry()
  {
    return queryGeom;
  }
  public boolean isMatch(Geometry geom)
  {
    return queryGeom.equalsExact(geom);
  }
}
