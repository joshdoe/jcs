

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

package com.vividsolutions.jcs.conflate.polygonmatch;

import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.util.Assert;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.geom.Angle;
import com.vividsolutions.jump.util.CoordinateArrays;

/**
 * Matches geometries by comparing their "angle histograms". An angle histogram
 * is a histogram of segment angles (with the positive x-axis), weighted by
 * segment length. Angles range from -pi to +pi.
 */
public class AngleHistogramMatcher extends IndependentCandidateMatcher {

  /**
   * Creates an AngleHistogramMatcher with 0 bins. Be sure to call #setBinCount.
   */
  public AngleHistogramMatcher() {
  }

  /**
   * Creates an AngleHistogramMatcher with the given number of bins.
   * @param binCount the number of bins into which -pi to +pi should be split
   */
  public AngleHistogramMatcher(int binCount) {
    setBinCount(binCount);
  }

  /**
   * Sets the number of histogram bins.
   * @param binCount the number of bins into which -pi to +pi should be split
   */
  public void setBinCount(int binCount) {
    this.binCount = binCount;
  }

  private int binCount;

  /**
   * Finds the symmetric difference between the angle histograms of the two
   * features.
   * @param target the feature to match
   * @param candidate the feature to compare with the target
   * @return a linear function of the symmetric difference: 1 if
   * the histograms perfectly overlap; 0 if they do not overlap at all.
   */
  protected double match(Feature target, Feature candidate) {
    Histogram targetHist = angleHistogram(target.getGeometry(), binCount);
    Histogram candidateHist = angleHistogram(candidate.getGeometry(), binCount);
    return MatcherUtil.toScoreFromSymDiffArea(
        targetHist.getTotalScore(), candidateHist.getTotalScore(),
        targetHist.symDiff(candidateHist));
  }

  /**
   * Creates an angle histogram for the given Geometry. The sum of the histogram
   * scores will equal the sum of the Geometry's segment lengths.
   * @param g the Geometry to analyze
   * @param binCount the number of bins into which -pi to +pi should be split
   * @return a histogram of g's segment angles (with the positive x-axis),
   * weighted by segment length
   */
  public Histogram angleHistogram(Geometry g, int binCount) {
    Geometry clone = (Geometry) g.clone();
    //#normalize makes linestrings and polygons use a standard orientation.
    //[Jon Aquino]
    clone.normalize();
    //In #toCoordinateArrays call, set orientPolygons=false because
    //#normalize takes care of orienting the rings. [Jon Aquino]
    List lineStrings = CoordinateArrays.toCoordinateArrays(clone, false);
    Histogram h = new Histogram(binCount);
    for (Iterator i = lineStrings.iterator(); i.hasNext(); ) {
      Coordinate[] lineString = (Coordinate[]) i.next();
      h.add(angleHistogram(lineString, binCount));
    }
    return h;
  }

  private Histogram angleHistogram(Coordinate[] lineString, int binCount) {
    Histogram h = new Histogram(binCount);
    for (int i = 1; i < lineString.length; i++) { //start 1
      h.addToBinScore(
            bin(Angle.angle(lineString[i-1], lineString[i]), binCount),
            lineString[i-1].distance(lineString[i]));
    }
    return h;
  }

  /**
   * Returns the histogram bin that the angle should go into.
   * @param angle in radians
   * @param binCount the number of bins into which -pi to pi is divided
   * @return the index of the bin that the angle should go into (0, 1, 2, ...)
   */
  public int bin(double angle, int binCount) {
    Assert.isTrue(angle >= -Math.PI);
    Assert.isTrue(angle <= Math.PI);
    double binSize = 2 * Math.PI / binCount;
    int bin = (int)Math.floor((angle + Math.PI) / binSize);
    return Math.min(bin, binCount - 1); //360 case
  }
}
