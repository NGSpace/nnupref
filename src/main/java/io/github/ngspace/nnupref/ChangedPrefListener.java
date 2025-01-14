package io.github.ngspace.nnupref;

/**
 * Event listner for when a value is changed or removed in an NNUPref object.
 */
@FunctionalInterface public interface ChangedPrefListener {
	/**
	 * Triggered after a value is changed or removed in an NNUPref object
	 * @param key - The key of the value
	 * @param newVal - The new value (null if removed)
	 * @param oldVal - The old value
	 */
	public void changedValue(String key, Object newVal, Object oldVal);
}
