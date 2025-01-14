package io.github.ngspace.nnupref;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class AppTest {
    @Test void shouldAnswerWithTrue() throws Exception {
    	File file = new File("properties.npref");
    	try {
    		NNUPrefBuilder prefbuilder = new NNUPrefBuilder(file);
    		prefbuilder.setAutocreatemissingfile(true);
    		prefbuilder.setAutosave(true);
    		NNUPref pref = prefbuilder.build();
        	pref.set("Fak", new Color(24,16,34));
        	pref.set("numba1", 1);
        	Map<String, Object> m = new HashMap<String, Object>();
        	m.put("testval", new Color(25,12,32));
        	pref.set("map", m);
        	
        	pref = prefbuilder.build();
            assertEquals(24,((Color)pref.get("Fak")).getRed());
            assertEquals(1,pref.getInt("numba1"));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    }
}
