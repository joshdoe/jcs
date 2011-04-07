/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI
 * for visualizing and manipulating spatial features with geometry and attributes.
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
import com.vividsolutions.jump.workbench.plugin.*;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.cursortool.editing.*;
import com.vividsolutions.jump.workbench.ui.toolbox.*;
import com.vividsolutions.jump.workbench.ui.cursortool.SelectFeaturesTool;

public class RoadMatcherToolboxPlugIn
    extends ToolboxPlugIn
{
  public final static String ROAD_MATCHER_MODEL_KEY =
      RoadMatcherToolboxPlugIn.class.getName() + " - model";

  private RoadMatcherPanel toolboxPanel;

  public void initialize(PlugInContext context) throws Exception {
    createMainMenuItem(
        new String[] { "Roads" }, null, context.getWorkbenchContext());
  }

  protected void initializeToolbox(ToolboxDialog toolbox) {
    EnableCheckFactory checkFactory = new EnableCheckFactory(toolbox.getContext());

    toolboxPanel = new RoadMatcherPanel(toolbox);

    toolbox.getCenterPanel().add(toolboxPanel, BorderLayout.CENTER);

    // add basic selection tool, since it is currently the main tool used
    toolbox.add(new SelectFeaturesTool());

    // Set y so it is positioned below Editing toolbox. [Jon Aquino]
    toolbox.setInitialLocation(new GUIUtil.Location(20, true, 20, false));
  }

  public boolean execute(PlugInContext context)
      throws Exception
  {
    super.execute(context);

    toolboxPanel.setModel(getModel(context));

    return true;
  }

  /**
   * Gets the existing or a new model for this task
   *
   * @param context the context in which to find the model
   * @return a model which exists in the context
   */
  private RoadMatcherModel getModel(PlugInContext context)
  {
    RoadMatcherModel model =
        (RoadMatcherModel) context.getLayerManager().getBlackboard().get(ROAD_MATCHER_MODEL_KEY);
    if (model == null) {
      model = new RoadMatcherModel(context);
      context.getLayerManager().getBlackboard().put(ROAD_MATCHER_MODEL_KEY, model);
    }
    return model;
  }

}
