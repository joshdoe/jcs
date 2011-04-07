package com.vividsolutions.jcs.qa;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jump.geom.LineSegmentUtil;
import com.vividsolutions.jcs.conflate.boundarymatch.SegmentMatcher;

public class MatchedShell
{

  public class MatchedSegment
  {
    boolean isSameMatch = false;
    boolean isBoundaryMatch = false;

    boolean isMatchedDifferent(MatchedSegment ms)
    {
      if (isSameMatch != ms.isBoundaryMatch) return true;
      if (isBoundaryMatch != ms.isSameMatch) return true;
      return false;
    }
  }

  private static final GeometryFactory geomFactory = new GeometryFactory();

  private Coordinate[] pts;
  private MatchedSegment[] matchSegs;

  public MatchedShell(Coordinate[] pts)
  {
    this.pts = pts;
    matchSegs = new MatchedSegment[pts.length - 1];
    for (int i = 0; i < matchSegs.length; i++) {
      matchSegs[i] = new MatchedSegment();
    }
  }

  public void matchSame(MatchedShell shell, SegmentMatcher segMatcher)
  {
    for (int i = 0; i < matchSegs.length; i++) {
      for (int j = 0; j < shell.matchSegs.length; j++) {
        if (! isSegmentMatch(i, shell, j, segMatcher))
          continue;
        // record match appropriately
        matchSegs[i].isSameMatch = true;
        shell.matchSegs[j].isSameMatch = true;
      }
    }
  }

  public void matchBoundary(MatchedShell shell, SegmentMatcher segMatcher)
  {
    for (int i = 0; i < matchSegs.length; i++) {
      for (int j = 0; j < shell.matchSegs.length; j++) {
        if (! isSegmentMatch(i, shell, j, segMatcher))
          continue;
        // record match appropriately
        matchSegs[i].isBoundaryMatch = true;
        shell.matchSegs[j].isBoundaryMatch = true;
      }
    }
  }

  private boolean isSegmentMatch(int i0,
                                 MatchedShell shell, int i1,
                                 SegmentMatcher segMatcher)
  {
    return segMatcher.isMatch(
          pts[i0],
          pts[i0 + 1],
          shell.pts[i1],
          shell.pts[i1 + 1] );
  }

  /**
   * Implements circular index for matchedSegments
   * @param i
   * @return
   */
  private int getSegmentIndex(int i)
  {
    if (i < 0) return matchSegs.length - 1;
    if (i > matchSegs.length - 1) return 0;
    return i;
  }
  private boolean isExtremalBoundarySegment(int i)
  {
    MatchedSegment prev = matchSegs[getSegmentIndex(i - 1)];
    MatchedSegment curr = matchSegs[getSegmentIndex(i - 1)];
    MatchedSegment next = matchSegs[getSegmentIndex(i + 1)];

    boolean isExtremal = curr.isMatchedDifferent(prev) || curr.isMatchedDifferent(next);
    return isExtremal && curr.isBoundaryMatch;
  }

  public List getExtremalBoundarySegments()
  {
    List segGeom = new ArrayList();
    for (int i = 0; i < matchSegs.length; i++) {
      if (isExtremalBoundarySegment(i)) {
        LineSegment seg = new LineSegment(pts[i], pts[i + 1]);

        segGeom.add(LineSegmentUtil.asGeometry(geomFactory, seg));
      }
    }
    return segGeom;
  }

}
