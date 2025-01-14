package io.github.ngspace.nnupref;

import java.io.File;

/**
 * Event listner for when {@link NNUPref#save()} is called.
 */
@FunctionalInterface public interface SavePrefListener {
	/**
	 * Triggers when {@link NNUPref#save()} is called.
	 * @param file - the file that's about to be written to.
	 * @return whether NNUPref should continue the save operation.
	 */
	public boolean shouldContinue(File file);
}
