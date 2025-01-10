package io.github.ngspace.properties_manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class NNUPref {
	
	protected File file = null;
	protected Map<String,Object> map = new HashMap<String,Object>();
	protected List<ChangedSettingsListener> changeListers = new ArrayList<ChangedSettingsListener>();

	public boolean safecasting;
	public boolean autosave;
	
	
	public NNUPref(File file, Map<String, Object> defaults) throws IOException {
		this.file = file;
		if (!file.exists()&&createfile(file.getAbsolutePath())) {
			map = new HashMap<String,Object>(defaults);
			save();
		}
		process(new FileInputStream(file));
	}
	public NNUPref(InputStream ins) throws IOException {
		process(ins);
	}
	protected NNUPref() {}
	protected boolean createfile(String FilePath) throws IOException {
		try {
			new File(FilePath).getParentFile().mkdirs();
			return new File(FilePath).createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	public void process(File file) throws IOException {
		process(new FileInputStream(file));
	}
	
	protected void process(InputStream ins) throws IOException {
		Scanner myReader = new Scanner(ins);
		ArrayList<String> lines = new ArrayList<String>();
		while (myReader.hasNextLine()) {
			String nextln = myReader.nextLine();
			StringBuilder strb = new StringBuilder(nextln);
			boolean shouldloop = true;
			while (shouldloop&&myReader.hasNextLine()) {
				for (int i = nextln.length()-1;i>0;i--) if (nextln.charAt(i)=='\\') shouldloop = !shouldloop;				
				strb.append(myReader.nextLine());
			}
			lines.add(strb.toString());
		}
	    myReader.close();
	    finalizelist(lines.toArray(new String[lines.size()]));
	}
	protected void finalizelist(String[] ls) throws IOException {
		if (map==null) map = new HashMap<String,Object>();
		for (int i = 0;i<ls.length;i++) {
			try {
				if (ls[i].trim().isEmpty()) continue;
				if (ls[i].charAt(0)=='#') continue;
				String[] kAV = ls[i].split("=", 2);
				int ln = kAV[1].length();
				while (kAV[1].charAt(ln-1)=='\\') {
					i++;
					kAV[1]=kAV[1].substring(0, ln - 1) + ls[i];
					ln = kAV[1].length();
				}
				map.put(kAV[0], kAV[1]);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ReadException(i);
				
			}
			
		}
	}
	public void save() throws IOException {
		if (file==null) throw new IOException("File not set for NNUPref object.");
		FileWriter fw = new FileWriter(file);
		
		for (Entry<String, Object> entry : map.entrySet())
			fw.write(entry.getKey() + "=" + entry.getValue() + "\n");
		
		fw.flush();
		fw.close();
	}
	
	
	public boolean has(String key) {return get(key)!=null;}
	public Object get(String key) {
		return map.get(key);
	}
	
	
	
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
