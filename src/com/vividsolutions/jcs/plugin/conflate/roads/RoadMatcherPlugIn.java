

/*
 * The JCS Conflation Suite (JCS) is a library of Java classes that
 * can be used to build automated or semi-automated conflation solutions.
 *
 * Copyright (C) 2003 Vivid Solutions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package com.vividsolutions.jcs.plugin.conflate.roads;

import java.awt.Color;
import com.vividsolutions.jcs.conflate.roads.*;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jump.util.feature.FeatureStatistics;
import com.vividsolutions.jump.workbench.*;
import com.vividsolutions.jump.workbench.model.*;
import com.vividsolutions.jump.workbench.plugin.*;
import com.vividsolutions.jump.workbench.ui.*;
import com.vividsolutions.jump.workbench.ui.plugin.*;
import com.vividsolutions.jts.util.*;
import com.vividsolutions.jump.task.*;
import com.vividsolutions.jump.workbench.ui.renderer.style.*;
import javax.swing.*;

public class RoadMatcherPlugIn extends ThreadedBasePlugIn {

  private final static String REF_LAYER = "Reference Layer";
  private final static String SUB_LAYER = "Subject Layer";
  private final static String REF_INSERT_VERT = "REF_INSERT_VERT";
  // hide this until it is implemented
  //private final static String SUB_DELETE_VERT = "SUB_DELETE_VERT";
  private final static String DIST_TOL = "Distance Tolerance";
  private final static String ANG_TOL = "Angle Tolerance";

  private Layer refLyr, subLyr;

  public RoadMatcherPlugIn() { }

  public void initialize(PlugInContext context) throws Exception {
    context.getFeatureInstaller().addMainMenuItem(this, new String[] {"Roads"},
        "Test Road Matcher...", false, null, new MultiEnableCheck()
        .add(context.getCheckFactory().createWindowWithLayerViewPanelMustBeActiveCheck())
        .add(context.getCheckFactory().createAtLeastNLayersMustExistCheck(2)));
  }

  public boolean execute(PlugInContext context) throws Exception {
    MultiInputDialog dialog = new MultiInputDialog(
        context.getWorkbenchFrame(), "Road Network Matcher", true);
    setDialogValues(dialog, context);
    GUIUtil.centreOnWindow(dialog);
    dialog.setVisible(true);
    if (!dialog.wasOKPressed()) { return false; }
    getDialogValues(dialog);
    return true;
  }

  public void run(TaskMonitor monitor, PlugInContext context)
       throws Exception
  {
    monitor.allowCancellationRequests();

    RoadMatcher roadMatcher = new RoadMatcher(
        refLyr.getFeatureCollectionWrapper(),
        subLyr.getFeatureCollectionWrapper(), monitor);

    monitor.report("Matching Roads...");
    //roadMatcher.computeClosestSimilarTopoNodeMatches();
    //roadMatcher.computeMutualNodeMatches();
    //roadMatcher.computeEdgeMatchesFromNodes();
    roadMatcher.match();
    createLayers(context, roadMatcher);
  }

  public static final Color GOLD = new Color(255, 192, 0, 150);

  void createLayers(PlugInContext context, RoadMatcher roadMatcher)
         throws Exception
  {
    FeatureCollection edgeIndFC = roadMatcher.getEdgeMatchIndicators();
    Layer lyr = context.addLayer(
        StandardCategoryNames.QA,
        "Edge Match Ind",
        edgeIndFC);
    lyr.setSynchronizingLineColor(false);
    lyr.getBasicStyle().setFillColor(GOLD);
    lyr.getBasicStyle().setRenderingFill(true);
    lyr.getBasicStyle().setLineColor(GOLD);
    lyr.getBasicStyle().setLineWidth(2);
    lyr.getBasicStyle().setAlpha(150);
    lyr.fireAppearanceChanged();
    lyr.setDescription("Edge Match Indicators for " + subLyr.getName());

    FeatureCollection matchedNodeIndFC = roadMatcher.getMatchedEdgeNodeVectors();
    Layer lyr2 = context.addLayer(
        StandardCategoryNames.QA,
        "Node Matches",
        matchedNodeIndFC);
    LayerStyleUtil.setLinearStyle(lyr2, Color.blue, 2, 0);
    lyr2.addStyle(new ArrowLineStringEndpointStyle.NarrowSolidEnd());
    lyr2.fireAppearanceChanged();
    lyr2.setDescription("Node Matches for " + subLyr.getName());

    FeatureCollection unmatchedRefFC = roadMatcher.getNetwork(0).getEdgesFC();
    Layer lyr3 = context.addLayer(
        StandardCategoryNames.QA,
        "Unmatched Ref",
        unmatchedRefFC);
    LayerStyleUtil.setLinearStyle(lyr3, Color.green, 4, 0);
    lyr3.setDescription("Unmatched Reference Edges");

    FeatureCollection unmatchedSubFC = roadMatcher.getNetwork(1).getEdgesFC();
    Layer lyr4 = context.addLayer(
        StandardCategoryNames.QA,
        "Unmatched Sub",
        unmatchedSubFC);
    LayerStyleUtil.setLinearStyle(lyr4, Color.red, 4, 0);
    lyr4.setDescription("Unmatched Subject Edges");

    createOutput(context, edgeIndFC, matchedNodeIndFC);

  }

  private void createOutput(PlugInContext context,
                            FeatureCollection edgeIndFC,
                            FeatureCollection matchedNodeIndFC)
         throws Exception
  {
    context.getOutputFrame().createNewDocument();
    context.getOutputFrame().addHeader(1,
        "Road Network Matching");
    context.getOutputFrame().addField(
        "Reference Layer: ", refLyr.getName() );
    context.getOutputFrame().addField(
        "Subject Layer: ", subLyr.getName() );

    int refEdgeCount = refLyr.getFeatureCollectionWrapper().size();
    int subjectEdgeCount = subLyr.getFeatureCollectionWrapper().size();
    int matchedCount = edgeIndFC.size();
    int refMatchPercent = (int) (100.0 * matchedCount / (double) refEdgeCount);
    int subMatchPercent = (int) (100.0 * matchedCount / (double) subjectEdgeCount);

    context.getOutputFrame().addText(" ");
    context.getOutputFrame().addField("Reference # Edges: ", refEdgeCount + " ");
    context.getOutputFrame().addField("Subject # Edges: ", subjectEdgeCount + " ");

    context.getOutputFrame().addText(" ");
    context.getOutputFrame().addField(
        "# Edges matched: ", "" + matchedCount);
    context.getOutputFrame().addField("Reference Matched Edges: ", refMatchPercent + " %");
    context.getOutputFrame().addField("Subject Matched Edges: ", subMatchPercent + " %");

    /*
    double[] minMax = FeatureStatistics.minMaxValue(adjustmentIndFC, "LENGTH");
    context.getOutputFrame().addField(
        "Min Adjustment Size: ", "" + minMax[0]);
    context.getOutputFrame().addField(
        "Max Adjustment Size: ", "" + minMax[1]);
    */
  }

  private void setDialogValues(MultiInputDialog dialog, PlugInContext context) {
    dialog.setTitle("Road Network Matching");
    //dialog.setSideBarImage(new ImageIcon(getClass().getResource("BoundaryMatch.gif")));
    dialog.setSideBarDescription("Matches one road network to another.  "
    );
    //Set initial layer values to the first and second layers in the layer list.
    //In #initialize we've already checked that the number of layers >= 2. [Jon Aquino]
    dialog.addLayerComboBox(REF_LAYER, context.getLayerManager().getLayer(0), "The Reference layer is not changed",
                         context.getLayerManager());
    dialog.addLayerComboBox(SUB_LAYER, context.getLayerManager().getLayer(1), "The Subject layer is matched to the Reference layer",context.getLayerManager());
  }

  private void getDialogValues(MultiInputDialog dialog) {
    refLyr = dialog.getLayer(REF_LAYER);
    subLyr = dialog.getLayer(SUB_LAYER);
    //param.insertRefVertices = dialog.getBoolean(REF_INSERT_VERT);
    //bmParam.deleteSubVertices = dialog.getBoolean(SUB_DELETE_VERT);
  }
}
