package com.vividsolutions.jcs.plugin.conflate.roads;

import java.util.*;

/**
 * <p> </p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public interface RoadMatchModelListener extends EventListener {
  public void dataChanged(RoadMatchModelEvent e);
}