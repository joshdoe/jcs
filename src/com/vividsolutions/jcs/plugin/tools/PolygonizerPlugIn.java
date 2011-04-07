

/*
 * The Java Conflation Suite (JCS) is a library of Java classes that
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

package com.vividsolutions.jcs.plugin.tools;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;

import java.util.*;
import com.vividsolutions.jcs.polygonize.Polygonizer;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.model.*;
import com.vividsolutions.jump.workbench.plugin.*;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.MultiInputDialog;
import com.vividsolutions.jump.workbench.ui.plugin.*;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jts.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jump.task.*;
import com.vividsolutions.jump.workbench.ui.*;

public class PolygonizerPlugIn
    extends ThreadedBasePlugIn
{

  private final static String LAYER = "Layer";
  private final static String SPLIT_LINESTRINGS = "Split linestrings into segments";

  private MultiInputDialog dialog;
  private String layerName;
  private boolean splitLineStrings = false;
  private int inputEdgeCount = 0;
  private int dangleCount = 0;
  private int cutCount = 0;
  private int invalidRingCount = 0;

  public PolygonizerPlugIn() { }

  /**
   * Returns a very brief description of this task.
   * @return the name of this task
   */
  public String getName() { return "Polygonizer"; }

  public void initialize(PlugInContext context) throws Exception {
    context.getFeatureInstaller().addMainMenuItem(this,
        new String[] {"Tools", "Analysis"}, "Polygonize...", false, null, new MultiEnableCheck()
        .add(context.getCheckFactory().createWindowWithLayerViewPanelMustBeActiveCheck())
        .add(context.getCheckFactory().createAtLeastNLayersMustExistCheck(1))
        );
  }

  public boolean execute(PlugInContext context) throws Exception {
    dialog = new MultiInputDialog(
        context.getWorkbenchFrame(), "Polygonizer", true);
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

    Polygonizer polygonizer = new Polygonizer();
    polygonizer.setSplitLineStrings(splitLineStrings);

    monitor.report("Polygonizing...");

    Layer layer = dialog.getLayer(LAYER);
    FeatureCollection lineFC = layer.getFeatureCollectionWrapper();
    inputEdgeCount = lineFC.size();

    for (Iterator i = lineFC.iterator(); i.hasNext(); ) {
      Feature f = (Feature) i.next();
      Geometry g = f.getGeometry();
      if (g.getDimension() == 1) {
        polygonizer.add(g);
      }
    }
    polygonizer.polygonize(monitor);

    if (monitor.isCancelRequested()) return;
    createLayers(context, polygonizer);
  }

  private void createLayers(PlugInContext context, Polygonizer polygonizer)
         throws Exception
  {
    FeatureCollection dangleFC = FeatureDatasetFactory.createFromGeometry(polygonizer.getDangles());
    dangleCount = dangleFC.size();
    if (dangleFC.size() > 0) {
      Layer lyr4 = context.addLayer(
          StandardCategoryNames.QA,
          "Dangles",
          dangleFC);
      LayerStyleUtil.setLinearStyle(lyr4, Color.red, 2, 0);
      lyr4.setDescription("Dangling edges");
    }

    FeatureCollection cutFC = FeatureDatasetFactory.createFromGeometry(polygonizer.getCutEdges());
    cutCount = cutFC.size();

    if (cutFC.size() > 0) {
      Layer lyr = context.addLayer(
          StandardCategoryNames.QA,
          "Cuts",
          cutFC);
      LayerStyleUtil.setLinearStyle(lyr, Color.blue, 2, 0);
      lyr.setDescription("Cut edges");
    }

    FeatureCollection invalidRingFC = FeatureDatasetFactory.createFromGeometry(polygonizer.getInvalidRingLines());
    invalidRingCount = invalidRingFC.size();

    if (invalidRingFC.size() > 0) {
      Layer lyr = context.addLayer(
          StandardCategoryNames.QA,
          "Invalid Rings",
          invalidRingFC);
      LayerStyleUtil.setLinearStyle(lyr, Color.blue, 2, 0);
      lyr.setDescription("Invalid rings");
    }

    FeatureCollection polyFC = FeatureDatasetFactory.createFromGeometry(polygonizer.getPolygons());
    context.addLayer(
        StandardCategoryNames.RESULT_SUBJECT,
        layerName + " Polygons",
        polyFC);

    createOutput(context, polyFC);

  }

  private void createOutput(PlugInContext context,
      FeatureCollection polyFC)
  {
    context.getOutputFrame().createNewDocument();
    context.getOutputFrame().addHeader(1,
        "Polygonization");
    context.getOutputFrame().addField("Layer: ", layerName);


    context.getOutputFrame().addText(" ");
    context.getOutputFrame().addField(
                                      "# Input Edges: ", "" + inputEdgeCount);
    context.getOutputFrame().addField(
                                      "# Polygons Created: ", "" + polyFC.size());
    context.getOutputFrame().addField(
        "# Dangling Edges found: ", "" + dangleCount);
    context.getOutputFrame().addField(
        "# Cut Edges found: ", "" + cutCount);
    context.getOutputFrame().addField(
        "# Invalid Rings found: ", "" + invalidRingCount);
  }

  private void setDialogValues(MultiInputDialog dialog, PlugInContext context) {
    dialog.setSideBarImage(new ImageIcon(getClass().getResource("Polygonize.png")));
    dialog.setSideBarDescription("Polygonizes the line segments in a layer. "
                                + "The line segments are assumed to be noded correctly. "
                                + "Dangles and Cutlines are identified."
    );
    String fieldName = LAYER;
    JComboBox addLayerComboBox = dialog.addLayerComboBox(fieldName, context.getCandidateLayer(0), null, context.getLayerManager());
    dialog.addCheckBox(SPLIT_LINESTRINGS, false, "If lines are noded at vertices rather than endpoints "
       + "this options allows input linestrings to be split into separate segments.");
  }

  private void getDialogValues(MultiInputDialog dialog) {
    Layer layer = dialog.getLayer(LAYER);
    layerName = layer.getName();
    splitLineStrings = dialog.getBoolean(SPLIT_LINESTRINGS);
  }
}
