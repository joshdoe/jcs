package com.vividsolutions.jcs.qa.diff;

import java.util.*;
import com.vividsolutions.jts.geom.*;

public class MatchGeometry {

  public static Collection splitGeometry(Geometry geom, boolean splitIntoComponents)
  {
    Collection list = new ArrayList();
    if (splitIntoComponents && geom instanceof GeometryCollection) {
      GeometryCollection geomColl = (GeometryCollection) geom;
      for (GeometryCollectionIterator gci = new GeometryCollectionIterator(geomColl); gci.hasNext(); ) {
        Geometry component = (Geometry) gci.next();
        if (! (component instanceof GeometryCollection)) {
          list.add(component);
        }
      }
    }
    else {
      // simply return input geometry in a list
      list.add(geom);
    }
    return list;
  }

  private MatchFeature feature;
  private Geometry geom;
  private MatchGeometry matchGeom = null;

  public MatchGeometry(MatchFeature feature, Geometry geom)
  {
    this.feature = feature;
    this.geom = geom;
  }
  public MatchFeature getFeature() { return feature; }
  public Geometry getGeometry() { return geom; }

  public MatchGeometry getMatch() { return matchGeom; }
  public void setMatch(MatchGeometry matchGeom) { this.matchGeom = matchGeom; }
  public boolean isMatched() { return matchGeom != null; }
}