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

import java.awt.*;
import javax.swing.*;
import java.util.*;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jump.workbench.plugin.*;
import com.vividsolutions.jump.workbench.model.*;
import com.vividsolutions.jump.workbench.ui.*;
import com.vividsolutions.jump.workbench.ui.toolbox.*;
import com.vividsolutions.jump.task.*;
import com.vividsolutions.jump.util.ColorUtil;
import com.vividsolutions.jump.workbench.ui.renderer.style.*;
import com.vividsolutions.jcs.conflate.roads.*;
import com.vividsolutions.jcs.util.*;

public class RoadMatcherModel
{
  private PlugInContext context;
  private DefaultComboBoxModel[] layerComboBoxModel = new DefaultComboBoxModel[2];
  private Layer refLyr = null;
  private Layer subLyr = null;
  private RoadMatcher roadMatcher;
  // workflow state variables
  private boolean isAutoMatchRun = false;

  private Layer edgeIndLyr;
  private Layer unmatchedRefLyr;
  private Layer unmatchedSubLyr;
  transient private Vector roadMatchModelListeners;

  public RoadMatcherModel(PlugInContext context)
  {
    init(context);
  }

  private void init(PlugInContext context)
  {
    this.context = context;
    /*
toolbox.addWindowListener(new WindowAdapter() {
    public void windowActivated(WindowEvent e) {
        updateComponents();
    }
});
GUIUtil
    .addInternalFrameListener(
        toolbox.getContext().getWorkbench().getFrame().getDesktopPane(),
        GUIUtil.toInternalFrameListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        updateComponents();
    }
}));
    */
    //sourceLayerComboBox.setModel(sourceLayerComboBoxModel);
    //sourceLayerComboBox.setRenderer(new LayerNameRenderer());

  }

  public boolean isAutoMatchRun() { return isAutoMatchRun; }

  public ComboBoxModel getLayerComboBoxModel(int i)
  {
    if (layerComboBoxModel[i] ==  null) {
      LayerViewPanelProxy proxy =
          (LayerViewPanelProxy) context
              .getWorkbenchFrame()
              .getActiveInternalFrame();
      layerComboBoxModel[i] =  new DefaultComboBoxModel(
          new Vector(proxy.getLayerViewPanel().getLayerManager().getLayers()));
    }
    return layerComboBoxModel[i];
  }

  public void match(Layer refLyr, Layer subLyr)
  {
    isAutoMatchRun = true;
    roadMatcher = new RoadMatcher(
        refLyr.getFeatureCollectionWrapper(),
        subLyr.getFeatureCollectionWrapper(), new DummyTaskMonitor());

    //monitor.report("Matching Roads...");
    roadMatcher.match();
    createLayers(context, roadMatcher);
    fireDataChanged(new RoadMatchModelEvent(this));
  }

  public void deleteSelectedMatches()
  {
    RoadMatches roadMatches = roadMatcher.getEdgeMatchIndicators();
    // would be better to get the selected features from only the match ind layer
    Collection selectedFeat = context.getLayerViewPanel().getSelectionManager().getFeatureSelection().getFeaturesWithSelectedItems();
    for (Iterator i = selectedFeat.iterator(); i.hasNext(); ) {
      Object obj = i.next();
      if (! (obj instanceof RoadEdgeMatch))
        continue;
      roadMatches.remove((RoadEdgeMatch) obj);
    }
    // force a repaint on the layer
    markLayersChanged();
    // clear the now-deleted selection
    context.getLayerViewPanel().getSelectionManager().clear();
    fireDataChanged(new RoadMatchModelEvent(this));
  }

  public void matchSelectedEdges()
  {
    RoadEdge[] matchEdges = new RoadEdge[2];
    int matchEdgeCount = 0;
    // would be better to get the selected features from only the edge layers
    Collection selectedFeat = context.getLayerViewPanel().getSelectionManager().getFeatureSelection().getFeaturesWithSelectedItems();
    for (Iterator i = selectedFeat.iterator(); i.hasNext(); ) {
      Object obj = i.next();
      // need to check that we have one from each dataset
      if ((obj instanceof RoadEdgeFeature) && matchEdgeCount < 2) {
        matchEdges[matchEdgeCount++] = ((RoadEdgeFeature) obj).getEdge();
      }
    }
    // make the match, only if both edges are not currently matched
    if (! matchEdges[0].hasMatch() && ! matchEdges[1].hasMatch()) {
      matchEdges[0].setMatch(matchEdges[1]);
      matchEdges[1].setMatch(matchEdges[0]);
      RoadMatches roadMatches = roadMatcher.getEdgeMatchIndicators();
      roadMatches.add(matchEdges[0], matchEdges[1]);

      // force a repaint on the layer
      markLayersChanged();
      // clear the selection
      context.getLayerViewPanel().getSelectionManager().clear();
      fireDataChanged(new RoadMatchModelEvent(this));
    }
    else {
      context.getWorkbenchFrame().warnUser("Both road edges must be unmatched");
    }
  }

  private void markLayersChanged()
  {
    edgeIndLyr.fireAppearanceChanged();
    unmatchedRefLyr.fireAppearanceChanged();
    unmatchedSubLyr.fireAppearanceChanged();
  }

  public String getStatus()
  {
    if (roadMatcher == null)
      return "";
    String txt = "Matches: " + roadMatcher.getEdgeMatchIndicators().size();
    txt += "\nRef - " + getMatchedEdgeLayerStatus(roadMatcher.getNetwork(0).getEdgesFC());
    txt += "\nSub - " + getMatchedEdgeLayerStatus(roadMatcher.getNetwork(1).getEdgesFC());
    return txt;
  }

