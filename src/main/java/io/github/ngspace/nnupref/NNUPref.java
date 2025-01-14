package io.github.ngspace.nnupref;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A small, fast and simple way to save user pereferences and other information without using too many resources.
 */
public class NNUPref implements Map<String, Object>, Serializable {
	
	private static final long serialVersionUID = -8607191564016108287L;
	
	/**
	 * The Map that holds all the values in this NNUPref instance
	 */
	private Map<String,Object> map = new HashMap<String,Object>();
	/**
	 * All the change listeners
	 */
	private List<ChangedPrefListener> changeListers = new ArrayList<ChangedPrefListener>();
	/**
	 * All the save listeners
	 */
	private List<SavePrefListener> saveListers = new ArrayList<SavePrefListener>();
	
	
	/**
	 * The file to be written to when {@link #save()} is called
	 */
	private File file = null;
	/**
	 * Should automatically save when changes occur
	 */
	private boolean autoSave;
	/**
	 * The IValueProcessor to use to read and write values
	 */
	private IValueProcessor valueProcessor;
	/**
	 * Should use a save method that does not overwrites the npref file without first writting all the objects to disk
	 */
	private boolean safeSave;
	/**
	 * Should check if the IValueProcessor instance is compatible with the objects given to {@link #set(String, Object)}
	 */
	private boolean typeChecking;
	
	
	/**
	 * Constructs an NNUPref object
	 * @param file - The default file, can be null.
	 * @param stream - The stream to read from, can be null.
	 * @param autoSave - should autosave
	 * @param autocreatefile - should automatically create file
	 * @param valueProcessor - the valueprocessor
	 * @param safeSave - should save safely
	 * @param typeChecking - should do typechecking
	 * @throws IOException - if fails to write or process the file/inputstream
	 */
	public NNUPref(File file, InputStream stream, boolean autoSave, boolean autocreatefile,
			IValueProcessor valueProcessor, boolean safeSave, boolean typeChecking) throws IOException {
		this.file = file;
		this.autoSave = autoSave;
		this.safeSave = safeSave;
		this.valueProcessor = valueProcessor;
		this.typeChecking = typeChecking;
		
		// Create the map
		map = new HashMap<String,Object>();
		
		// Is file set?
		if (file!=null) {

			// If file doesn't exit, create it.
			if (autocreatefile&&!file.exists()&&file.createNewFile()) save();
			
			// Process file
			read(new FileInputStream(file));
		}
		
		// Process InputStream if exists
		if (stream!=null) read(stream);
	}
	
	
	
	/**
	 * Reads the given InputStream, parses the key and gives the value to the IValueProcessor set in this object.
	 * @param ins - the InputStream to read
	 * @throws IOException if fails to read the InputStream
	 */
	public void read(InputStream ins) throws IOException {
		ArrayList<byte[]> lines = new ArrayList<byte[]>();
		int byt = -1;
		while ((byt=ins.read())!=-1) {
			List<Byte> bytes = new ArrayList<Byte>();
			bytes.add((byte)byt);
			while ((byt=ins.read())!=-1&&((char)byt)!='\n') {
				bytes.add((byte)byt);
			}
	    	byte[] b = new byte[bytes.size()];
	    	for (int i = 0;i<b.length;i++) b[i]=bytes.get(i);
			lines.add(b);
		}
		ins.close();
	    process(lines);
	}
	
	
	
	private void process(ArrayList<byte[]> ls) {
		if (map==null) map = new HashMap<String,Object>();
		for (int i = 0;i<ls.size();i++) {
			String str = new String(ls.get(i));
			
			if (str.trim().isEmpty()) continue;
			if (str.charAt(0)=='#') continue;
			
			int line = i;
			
			String[] keyandvalue = str.split("=", 2);
			
			if (keyandvalue[1].length()>2)
				while (keyandvalue[1].charAt(keyandvalue[1].length()-1)=='\\') {
					i++;
					keyandvalue[1]=keyandvalue[1].substring(0, keyandvalue[1].length() - 1) + str;
				}
			int start = 0;
			for (int j = 0;j<ls.get(i).length;j++) {
				if ((ls.get(i)[j])=='=') {start = j+1;break;}
			}
			
			byte[] bytes = Arrays.copyOfRange(ls.get(i), start, ls.get(i).length);
			
			map.put(keyandvalue[0], valueProcessor.readValue(bytes, line));
			
		}
	}
	
	
	
