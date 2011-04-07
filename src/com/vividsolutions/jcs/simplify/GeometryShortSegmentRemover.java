package com.vividsolutions.jcs.simplify;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jcs.geom.*;

public class GeometryShortSegmentRemover {

  private Geometry geom;
  private double minLength;
  private double maxDisplacement;
  private boolean isModified = false;
  private int segmentsRemovedCount = 0;

  public GeometryShortSegmentRemover(Geometry geom, double minLength, double maxDisplacement)
  {
    this.geom = geom;
    this.minLength = minLength;
    this.maxDisplacement = maxDisplacement;
  }

  public Geometry getResult()
  {
    GeometryEditor geomEdit = new GeometryEditor();
    /**
     * GeometryEditor always creates a new geometry even if the original one wasn't modified.
     * Explicitly check for modifications and return the original if no mods were made
     */
    Geometry newGeom = geomEdit.edit(geom, new ShortSegmentRemoverCoordinateOperation());
    if (! isModified)
      return geom;
    return newGeom;
  }

  public boolean isModified() { return isModified; }

  public int getSegmentsRemovedCount() { return segmentsRemovedCount; }

  private class ShortSegmentRemoverCoordinateOperation
      extends GeometryEditor.CoordinateOperation
  {
    public Coordinate[] edit(Coordinate[] coordinates, Geometry geom, GeometryEditor editor)
    {
      boolean isRing = geom instanceof LinearRing;

      ShortSegmentRemover shortSegRemover = new ShortSegmentRemover(coordinates, isRing, minLength, maxDisplacement);
      if (shortSegRemover.isModified()) {
        isModified = true;
        segmentsRemovedCount += shortSegRemover.getSegmentsRemovedCount();
      }

      return shortSegRemover.getUpdatedCoordinates();
    }
  }
}
