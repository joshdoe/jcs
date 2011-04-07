package com.vividsolutions.jcs.conflate.roads;

import com.vividsolutions.jts.geom.*;
import java.util.*;
import com.vividsolutions.jump.feature.*;

public class EdgeMatchReportBuilder {

  private static final int MAX_COLS = 5;

  private Collection refCols;
  private Collection subCols;
  private FeatureSchema schema;
  private RoadMatches matches;

  public EdgeMatchReportBuilder(FeatureCollection refFC,
                                FeatureCollection subFC,
                                RoadMatches matches)
  {
    this.matches = matches;
    this.schema = createFeatureSchema(refFC, subFC);
  }
  private FeatureSchema createFeatureSchema(FeatureCollection refFC,
                                FeatureCollection subFC)
  {
    refCols = getFirstNonGeometryCols(MAX_COLS, refFC.getFeatureSchema());
    subCols = getFirstNonGeometryCols(MAX_COLS, subFC.getFeatureSchema());
    return createFeatureSchema(refFC, refCols,
                               subFC, subCols);
  }

  private Collection getFirstNonGeometryCols(int n, FeatureSchema fs)
  {
    Collection colNames = new ArrayList();
    for (int i = 0; i < n; i++) {
      if (i < fs.getAttributeCount()) {
        if (fs.getAttributeType(i) != AttributeType.GEOMETRY) {
          colNames.add(fs.getAttributeName(i));
        }
      }
    }
    return colNames;
  }
  private FeatureSchema createFeatureSchema(FeatureCollection refFC, Collection refCols,
                                FeatureCollection subFC, Collection subCols)
  {
    FeatureSchema featureSchema = new FeatureSchema();
    featureSchema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
    addCols(refFC, refCols, featureSchema);
    addCols(subFC, subCols, featureSchema);
    return featureSchema;
  }

  private void addCols(FeatureCollection fc, Collection cols, FeatureSchema fs)
  {
    for (Iterator i = cols.iterator(); i.hasNext(); ) {
      String colName = (String) i.next();
      FeatureSchema fcSchema = fc.getFeatureSchema();
      fs.addAttribute(colName, fcSchema.getAttributeType(colName));
    }
  }

  public FeatureCollection createReportFC()
  {
    GeometryFactory geomFact = new GeometryFactory();
    FeatureDataset reportFC = new FeatureDataset(schema);
    for (Iterator i = matches.iterator(); i.hasNext(); ) {
      RoadEdgeMatch match = (RoadEdgeMatch) i.next();
      RoadEdge[] edge = match.getEdges();
      Geometry pairGeom = geomFact.createGeometryCollection(
          new Geometry[] { edge[0].getGeometry(), edge[1].getGeometry() } );
      Feature f = new BasicFeature(schema);
      f.setGeometry(pairGeom);

      copyCols(edge[0].getFeature(), refCols, f);
      copyCols(edge[1].getFeature(), subCols, f);
      reportFC.add(f);
    }
    return reportFC;
  }

  private void copyCols(Feature srcFeat, Collection colNames, Feature destFeat)
  {
    for (Iterator i = colNames.iterator(); i.hasNext(); ) {
      String colName = (String) i.next();
      destFeat.setAttribute(colName, srcFeat.getAttribute(colName));
    }
  }
}