package com.vividsolutions.jcs.qa.diff;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;

public class DiffGeometryIndex
{

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

  private SpatialIndex index;
  private DiffGeometryMatcher diffMatcher;
  private boolean splitIntoComponents;
  private Collection featureList;

  public DiffGeometryIndex(
      FeatureCollection fc,
      DiffGeometryMatcher diffMatcher,
      boolean splitIntoComponents)
  {
    this.diffMatcher = diffMatcher;
    this.splitIntoComponents = splitIntoComponents;
    buildIndex(fc);
  }

  public boolean hasMatch(Geometry testGeom)
  {
    diffMatcher.setQueryGeometry(testGeom);

    List closeFeatList = index.query(diffMatcher.getQueryGeometry().getEnvelopeInternal());
    for (Iterator j = closeFeatList.iterator(); j.hasNext(); ) {
      FeatureGeometry closeFeat = (FeatureGeometry) j.next();

      if (diffMatcher.isMatch(closeFeat.getGeometry())) {
        closeFeat.setMatched(true);
        return true;
      }
    }
    return false;
  }

  private void buildIndex(FeatureCollection fc)
  {
    featureList = new ArrayList();
    index = new STRtree();
    for (Iterator i = fc.iterator(); i.hasNext(); )
    {
      Feature feat = (Feature) i.next();
      Geometry geom = feat.getGeometry();
      Collection list = splitGeometry(geom, splitIntoComponents);
      for (Iterator j = list.iterator(); j.hasNext(); ) {
        Geometry g = (Geometry) j.next();
        FeatureGeometry featGeom = new FeatureGeometry(feat, g);
        index.insert(featGeom.getGeometry().getEnvelopeInternal(), featGeom);
        featureList.add(featGeom);
      }
    }
  }

  public Collection getUnmatchedFeatures()
  {
    Set unmatchedFeatureSet = new TreeSet(new FeatureUtil.IDComparator());
    for (Iterator i = featureList.iterator(); i.hasNext(); ) {
      FeatureGeometry fg = (FeatureGeometry) i.next();
      if (! fg.isMatched()) {
        unmatchedFeatureSet.add(fg.getFeature());
      }
    }
    return unmatchedFeatureSet;
  }

  public class FeatureGeometry
  {
    private Feature feat;
    private Geometry geom;
    private boolean isMatched = false;

    public FeatureGeometry(Feature feat, Geometry geom)
    {
      this.feat = feat;
      this.geom = geom;
    }
    public Feature getFeature() { return feat; }
    public Geometry getGeometry() { return geom; }

    public void setMatched(boolean isMatched) { this.isMatched = isMatched; }
    public boolean isMatched() { return isMatched; }
  }

}