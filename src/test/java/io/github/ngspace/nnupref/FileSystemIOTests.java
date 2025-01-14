package io.github.ngspace.nnupref;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

class FileSystemIOTests {
	NNUPref buildPref(String name, boolean auto, boolean safe) throws IOException {
		File testsdir = new File("tests");
		if (!testsdir.exists()) testsdir.mkdir();
		NNUPrefBuilder prefbuilder = new NNUPrefBuilder(new File("tests/"+name));
		prefbuilder.setAutocreatemissingfile(auto);
		prefbuilder.setAutosave(auto);
		prefbuilder.setSafesave(safe);
		return prefbuilder.build();
	}
	private void delete(String string) {
		File testsdir = new File("tests/"+string);
		if (testsdir.exists()) testsdir.delete();
	}

	@Test void safeWriteObjects() throws IOException {
		delete("objects");
		NNUPref pref = buildPref("objects", true, true);
		
		
		// Serializable
		Object serializable = new Color(120);
		assertDoesNotThrow(() -> pref.set("Serializable", serializable));
		
		
		// Not serializable (Throws exception)
		Object notserializableobject = new Object();
		assertThrows(IncompatibleTypeException.class, () -> pref.set("NotSerializable", notserializableobject));
		
		
		// Check if NNUPref is not fucked.
		assertDoesNotThrow(pref::save);
		
		
		// Check if file was written correctly and is read correctly
		assertDoesNotThrow(()->buildPref("objects", true, true));
	}
	@Test void primitives() throws IOException {
		delete("primitives");
		NNUPref pref = buildPref("primitives", true, true);
		
		
		// Write data
		assertDoesNotThrow(() -> {
			pref.set("Number", 1);
			pref.set("String", "Test");
			pref.set("Char", 'c');
			pref.set("Boolean", true);
		});
		
		
		
		// Check if file was written correctly and is read correctly
		assertDoesNotThrow(()->{
			NNUPref newpref = buildPref("primitives", true, true);
			assertEquals(1, newpref.getInt("Number"));
			assertEquals("Test", newpref.getString("String"));
			assertEquals('c', newpref.getChar("Char"));
			assertEquals(true, newpref.getBoolean("Boolean"));
		});
	}
	@Test void map() throws IOException {
		delete("map");
		NNUPref pref = buildPref("map", true, true);
		
		
		// Write data
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("test", 2);
		
		assertDoesNotThrow(() -> pref.set("map", map));
		
		
		// Check if file was written correctly and is read correctly
		assertDoesNotThrow(()->{
			NNUPref newpref = buildPref("map", true, true);
			assertEquals(2, newpref.getMap("map").get("test"));
		});
	}
	@Test void array() throws IOException {
		delete("array");
		NNUPref pref = buildPref("array", true, true);
		
		
		// Write data
		Object[] f = {1};
		
		assertDoesNotThrow(() -> pref.set("arr", f));
		
		
		// Check if file was written correctly and is read correctly
		assertDoesNotThrow(()->{
			NNUPref newpref = buildPref("array", true, true);
			assertEquals(1, newpref.getArray("arr")[0]);
		});
	}
}
