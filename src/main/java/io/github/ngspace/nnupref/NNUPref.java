package io.github.ngspace.nnupref;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NNUPref {
	
	private File file = null;
	private Map<String,Object> map = new HashMap<String,Object>();
	private List<ChangedPrefListener> changeListers = new ArrayList<ChangedPrefListener>();
	private List<SavePrefListener> saveListers = new ArrayList<SavePrefListener>();
	
	private boolean autosave;
	private IValueProcessor valueProcessor;
	private boolean safeSave;
	
	
	
	public NNUPref(File file, InputStream stream, Map<String, Object> defaults, boolean autosave, boolean autocreatefile,
			IValueProcessor valueProcessor, boolean safeSave) throws IOException {
		this.file = file;
		this.autosave = autosave;
		this.safeSave = safeSave;
		this.valueProcessor = valueProcessor;
		
		// Create the map
		map = new HashMap<String,Object>();
		
		// Add all defaults
		if (defaults!=null) map.putAll(defaults);
		
		// Is file set?
		if (file!=null) {

			// If file doesn't exit, create it.
			if (autocreatefile&&!file.exists()&&file.createNewFile()) save();
			
			// Process file
			readAndProcess(new FileInputStream(file));
		}
		
		// Process InputStream if exists
		if (stream!=null) readAndProcess(stream);
	}
	
	
	
	
	protected void readAndProcess(InputStream ins) throws IOException {
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
	
	
	
	protected void process(ArrayList<byte[]> ls) {
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
	
	
	
	public void save() throws IOException, NullPointerException {
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
	
	
	
	public Object get(String key) {return map.get(key);}
	public boolean has(String key) {return get(key)!=null;}
	
	
	
	
	public boolean getBoolean(String key) {return (Boolean)get(key);}
	public char getChar(String key) {return (Character)get(key);}
	public String getString(String key) {return (String)get(key);}
	
	
	public int getInt(String key) {return ((Number)get(key)).intValue();}
	public double getDouble(String key) {return ((Number)get(key)).doubleValue();}
	

	public Object[] getArray(String key) {return (Object[])get(key);}
	@SuppressWarnings("unchecked")
	public Map<Object, Object> getMap(String key) {return (Map<Object, Object>)get(key);}
	public <T> T getOfType(String key, Class<T> clazz) {return clazz.cast(get(key));}
	
	
	
	public void set(String key, Object value) throws IOException {
		Object oldval = null;
		try {oldval = get(key);} catch (Exception e) {e.printStackTrace();}
		map.put(key, value);
		if (autosave) save();
		for (var lis : changeListers) lis.changedValue(key, value, oldval);
	}
	
	

	public void addChangeValueListener(ChangedPrefListener listener) {changeListers.add(listener);}
	public void addSaveToFileListener(SavePrefListener listener) {saveListers.add(listener);}
	
	
	public Map<String, Object> toMap() {return map;}
}
