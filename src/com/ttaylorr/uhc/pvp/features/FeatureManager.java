package com.ttaylorr.uhc.pvp.features;

import java.util.ArrayList;

public class FeatureManager {

	ArrayList<PVPFeature> features;

	public FeatureManager() {
		features = new ArrayList<PVPFeature>();
	}

	public ArrayList<PVPFeature> getFeatures() {
		if (this.features.clone() instanceof ArrayList<?>) {
			return (ArrayList<PVPFeature>) this.features.clone();
		}

		return null;
	}

	public ArrayList<PVPFeature> getActiveFeatures() {
		ArrayList<PVPFeature> f = new ArrayList<PVPFeature>();

		for (PVPFeature feature : this.features) {
			if (feature.isEnabled()) {
				f.add(feature.clone());
			}
		}

		return f;
	}

	private boolean setState(String name, boolean state) {
		for (PVPFeature f : features) {
			if (f.getName().equalsIgnoreCase(name)) {
				f.setEnabled(state);
				return true;
			}
		}

		return false;
	}

	public boolean enableFeature(String name) {
		return setState(name, true);
	}

	public boolean disableFeature(String name) {
		return setState(name, false);
	}

	public void addFeature(PVPFeature f) {
		features.add(f);
	}

	public boolean removeAll() {
		try {
			features.clear();
		} catch (Exception e) {
			return false;
		}

		return true;
	}

}
