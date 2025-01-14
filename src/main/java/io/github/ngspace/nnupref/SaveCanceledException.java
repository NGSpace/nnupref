package io.github.ngspace.nnupref;

/**
 * Thrown when a {@link NNUPref#save()} call is distrupted by a one of the SavePrefListeners returning false
 * and canceling the operation.
 */
public class SaveCanceledException extends RuntimeException {
	/**
	 * Constructs the exception
	 * @param string - the message
	 */
	public SaveCanceledException(String string) {super(string);}

	private static final long serialVersionUID = -2719284987522366489L;
	
}