	/**
	 * Writes the values of this NNUPref instance to disk using the set IValueProcessor
	 * @throws IOException if fails to write to disk
	 * @throws NullPointerException if a file is not set for this NNUPref instance
	 * @throws SaveCanceledException if this save operation was canceled by an event listener
	 */
	public void save() throws IOException, NullPointerException, SaveCanceledException {
		if (file==null) throw new NullPointerException("File is not set for NNUPref.");
		for (var v : saveListers)
			if (!v.shouldContinue(file))
				throw new SaveCanceledException("NNUPref save canceled by event listener");
		if (safeSave) saveSafe(); else saveUnsafe();
	}
	
	
	private void saveUnsafe() throws IOException {
		FileOutputStream fw = new FileOutputStream(file);
		
		for (Entry<String, Object> entry : map.entrySet()) {
			fw.write((entry.getKey() + "=").getBytes());
			
			if (entry.getValue()==null) continue;
			
			byte[] bytes = valueProcessor.writeValue(entry.getValue());
			fw.write(bytes);
			fw.write("\n".getBytes());
		}
		
		fw.flush();
		fw.close();
	}
	
	
	private void saveSafe() throws IOException {
		ByteArrayOutputStream safeoutput = new ByteArrayOutputStream();
		
		for (Entry<String, Object> entry : map.entrySet()) {
			safeoutput.write((entry.getKey() + "=").getBytes());
			
			if (entry.getValue()==null) continue;
			
			byte[] bytes = valueProcessor.writeValue(entry.getValue());
			safeoutput.write(bytes);
			safeoutput.write("\n".getBytes());
		}
		safeoutput.flush();
		safeoutput.close();
		FileOutputStream fileoutput = new FileOutputStream(file);
		fileoutput.write(safeoutput.toByteArray());
		fileoutput.flush();
		fileoutput.close();
	}
	
	
	
	/**
	 * Sets the given key to the given value
	 * @param key - the key
	 * @param value - the value
	 * @throws NullPointerException if autosave is enabled and file is set to null
	 * @throws IncompatibleTypeException if typechecking is enabled and the IValueProcessor doesn't support the type of
	 * the given value
	 * @throws IOException if autosave is enabled and fails to write to file
	 */
	public void set(String key, Object value) throws NullPointerException, IncompatibleTypeException, IOException {
		if (typeChecking&&!valueProcessor.isCompatibleType(value))
			throw new IncompatibleTypeException("Value processor " + valueProcessor.getValueProcessorName()
				+ " is not compatible with objects of type " + (value==null?"null":value.getClass().getName()));
		Object oldval = get(key);
		map.put(key, value);
		for (var lis : changeListers) lis.changedValue(key, value, oldval);
		if (autoSave) save();
	}
	
	
	
	/**
	 * Removes the key from this NNUPref instance
	 * @param key - the key
	 * @throws NullPointerException if autosave is enabled and file is set to null
	 * @throws IOException if autosave is enabled and fails to write to file
	 */
	public void remove(String key) throws NullPointerException, IOException {
		Object oldval = get(key);
		map.remove(key);
		for (var lis : changeListers) lis.changedValue(key, null, oldval);
		if (autoSave) save();
	}



	/**
	 * Add an event listener for when a value is changed
	 * @param listener - the listener
	 */
	public void addChangeValueListener(ChangedPrefListener listener) {changeListers.add(listener);}
	/**
	 * Add an event listener for when {@link #save()} is called
	 * @param listener - the listener
	 */
	public void addSaveToFileListener(SavePrefListener listener) {saveListers.add(listener);}
	
	
	
	private Object get(String key) {return map.get(key);}
	
	
	
