

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

package com.vividsolutions.jcs.plugin.conflate;

import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jcs.conflate.polygonmatch.AngleHistogramMatcher;
import com.vividsolutions.jcs.conflate.polygonmatch.Histogram;
import com.vividsolutions.jump.workbench.ui.GraphFrame;
import java.util.List;
import java.util.ArrayList;
import com.vividsolutions.jump.workbench.ui.GraphPanel;
import com.vividsolutions.jump.workbench.plugin.*;

public class AngleHistogramPlugIn extends AbstractPlugIn {

  public AngleHistogramPlugIn() {
  }

  private static final int BIN_COUNT = 18;

  public void initialize(PlugInContext context) throws Exception {
    context.getFeatureInstaller().addMainMenuItem(this,
        new String[] {"Conflate", "Test"}, getName(), false, null, new MultiEnableCheck()
        .add(context.getCheckFactory().createWindowWithLayerViewPanelMustBeActiveCheck())
        .add(context.getCheckFactory().createExactlyNItemsMustBeSelectedCheck(1)));
  }

  public boolean execute(PlugInContext context) throws Exception {
    reportNothingToUndoYet(context);
    Feature feature = (Feature) context.getLayerViewPanel().getSelectionManager().createFeaturesFromSelectedItems().iterator().next();
    String title = "Angle Histogram (Feature #" + feature.getID() +")";
    display(angleHistogramMatcher.angleHistogram(
        feature.getGeometry(), BIN_COUNT), title, context);
    return true;
  }

  private AngleHistogramMatcher angleHistogramMatcher = new AngleHistogramMatcher();

  private void display(Histogram h, String title, PlugInContext context) {
    GraphFrame f = new GraphFrame();
    f.setTitle(title);
    f.getPanel().setValues(values(h));
    f.getPanel().setType(GraphPanel.BAR);
    context.getWorkbenchFrame().addInternalFrame(f);
  }

  private List values(Histogram h) {
    List values = new ArrayList();
    for (int i = 0; i < h.getBinCount(); i++) {
      values.add(new Double(h.getBinScore(i)));
    }
    return values;
  }

}
