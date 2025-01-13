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
	private List<ChangedSettingsListener> changeListers = new ArrayList<ChangedSettingsListener>();
	
	private boolean autosave;
	private IValueProcessor valueProcessor;
	private boolean safeSave;
	
	
	
	public NNUPref(File file, InputStream stream, Map<String, Object> defaults, boolean autosave, boolean autocreatefile,
			IValueProcessor valueProcessor) throws IOException {
		this.file = file;
		this.autosave = autosave;
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
	
	
	
	public void save() throws IOException {
		if (file==null) throw new IOException("File not set for NNUPref object.");
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
	private void saveSafe() {
		try {
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
		} catch (IOException e) {
			System.err.println("Encountered IO error, dropping change.");
			
		}
	}
	
	
	
	public Object get(String key) {return map.get(key);}
	public boolean has(String key) {return get(key)!=null;}
	
	
	
	public boolean getBoolean(String key) {return (boolean)get(key);}
	public int getInt(String string) {return ((Number)get(string)).intValue();}
	public double getDouble(String string) {return ((Number)get(string)).doubleValue();}
	
	
	
	public void set(String key, Object value) throws IOException {
		Object oldval = null;
		try {oldval = get(key);} catch (Exception e) {e.printStackTrace();}
		map.put(key, value);
		autoSave();
		dispatchChange(key, value, oldval);
	}
	
	
	
	public void addChangeListener(ChangedSettingsListener listener) {changeListers.add(listener);}
	
	public void dispatchChange(String key, Object value, Object oldval) {
		for (ChangedSettingsListener lis : changeListers) lis.changedValue(key, value, oldval, this);
	}
	
	
	
	public File getFile() {return file;}
	public void setFile(File file) {this.file = file;}
	
	public Map<String, Object> getMap() {return map;}
	public void setMap(Map<String, Object> map) {this.map = map;}
	
	private void autoSave() throws IOException {if (autosave) save();}
	
	
	
}
