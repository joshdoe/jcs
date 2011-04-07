package com.vividsolutions.jcs.feature;

import java.util.Iterator;

import com.vividsolutions.jump.feature.*;

public class FeatureCollectionGeometryIterator
    implements Iterator
{
  private Iterator it;

  public FeatureCollectionGeometryIterator(FeatureCollection fc)
  {
    it = fc.iterator();
  }

  public boolean hasNext()
  {
    return it.hasNext();
  }

  public Object next()
  {
    Feature f = (Feature) it.next();
    return f.getGeometry();
  }

  public void remove()
  {
    it.remove();
  }


}