  private String getMatchedEdgeLayerStatus(FeatureCollection edgesFC)
  {
    int totalEdgesCount = edgesFC.size();
    int totalUnmatched = 0;
    for (Iterator i = edgesFC.iterator(); i.hasNext(); ) {
      RoadEdgeFeature f = (RoadEdgeFeature) i.next();
      if (! f.hasMatch())
        totalUnmatched++;
    }
    return "total: " + totalEdgesCount + " unmatched: " + totalUnmatched;

  }
  private void createLayers(PlugInContext context, RoadMatcher roadMatcher)
  {
    FeatureCollection edgeIndFC = roadMatcher.getEdgeMatchIndicators();
    edgeIndLyr = context.addLayer(
        StandardCategoryNames.QA,
        "Edge Match Ind",
        edgeIndFC);
    edgeIndLyr.setSynchronizingLineColor(false);
    edgeIndLyr.getBasicStyle().setFillColor(ColorUtil.GOLD);
    edgeIndLyr.getBasicStyle().setRenderingFill(true);
    edgeIndLyr.getBasicStyle().setLineColor(ColorUtil.GOLD);
    edgeIndLyr.getBasicStyle().setLineWidth(2);
    edgeIndLyr.getBasicStyle().setAlpha(150);
    edgeIndLyr.fireAppearanceChanged();
    edgeIndLyr.setDescription("Edge Match Indicators");

    FeatureCollection matchedNodeIndFC = roadMatcher.getMatchedEdgeNodeVectors();
    Layer lyr2 = context.addLayer(
        StandardCategoryNames.QA,
        "Node Matches",
        matchedNodeIndFC);
    LayerStyleUtil.setLinearStyle(lyr2, Color.blue, 2, 0);
    lyr2.addStyle(new ArrowLineStringEndpointStyle.NarrowSolidEnd());
    lyr2.fireAppearanceChanged();
    lyr2.setDescription("Node Matches");

    FeatureCollection unmatchedRefFC = roadMatcher.getNetwork(0).getEdgesFC();
    unmatchedRefLyr = context.addLayer(
        StandardCategoryNames.QA,
        "Unmatched Ref",
        unmatchedRefFC);
    unmatchedRefLyr.getBasicStyle().setEnabled(false);
    unmatchedRefLyr.addStyle(createUnmatchedStyle(Color.green));
    unmatchedRefLyr.setDescription("Unmatched Reference Edges");

    FeatureCollection unmatchedSubFC = roadMatcher.getNetwork(1).getEdgesFC();
    unmatchedSubLyr = context.addLayer(
        StandardCategoryNames.QA,
        "Unmatched Sub",
        unmatchedSubFC);
    unmatchedSubLyr.getBasicStyle().setEnabled(false);
    unmatchedSubLyr.addStyle(createUnmatchedStyle(Color.red));
    unmatchedSubLyr.setDescription("Unmatched Subject Edges");
  }

  private static ColorThemingStyle createUnmatchedStyle(Color lineColor)
  {
    Map attributeToStyleMap = new HashMap();
    BasicStyle lineStyle = new BasicStyle(lineColor);
    lineStyle.setRenderingFill(false);
    lineStyle.setLineColor(lineColor);
    lineStyle.setLineWidth(5);
    attributeToStyleMap.put(new Integer(0), lineStyle);
    BasicStyle noDraw = new BasicStyle();
    noDraw.setRenderingFill(false);
    noDraw.setRenderingLine(false);
    ColorThemingStyle themeStyle = new ColorThemingStyle(RoadEdgeFeature.ATTR_UNMATCHED, attributeToStyleMap, noDraw);
    themeStyle.setEnabled(true);

    return themeStyle;
  }


  public void createMatchLayer()
  {
    FeatureCollection edgeMatchFC = roadMatcher.getEdgeMatchReportFC();
    edgeIndLyr = context.addLayer(
        StandardCategoryNames.QA,
        "Edge Matches",
        edgeMatchFC);
    edgeIndLyr.setSynchronizingLineColor(false);
    edgeIndLyr.getBasicStyle().setFillColor(Color.red);
    edgeIndLyr.getBasicStyle().setRenderingFill(false);
    edgeIndLyr.getBasicStyle().setLineColor(Color.red);
    edgeIndLyr.getBasicStyle().setLineWidth(2);
    edgeIndLyr.getBasicStyle().setAlpha(200);
    edgeIndLyr.fireAppearanceChanged();
    edgeIndLyr.setDescription("Edge Matches");

  }
  //================== Events ============================

  public synchronized void removeRoadMatchModelListener(RoadMatchModelListener l) {
    if (roadMatchModelListeners != null && roadMatchModelListeners.contains(l)) {
      Vector v = (Vector) roadMatchModelListeners.clone();
      v.removeElement(l);
      roadMatchModelListeners = v;
    }
  }
  public synchronized void addRoadMatchModelListener(RoadMatchModelListener l) {
    Vector v = roadMatchModelListeners == null ? new Vector(2) : (Vector) roadMatchModelListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      roadMatchModelListeners = v;
    }
  }
  protected void fireDataChanged(RoadMatchModelEvent e) {
    if (roadMatchModelListeners != null) {
      Vector listeners = roadMatchModelListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((RoadMatchModelListener) listeners.elementAt(i)).dataChanged(e);
      }
    }
  }

}

