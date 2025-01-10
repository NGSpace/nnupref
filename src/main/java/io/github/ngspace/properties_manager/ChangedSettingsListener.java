package io.github.ngspace.properties_manager;

@FunctionalInterface public interface ChangedSettingsListener {
	public void changedValue(String key, Object newVal, Object oldVal, NNUPref nnupref);
}
