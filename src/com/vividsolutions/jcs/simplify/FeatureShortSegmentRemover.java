package com.vividsolutions.jcs.simplify;

import java.util.*;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jump.task.*;

public class FeatureShortSegmentRemover {

  private FeatureCollection features;
  private FeatureCollection adjustedFC;
  private double minLength;
  private double maxDisplacement;
  private int updateCount = 0;
  private int segmentsRemovedCount = 0;

  public FeatureShortSegmentRemover(FeatureCollection features, double minLength, double maxDisplacement)
  {
    this.features = features;
    this.minLength = minLength;
    this.maxDisplacement = maxDisplacement;
  }

  public int getUpdateCount()
  {
    return updateCount;
  }

  public int getSegmentsRemovedCount() { return segmentsRemovedCount; }

  public FeatureCollection process(TaskMonitor monitor)
  {
    FeatureUpdateRecorder updates =  new FeatureUpdateRecorder();
    int totalSegments = features.size();
    int count = 0;
    for (Iterator i = features.iterator(); i.hasNext(); ) {
      monitor.report(++count, totalSegments, "features");
      Feature f = (Feature) i.next();
      Geometry geom = f.getGeometry();
      GeometryShortSegmentRemover remover = new GeometryShortSegmentRemover(geom, minLength, maxDisplacement);
      Geometry newGeom = remover.getResult();
      if (remover.isModified()) {
        // don't update geometry if it's not valid
        if (newGeom.isValid()) {
          Feature newFeat = f.clone(false);
          newFeat.setGeometry(newGeom);
          // record this feature as an update to the original
          updates.update(f, newFeat);
          segmentsRemovedCount += remover.getSegmentsRemovedCount();
        }
      }
    }
    updateCount = updates.getCount();

    return updates.applyUpdates(features);
  }
}
