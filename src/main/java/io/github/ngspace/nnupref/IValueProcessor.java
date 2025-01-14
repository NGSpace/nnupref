package io.github.ngspace.nnupref;

import java.io.IOException;

/**
 * The object tasked with processing an array of bytes (which might or might not be a valid string) and writing objects
 * to an array of bytes for the NNUPref object to write to disk.
 */
public interface IValueProcessor {
	/**
	 * Reads an array of bytes (which can be created using {@link #writeValue(Object)}) and builds an Object depending
	 * on what those bytes are.
	 * @param value - The array of bytes
	 * @param line - The line currently being read (Used for debugging purposes)
	 * @return The constructed object
	 * @throws ValueProcessingException if the provided byte array can't be understood by the implementation
	 */
	public Object readValue(byte[] value, int line) throws ValueProcessingException;
	/**
	 * Constructs the given object in the form of an array of bytes that can be used to reconstruct the same or a similar
	 * object using {@link #readValue(byte[], int)}
	 * @param value - The object to construct
	 * @return an array of bytes that can be used to reconstruct the object later
	 * @throws ValueProcessingException if fails to construct the byte array
	 * @throws IOException if fails to construct the byte array
	 */
	public byte[] writeValue(Object value) throws IOException;
	/**
	 * Is the type of the given object compatible with this implementation of the IValueProcessor
	 * @param value - the object
	 * @return boolean
	 */
	public boolean isCompatibleType(Object value);
	/**
	 * The name of this IValueProcessor implementation
	 * @return The name of this IValueProcessor implementation
	 */
	public default String getValueProcessorName() {return getClass().getSimpleName();}
}
