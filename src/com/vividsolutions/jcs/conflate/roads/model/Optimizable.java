package com.vividsolutions.jcs.conflate.roads.model;

import com.vividsolutions.jcs.jump.util.Block;

public interface Optimizable {
	public abstract void doOptimizedOp(Block op);
}