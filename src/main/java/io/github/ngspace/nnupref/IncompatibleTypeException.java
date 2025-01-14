package io.github.ngspace.nnupref;

/**
 * Thrown when typechecking is enabled and an object that is not compatible with the current {@link IValueProcessor} is
 * set.
 */
public class IncompatibleTypeException extends RuntimeException {

	/**
	 * Constructs the exception
	 * @param string - the message
	 */
	public IncompatibleTypeException(String string) {super(string);}

	private static final long serialVersionUID = -8963286841882544402L;
	
}
