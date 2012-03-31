package com.vividsolutions.jcs.conflate.roads.model;
import java.io.Serializable;

/**
 * Determines which @SourceRoadSegment to use.
 */
public interface PrecedenceRuleEngine extends Serializable {
    public abstract SourceRoadSegment chooseReference(SourceRoadSegment a,
            SourceRoadSegment b);
}