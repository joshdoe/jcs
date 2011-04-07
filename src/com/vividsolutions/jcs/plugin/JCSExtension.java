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

package com.vividsolutions.jcs.plugin;

import com.vividsolutions.jcs.plugin.clean.*;
import com.vividsolutions.jcs.plugin.clean.coveragecleaningtoolbox.CoverageCleaningToolboxPlugIn;
import com.vividsolutions.jcs.plugin.conflate.*;
import com.vividsolutions.jcs.plugin.conflate.polygonmatch.PolygonMatcherToolboxPlugIn;
import com.vividsolutions.jcs.plugin.conflate.roads.*;
import com.vividsolutions.jcs.plugin.qa.*;
import com.vividsolutions.jcs.plugin.test.VertexHausdorffDistancePlugIn;
import com.vividsolutions.jcs.plugin.tools.*;
import com.vividsolutions.jump.workbench.plugin.*;

public class JCSExtension 
    extends Extension 
{

    public String getName() {
        return "JCS Conflation Suite";
    }

    public String getVersion() {
        return "1.0.1";
    }

  public void configure(PlugInContext context) throws Exception
  {

    new NewConflationTaskPlugIn().initialize(context);

    new PolygonizerPlugIn().initialize(context);
    new UniqueSegmentsPlugIn().initialize(context);
    new PrecisionReducerPlugIn().initialize(context);

    new MatchedSegmentsPlugIn().initialize(context);
    new OverlapFinderPlugIn().initialize(context);
    new CoverageGapPlugIn().initialize(context);
    new CoverageOverlapFinderPlugIn().initialize(context);
    new CloseVertexFinderPlugIn().initialize(context);
    new OffsetBoundaryCornerFinderPlugIn().initialize(context);
    new DiffSegmentsPlugIn().initialize(context);
    new DiffGeometryPlugIn().initialize(context);

    new BoundaryMatcherPlugIn().initialize(context);
    new VertexSnapperPlugIn().initialize(context);
    new CoverageAlignerPlugIn().initialize(context);

    new RoadMatcherPlugIn().initialize(context);
    new RoadMatcherToolboxPlugIn().initialize(context);

    new CoverageGapRemoverPlugIn().initialize(context);
    new CoverageGapInFencePlugIn().initialize(context);
    new UpdateCoverageGapInFencePlugIn().initialize(context);
    new ShortSegmentRemoverPlugIn().initialize(context);

    new PolygonMatcherToolboxPlugIn().initialize(context);
    new CoverageCleaningToolboxPlugIn().initialize(context);
    
    //AngleHistogramPlugIn uses Chart2D, which I've removed from the
    //build. TODO: Update it to use JFreeChart instead, which is better,
    //and which I'm including in the build for polygon-matching. [Jon Aquino]
    //new AngleHistogramPlugIn().initialize(context);
    
    new TurningFunctionPlugIn().initialize(context);

    //These two were in the XML properties file, so I've added them here. [Jon Aquino]
    new AlignmentToolboxPlugIn().initialize(context);
    new VertexHausdorffDistancePlugIn().initialize(context);
  }

}
