package com.vividsolutions.jcs.conflate.coverage.snap;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jcs.geom.*;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jcs.feature.*;

public class SlowPointIndex {

  private List indexPts = new ArrayList();

  public SlowPointIndex()
  {
  }

  public void add(FeatureCollection fc)
  {
    add(fc.getFeatures());
  }

  public void add(List featureList)
  {
    for (Iterator i = featureList.iterator(); i.hasNext(); ) {
      Feature f = (Feature) i.next();
      Geometry g = f.getGeometry();
      add(g.getCoordinates());
    }
  }

  public void add(Coordinate[] pts)
  {
    for (int i = 0; i < pts.length; i++) {
      add(pts[i]);
    }
  }

  public void add(Coordinate pt)
  {
    indexPts.add(pt);
  }

  public List query(Envelope queryEnv)
  {
    List result = new ArrayList();
    for (Iterator i = indexPts.iterator(); i.hasNext(); ) {
      Coordinate p = (Coordinate) i.next();
      if (queryEnv.contains(p))
          result.add(p);
    }
    return result;
  }
}
