package io.github.ngspace.nnupref;

@FunctionalInterface public interface ChangedSettingsListener {
	public void changedValue(String key, Object newVal, Object oldVal, NNUPref nnupref);
}
