package io.github.ngspace.nnupref;

/**
 * Wrapper for exceptions that occured in a method that is an implementation of a Map method
 */
public class MapWrapperMethodException extends RuntimeException {
	
	private static final long serialVersionUID = -8632764025580817681L;

	/**
	 * Constructs the exception
	 * @param cause - the wrapped exception
	 */
	public MapWrapperMethodException(Throwable cause) {super(cause);}
}
