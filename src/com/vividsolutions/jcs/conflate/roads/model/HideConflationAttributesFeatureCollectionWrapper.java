package com.vividsolutions.jcs.conflate.roads.model;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jcs.jump.feature.Feature;
import com.vividsolutions.jcs.jump.feature.FeatureCollection;
import com.vividsolutions.jcs.jump.feature.FeatureSchema;
import java.io.Serializable;
import java.util.*;

public class HideConflationAttributesFeatureCollectionWrapper implements
		FeatureCollection, Serializable {
	private FeatureSchema schema;

	private List features = new ArrayList();

	public HideConflationAttributesFeatureCollectionWrapper(
			FeatureCollection featureCollection) {
		schema = new FeatureSchema();
		Map newIndexToOldIndexMap = new HashMap();
		int j = 0;
		for (int i = 0; i < featureCollection.getFeatureSchema()
				.getAttributeCount(); i++) {
			// true to handle a copy of a Result layer being fed in as input,
			// false to handle a copy of a Source layer being fed in as input.
			// [Jon Aquino 2004-09-09]
                    // FIXME: handle this case, not depending on JUMP plugin
//			if (FUTURE_CollectionUtil.concatenate(
//					GenerateResultLayerPlugIn.conflationAttributeNames(true),
//					GenerateResultLayerPlugIn.conflationAttributeNames(false))
//					.contains(
//							featureCollection.getFeatureSchema()
//									.getAttributeName(i))) {
//				continue;
//			}
			schema.addAttribute(featureCollection.getFeatureSchema()
					.getAttributeName(i), featureCollection.getFeatureSchema()
					.getAttributeType(i));
			newIndexToOldIndexMap.put(new Integer(j), new Integer(i));
			j++;
		}
		for (Iterator i = featureCollection.iterator(); i.hasNext();) {
			Feature feature = (Feature) i.next();
			features.add(new HideConflationAttributesFeature(feature,
					newIndexToOldIndexMap, schema));
		}
	}

	public FeatureSchema getFeatureSchema() {
		return schema;
	}

	public Envelope getEnvelope() {
		throw new UnsupportedOperationException();
	}

	public int size() {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	public List getFeatures() {
		throw new UnsupportedOperationException();
	}

	public Iterator iterator() {
		return features.iterator();
	}

	public List query(Envelope envelope) {
		throw new UnsupportedOperationException();
	}

	public void add(Feature feature) {
		throw new UnsupportedOperationException();
	}

	public void addAll(Collection features) {
		throw new UnsupportedOperationException();
	}

	public void removeAll(Collection features) {
		throw new UnsupportedOperationException();
	}

	public void remove(Feature feature) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public Collection remove(Envelope env) {
		throw new UnsupportedOperationException();
	}

}