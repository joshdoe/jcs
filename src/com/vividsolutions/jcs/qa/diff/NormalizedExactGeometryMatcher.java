package com.vividsolutions.jcs.qa.diff;

import com.vividsolutions.jts.geom.*;

public class NormalizedExactGeometryMatcher
    implements DiffGeometryMatcher
{
  private Geometry queryGeom;

  public void setQueryGeometry(Geometry geom)
  {
    queryGeom = normalizedClone(geom);
  }
  public Geometry getQueryGeometry()
  {
    return queryGeom;
  }
  public boolean isMatch(Geometry geom)
  {
    return queryGeom.equalsExact(normalizedClone(geom));
  }

  public Geometry normalizedClone(Geometry geom)
  {
    Geometry geomNorm = (Geometry) geom.clone();
    geomNorm.normalize();
    return geomNorm;
  }
}
