package io.github.ngspace.nnupref;

import java.io.File;

@FunctionalInterface
public interface SavePrefListener {
	public boolean shouldContinue(File f);
}
