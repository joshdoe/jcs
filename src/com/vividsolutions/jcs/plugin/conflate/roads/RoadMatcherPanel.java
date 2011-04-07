package com.vividsolutions.jcs.plugin.conflate.roads;

import java.awt.*;
import javax.swing.*;
import com.vividsolutions.jump.workbench.ui.toolbox.*;
import com.vividsolutions.jump.workbench.ui.*;
import com.vividsolutions.jump.workbench.model.*;
import java.awt.event.*;

public class RoadMatcherPanel extends JPanel
{
  // custom data
  private RoadMatcherModel model;

  private JButton btnMatch = new JButton();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JComboBox refLayerComboBox = new JComboBox();
  private JLabel jLabel1 = new JLabel();
  private JComboBox subLayerComboBox = new JComboBox();
  private JLabel jLabel2 = new JLabel();

  public RoadMatcherPanel(ToolboxDialog toolbox) {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    // set width explicitly to make sure Toolbox is sized appropriately
    subLayerComboBox.setPreferredSize(new Dimension(100, (int)subLayerComboBox.getPreferredSize().getHeight()));
    refLayerComboBox.setPreferredSize(new Dimension(100, (int)refLayerComboBox.getPreferredSize().getHeight()));

    btnMatch.setToolTipText("");
    btnMatch.setText("Match Roads");
    btnMatch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnMatch_actionPerformed(e);
      }
    });
    this.setLayout(gridBagLayout1);
    jLabel1.setToolTipText("");
    jLabel1.setText("Reference");
    jLabel2.setText("Subject");
    this.add(btnMatch,         new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
    this.add(refLayerComboBox,      new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel1,       new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
    this.add(subLayerComboBox,      new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel2,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
  }

  public void setModel(RoadMatcherModel model)
  {
    this.model = model;
    refLayerComboBox.setModel(model.getLayerComboBoxModel(0));
    refLayerComboBox.setRenderer(new LayerNameRenderer());
    subLayerComboBox.setModel(model.getLayerComboBoxModel(1));
    subLayerComboBox.setRenderer(new LayerNameRenderer());
  }

  void btnMatch_actionPerformed(ActionEvent e) {
    model.match((Layer) refLayerComboBox.getModel().getSelectedItem(),
                (Layer) subLayerComboBox.getModel().getSelectedItem());
  }
}
