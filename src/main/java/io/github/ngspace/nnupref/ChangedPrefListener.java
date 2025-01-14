package io.github.ngspace.nnupref;

@FunctionalInterface public interface ChangedPrefListener {
	public void changedValue(String key, Object newVal, Object oldVal);
}