	/**
	 * Returns a boolean
	 * @param key - the key
	 * @return a boolean
	 */
	public boolean getBoolean(String key) {return (Boolean)get(key);}
	/**
	 * Returns a char
	 * @param key - the key
	 * @return a char
	 */
	public char getChar(String key) {return getOfType(key, Character.class);}
	/**
	 * Returns a String
	 * @param key - the key
	 * @return a String
	 */
	public String getString(String key) {return getOfType(key, String.class);}
	
	
	/**
	 * Returns an int
	 * @param key - the key
	 * @return an int
	 */
	public int getInt(String key) {return getOfType(key, Integer.class).intValue();}
	/**
	 * Returns a Long
	 * @param key - the key
	 * @return a Long
	 */
	public long getLong(String key) {return getOfType(key, Long.class).longValue();}
	/**
	 * Returns a byte
	 * @param key - the key
	 * @return a byte
	 */
	public byte getByte(String key) {return getOfType(key, Byte.class).byteValue();}
	/**
	 * Returns a short
	 * @param key - the key
	 * @return a short
	 */
	public short getShort(String key) {return getOfType(key, Short.class).shortValue();}
	/**
	 * Returns a float
	 * @param key - the key
	 * @return a float
	 */
	public float getFloat(String key) {return getOfType(key, Float.class).floatValue();}
	/**
	 * Returns a double
	 * @param key - the key
	 * @return a double
	 * @see #getDoubleSafe(String)
	 */
	public double getDouble(String key) {return getOfType(key, Double.class).doubleValue();}
	
	
	/**
	 * Returns a double (Compatible with any number)
	 * @param key - the key
	 * @return a double
	 */
	public double getDoubleSafe(String key) {return getOfType(key, Number.class).doubleValue();}
	
	
	/**
	 * Returns an object array
	 * @param key - the key
	 * @return an array
	 */
	public Object[] getArray(String key) {return (Object[])get(key);}
	/**
	 * Returns a Map
	 * @param key - the key
	 * @return a Map
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getMap(String key) {return getOfType(key,Map.class);}
	/**
	 * Returns a value of any type
	 * @param <T> - the type
	 * @param key - the key
	 * @param clazz - the class of the requested type
	 * @return the value
	 */
	public <T> T getOfType(String key, Class<T> clazz) {return clazz.cast(get(key));}


	
	/**
	 * If this NNUPref instance contains the requested key
	 * @param key - the key
	 * @return whether it contains the key or not
	 */
	public boolean containsKey(String key) {return get(key)!=null;}
	public boolean containsValue(Object value) {return map.containsValue(value);}
	
	
	
	/**
	 * Writes all the values in the given map to this NNUPref instance.
	 * @param m - the map
	 * @throws NullPointerException if autosave is enabled and file is set to null
	 * @throws IOException if autosave is enabled and fails to write to file
	 */
	public void setAll(Map<String, Object> m) throws NullPointerException, IOException {
		for (var v : m.entrySet()) set(v.getKey(), v.getValue());
	}



	/**
	 * Removes all values in this NNUPref instance
	 * @throws NullPointerException if autosave is enabled and file is set to null
	 * @throws IOException if autosave is enabled and fails to write to file
	 */
	public void removeAll() throws NullPointerException, IOException {
		for (var key : keySet()) remove(key);
	}
	@Override public void clear() {
		try {
			removeAll();
		} catch (IOException e) {
			throw new MapWrapperMethodException(e);
		}
	}
	
	/**
	 * Map Methods
	 */
	



	@Override public int size() {return map.size();}
	@Override public boolean isEmpty() {return map.isEmpty();}




	/**
	 * @deprecated, please use {@link #setAll(Map<String, Object>)}.
	 * <br><br>
	 * This method was added to implement the Map interface
	 */
	@Deprecated(forRemoval = false, since = "1.0.0")
	@Override public boolean containsKey(Object key) {return containsKey(String.valueOf(key));}



	/**
	 * @deprecated, please use {@link #get(String)}.
	 * <br><br>
	 * This method was added to implement the Map interface
	 */
	@Deprecated(forRemoval = false, since = "1.0.0")
	@Override public Object get(Object key) {return get(String.valueOf(key));}




	/**
	 * @deprecated, please use {@link #put(String, Object)}.
	 * <br><br>
	 * This method was added to implement the Map interface
	 */
	@Deprecated(forRemoval = false, since = "1.0.0")
	@Override public Object put(String key, Object value) {
		Object val = get(key);
		try {
			set(key, value);
		} catch (IOException e) {
			throw new MapWrapperMethodException(e);
		}
		return val;
	}

	

	/**
	 * @deprecated, please use {@link #remove(String)}.
	 * <br><br>
	 * This method was added to implement the Map interface
	 */
	@Deprecated(forRemoval = false, since = "1.0.0")
	@Override public Object remove(Object key) {
		Object val = get(key);
		remove(key);
		return val;
	}

	

	/**
	 * @deprecated, please use {@link #setAll(Map<String, Object>)}.
	 * <br><br>
	 * This method was added to implement the Map interface
	 */
	@Deprecated(forRemoval = false, since = "1.0.0")
	@Override public void putAll(Map<? extends String, ? extends Object> m) {
		try {
			setAll(map);
		} catch (IOException e) {
			throw new MapWrapperMethodException(e);
		}
	}



	@Override public Set<String> keySet() {return map.keySet();}
	@Override public Collection<Object> values() {return map.values();}
	@Override public Set<Entry<String, Object>> entrySet() {return map.entrySet();}
}