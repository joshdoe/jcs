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
import com.vividsolutions.jump.workbench.ui.renderer.style.*;
import com.vividsolutions.jcs.conflate.roads.*;
import com.vividsolutions.jcs.util.*;

public class RoadMatcherModel {

  private PlugInContext context;
  private DefaultComboBoxModel[] layerComboBoxModel = new DefaultComboBoxModel[2];
  private Layer refLyr = null;
  private Layer subLyr = null;

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
    RoadMatcher roadMatcher = new RoadMatcher(
        refLyr.getFeatureCollectionWrapper(),
        subLyr.getFeatureCollectionWrapper(), new DummyTaskMonitor());

    //monitor.report("Matching Roads...");
    roadMatcher.match();
    createLayers(context, roadMatcher);

  }

  private void createLayers(PlugInContext context, RoadMatcher roadMatcher)
  {
    FeatureCollection edgeIndFC = roadMatcher.getEdgeMatchIndicators();
    Layer lyr = context.addLayer(
        StandardCategoryNames.QA,
        "Edge Match Ind",
        edgeIndFC);
    lyr.setSynchronizingLineColor(false);
    lyr.getBasicStyle().setFillColor(ColorUtil.GOLD);
    lyr.getBasicStyle().setRenderingFill(true);
    lyr.getBasicStyle().setLineColor(ColorUtil.GOLD);
    lyr.getBasicStyle().setLineWidth(2);
    lyr.getBasicStyle().setAlpha(150);
    lyr.fireAppearanceChanged();
    lyr.setDescription("Edge Match Indicators");

    FeatureCollection matchedNodeIndFC = roadMatcher.getMatchedEdgeNodeVectors();
    Layer lyr2 = context.addLayer(
        StandardCategoryNames.QA,
        "Node Matches",
        matchedNodeIndFC);
    LayerStyleUtil.setLinearStyle(lyr2, Color.blue, 2, 0);
    lyr2.addStyle(new ArrowTerminalDecorator.NarrowSolidEnd());
    lyr2.fireAppearanceChanged();
    lyr2.setDescription("Node Matches");

    FeatureCollection unmatchedRefFC = roadMatcher.getNetwork(0).getUnmatchedEdgeFC();
    Layer lyr3 = context.addLayer(
        StandardCategoryNames.QA,
        "Unmatched Ref",
        unmatchedRefFC);
    LayerStyleUtil.setLinearStyle(lyr3, Color.green, 4, 0);
    lyr3.setDescription("Unmatched Reference Edges");

    FeatureCollection unmatchedSubFC = roadMatcher.getNetwork(1).getUnmatchedEdgeFC();
    Layer lyr4 = context.addLayer(
        StandardCategoryNames.QA,
        "Unmatched Sub",
        unmatchedSubFC);
    LayerStyleUtil.setLinearStyle(lyr4, Color.red, 4, 0);
    lyr4.setDescription("Unmatched Subject Edges");


  }

}