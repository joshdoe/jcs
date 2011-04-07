package com.vividsolutions.jcs.qa.diff;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;

/**
 * Indexes a MatchCollection to optimize the spatial queries
 * used during matching.
 */
public class MatchIndex
{

  private SpatialIndex index;

  public MatchIndex(MatchCollection matchColl)
  {
    buildIndex(matchColl);
  }

  private void buildIndex(MatchCollection matchColl)
  {
    index = new STRtree();
    for (Iterator i = matchColl.geometryIterator(); i.hasNext(); )
    {
      MatchGeometry matchGeom = (MatchGeometry) i.next();
      index.insert(matchGeom.getGeometry().getEnvelopeInternal(), matchGeom);
    }
  }

  public void testMatch(MatchCollection matchColl, DiffGeometryMatcher diffMatcher)
  {
    for (Iterator i = matchColl.geometryIterator(); i.hasNext(); ) {
      MatchGeometry matchGeom = (MatchGeometry) i.next();
      testMatch(matchGeom, diffMatcher);
    }
  }

  public boolean testMatch(MatchGeometry testGeom, DiffGeometryMatcher diffMatcher)
  {
    diffMatcher.setQueryGeometry(testGeom.getGeometry());

    List resultList = index.query(diffMatcher.getQueryGeometry().getEnvelopeInternal());
    for (Iterator j = resultList.iterator(); j.hasNext(); ) {
      MatchGeometry matchGeom = (MatchGeometry) j.next();
      if (! matchGeom.isMatched()) {
        if (diffMatcher.isMatch(matchGeom.getGeometry())) {
          matchGeom.setMatch(testGeom);
          testGeom.setMatch(matchGeom);
          return true;
        }
      }
    }
    return false;
  }

}