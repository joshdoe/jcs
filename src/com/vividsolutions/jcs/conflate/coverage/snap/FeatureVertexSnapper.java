package com.vividsolutions.jcs.conflate.coverage.snap;

import java.util.*;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jump.geom.*;
import com.vividsolutions.jump.task.*;

public class FeatureVertexSnapper {

  private FeatureCollection refFC;
  private FeatureCollection subjectFC;
  private FeatureCollection adjustedFC;
  private double distanceTolerance;
  private FeatureUpdateRecorder updates =  new FeatureUpdateRecorder();
  private int updateCount = 0;
  private GeometryFactory fact = new GeometryFactory();
  private List snappedVertexList = new ArrayList();

  public FeatureVertexSnapper(FeatureCollection refFC,
                              FeatureCollection subjectFC,
                              double distanceTolerance)
  {
    this.refFC = refFC;
    this.subjectFC = subjectFC;
    this.distanceTolerance = distanceTolerance;
  }

  public int getUpdateCount()
  {
    return updateCount;
  }

  public FeatureCollection process(TaskMonitor monitor)
  {
    adjustedFC = new FeatureDataset(subjectFC.getFeatureSchema());
    FeatureCollection indexFC = new IndexedFeatureCollection(refFC);
    int totalSegments = subjectFC.size();
    int count = 0;
    for (Iterator i = subjectFC.iterator(); i.hasNext(); ) {
      monitor.report(++count, totalSegments, "features");

      Feature f = (Feature) i.next();
      Envelope fEnv = f.getGeometry().getEnvelopeInternal();
      Envelope searchEnv = EnvelopeUtil.expand(fEnv, distanceTolerance);
      List closeFeatures = indexFC.query(searchEnv);

      snapFeature(f, closeFeatures);
    }
    updateCount = updates.getCount();

    return updates.applyUpdates(subjectFC);
  }
  public FeatureCollection getAdjustedFeatures()  { return adjustedFC; }

  public FeatureCollection getAdjustmentIndicators()
  {
    GeometryFactory fact = new GeometryFactory();
    List indicatorLineList = new ArrayList();
    for (Iterator i = snappedVertexList.iterator(); i.hasNext(); ) {
      Coordinate[] origCoord = (Coordinate[]) i.next();
      Coordinate[] lineSeg = new Coordinate[] {
        new Coordinate(origCoord[0]),
        new Coordinate(origCoord[1]),
        };
      Geometry line = fact.createLineString(lineSeg);
      indicatorLineList.add(line);
    }
    return FeatureDatasetFactory.createFromGeometryWithLength(indicatorLineList, "LENGTH");
  }

  private void snapFeature(Feature f, List closeFeatures)
  {
    SlowPointIndex ptIndex = new SlowPointIndex();
    ptIndex.add(closeFeatures);
    CoordinateSnapper coordSnapper = new CoordinateSnapper(ptIndex);
    Geometry geom = f.getGeometry();
    GeometryVertexSnapper snapper = new GeometryVertexSnapper(geom, coordSnapper, distanceTolerance);
    Geometry newGeom = snapper.getResult();
    if (snapper.isModified()) {
      snappedVertexList.addAll(snapper.getSnappedVertices());

      // don't update geometry if it's not valid
      if (newGeom.isValid()) {
        Feature newFeat = f.clone(false);
        newFeat.setGeometry(newGeom);
        // record this feature as an update to the original
        updates.update(f, newFeat);
        adjustedFC.add(f);
      }
    }
  }

}
