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
import com.vividsolutions.jump.workbench.ui.toolbox.*;
import com.vividsolutions.jump.workbench.ui.*;
import com.vividsolutions.jump.workbench.model.*;
import java.awt.event.*;
import javax.swing.border.*;

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
  private JButton btnDeleteSelectedMatches = new JButton();
  private JButton btnCreateMatch = new JButton();
  private JTextArea txtStatus = new JTextArea();
  private Border border1;
  private JButton btnCreateMatchLayer = new JButton();

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
    border1 = BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(124, 124, 124),new Color(178, 178, 178));
    subLayerComboBox.setPreferredSize(new Dimension(100, (int)subLayerComboBox.getPreferredSize().getHeight()));
    refLayerComboBox.setPreferredSize(new Dimension(100, (int)refLayerComboBox.getPreferredSize().getHeight()));

    btnMatch.setToolTipText("");
    btnMatch.setMargin(new Insets(0, 14, 0, 14));
    btnMatch.setText("AutoMatch");
    btnMatch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnMatch_actionPerformed(e);
      }
    });
    this.setLayout(gridBagLayout1);
    jLabel1.setToolTipText("");
    jLabel1.setText("Reference");
    jLabel2.setText("Subject");
    btnDeleteSelectedMatches.setToolTipText("Deletes the currently selected matches");
    btnDeleteSelectedMatches.setMargin(new Insets(1, 2, 1, 2));
    btnDeleteSelectedMatches.setText("Delete Matches");
    btnDeleteSelectedMatches.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnDeleteSelectedMatches_actionPerformed(e);
      }
    });
    btnCreateMatch.setMaximumSize(new Dimension(70, 25));
    btnCreateMatch.setMinimumSize(new Dimension(70, 25));
    btnCreateMatch.setPreferredSize(new Dimension(70, 25));
    btnCreateMatch.setToolTipText("Match the currently selected edges");
    btnCreateMatch.setMargin(new Insets(1, 2, 1, 2));
    btnCreateMatch.setText("Match Edges");
    btnCreateMatch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnCreateMatch_actionPerformed(e);
      }
    });
    txtStatus.setBackground(SystemColor.inactiveCaptionText);
    txtStatus.setFont(new java.awt.Font("SansSerif", 0, 10));
    txtStatus.setBorder(BorderFactory.createLoweredBevelBorder());
    txtStatus.setToolTipText("");
    btnCreateMatchLayer.setToolTipText("");
    btnCreateMatchLayer.setMargin(new Insets(1, 2, 1, 2));
    btnCreateMatchLayer.setText("Create Match Report Layer");
    btnCreateMatchLayer.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnCreateMatchLayer_actionPerformed(e);
      }
    });
    this.add(btnMatch,              new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
    this.add(refLayerComboBox,          new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel1,             new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(0, 5, 0, 5), 0, 0));
    this.add(subLayerComboBox,          new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel2,         new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(0, 5, 0, 5), 0, 0));
    this.add(txtStatus,         new GridBagConstraints(0, 5, 2, 1, 0.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    this.add(btnDeleteSelectedMatches,        new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
    this.add(btnCreateMatch,        new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
    this.add(btnCreateMatchLayer,    new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
  }

  public void setModel(RoadMatcherModel model)
  {
    this.model = model;
    model.addRoadMatchModelListener(new RoadMatchModelListener() {
      public void dataChanged(RoadMatchModelEvent e) {
        updateUIState();
      }
    });
    updateUIState();

    refLayerComboBox.setModel(model.getLayerComboBoxModel(0));
    refLayerComboBox.setRenderer(new LayerNameRenderer());
    subLayerComboBox.setModel(model.getLayerComboBoxModel(1));
    subLayerComboBox.setRenderer(new LayerNameRenderer());
  }

  void btnMatch_actionPerformed(ActionEvent e) {
    model.match((Layer) refLayerComboBox.getModel().getSelectedItem(),
                (Layer) subLayerComboBox.getModel().getSelectedItem());
  }

  void btnDeleteSelectedMatches_actionPerformed(ActionEvent e) {
    model.deleteSelectedMatches();
  }

  void btnCreateMatch_actionPerformed(ActionEvent e) {
    model.matchSelectedEdges();
  }

  void updateUIState()
  {
    boolean isAutoMatchRun  = model.isAutoMatchRun();
    btnMatch.setEnabled(! isAutoMatchRun);
    btnDeleteSelectedMatches.setEnabled(isAutoMatchRun);
    btnCreateMatch.setEnabled(isAutoMatchRun);
    btnCreateMatchLayer.setEnabled(isAutoMatchRun);
    updateUIData();
  }

  void updateUIData()
  {
    String txt = "Matches: " + 100;
    txtStatus.setText(model.getStatus());
  }

  void btnCreateMatchLayer_actionPerformed(ActionEvent e) {
    model.createMatchLayer();
  }
}
