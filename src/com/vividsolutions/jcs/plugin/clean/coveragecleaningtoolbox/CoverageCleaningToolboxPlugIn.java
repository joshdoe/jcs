package com.vividsolutions.jcs.plugin.clean.coveragecleaningtoolbox;

import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.toolbox.ToolboxDialog;
import com.vividsolutions.jump.workbench.ui.toolbox.ToolboxPlugIn;
import com.vividsolutions.jump.workbench.ui.toolbox.ToolboxStateManager;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import java.util.Collections;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;


public class CoverageCleaningToolboxPlugIn extends ToolboxPlugIn {
    protected void initializeToolbox(ToolboxDialog toolbox) {
        ToolboxPanel toolboxPanel = new ToolboxPanel(toolbox.getContext());
        toolbox.add(new UpdatingSnapVerticesTool(toolbox.getContext(),
                toolboxPanel));
        moveToolbarSouth(toolbox);
        toolbox.getCenterPanel().add(toolboxPanel, BorderLayout.CENTER);
        toolbox.setInitialLocation(new GUIUtil.Location(20, true, 20, false));
        new ToolboxStateManager(toolbox,
            Collections.singletonMap(ToolboxPanel.Tab.class,
                new ToolboxStateManager.Strategy() {
                protected void addActionListener(ActionListener actionListener,
                        Component component) {
                    ((ToolboxPanel.Tab) component).addActionListener(
                            actionListener);
                }

                protected Object getToolboxValue(Component component) {
                    return ((ToolboxPanel.Tab) component).getChild();
                }

                protected void setToolboxValue(Object value, Component component) {
                    ((ToolboxPanel.Tab) component).setChild((Component) value);
                    if (component instanceof ToolboxPanel.TableTab) {
                        ((ToolboxPanel.TableTab) component).updateTitle();
                    }
                }

                protected Object getDefaultValue(Object initialToolboxValue,
                        Component component) {
                    return ((ToolboxPanel.Tab) component).createDefaultChild();
                }
            }));
    }

    private void moveToolbarSouth(ToolboxDialog toolbox) {
        JToolBar toolbar = toolbox.getToolBar();
        toolbar.getParent().remove(toolbar);

        JPanel southPanel = new JPanel(new GridBagLayout());
        southPanel.add(toolbar,
            new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        southPanel.add(new JLabel("Snap Vertices tool, for fixing gaps manually"),
            new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        southPanel.add(new JPanel(),
            new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        toolbox.getCenterPanel().add(southPanel, BorderLayout.SOUTH);
    }

    public void initialize(PlugInContext context) throws Exception {
        createMainMenuItem(new String[] { "Clean" }, null,
            context.getWorkbenchContext());
    }
}
