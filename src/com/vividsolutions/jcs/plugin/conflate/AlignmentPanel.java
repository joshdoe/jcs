package com.vividsolutions.jcs.plugin.conflate;

import java.awt.*;
import com.vividsolutions.jump.workbench.ui.toolbox.*;
import javax.swing.*;

public class AlignmentPanel extends JPanel {
  private BorderLayout borderLayout1 = new BorderLayout();
  private JButton jButton1 = new JButton();

  public AlignmentPanel(ToolboxDialog toolbox) {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    jButton1.setText("jButton1");
    this.setLayout(borderLayout1);
    this.add(jButton1, BorderLayout.WEST);
  }
}
