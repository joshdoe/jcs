package com.vividsolutions.jcs.conflate.coverage.snap;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jcs.geom.*;

public class GeometryVertexSnapper {

  private Geometry geom;
  private CoordinateSnapper snapper;
  private double distanceTolerance;
  private GeometryEditor geomEdit = new GeometryEditor();
  private boolean isModified = false;
  private List snappedVertexList = new ArrayList();

  public GeometryVertexSnapper(Geometry geom, CoordinateSnapper snapper, double distanceTolerance)
  {
    this.geom = geom;
    this.snapper = snapper;
    this.distanceTolerance = distanceTolerance;
  }

  public boolean isModified() { return isModified; }

  /**
   * Get the list of adjustments that were made
   * @return a list of Coordinate[2]
   */
  public List getSnappedVertices() { return snappedVertexList; }

  public Geometry getResult()
  {
    return geomEdit.edit(geom, new VertexSnapperCoordinateOperation());
  }

  private void addAdjustedVertex(Coordinate original, Coordinate adjusted)
  {
    isModified = true;
    snappedVertexList.add(new Coordinate[] {original, adjusted } );
  }

  private class VertexSnapperCoordinateOperation
    extends GeometryEditor.CoordinateOperation
  {
    public Coordinate[] edit(Coordinate[] coordinates, Geometry geom, GeometryEditor editor)
    {
      CoordinateList noRepeatedCoordList = new CoordinateList();
      for (int i = 0; i < coordinates.length; i++) {
        Coordinate snappedCoord = snapper.snap(coordinates[i], distanceTolerance);
        // check for both no coordinate found and an identical coordinate found
        if (snappedCoord != coordinates[i] && ! snappedCoord.equals(coordinates[i])) {
          addAdjustedVertex(coordinates[i], snappedCoord);
        }
        noRepeatedCoordList.add(new Coordinate(snappedCoord), false);
      }
      // remove repeated points
      Coordinate[] noRepeatedCoord = noRepeatedCoordList.toCoordinateArray();

      /**
       * Check to see if the removal of repeated points
       * collapsed the coordinate List to an invalid length
       * for the type of the parent geometry.
       * If this is the case, return the orginal coordinate list.
       * Note that the returned geometry will still be invalid, since it
       * has fewer unique coordinates than required. This check simply
       * ensures that the Geometry constructors won't fail.
       * It is not necessary to check for Point collapses, since the coordinate list can
       * never collapse to less than one point
       */
      if (geom instanceof LinearRing && noRepeatedCoord.length <= 3) return coordinates;
      if (geom instanceof LineString && noRepeatedCoord.length <= 1) return coordinates;

      return noRepeatedCoord;
    }
  }

}
