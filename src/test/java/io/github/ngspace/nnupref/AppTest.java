package io.github.ngspace.nnupref;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class AppTest {
    @Test void shouldAnswerWithTrue() throws Exception {
    	File file = new File("properties.npref");
    	try {
        	NNUPref pref = new NNUPref(file, null, null, true, true, new DefaultValueProcessor());
        	pref.set("Fak", new Color(24,16,34));
        	pref.set("numba1", 1);
        	Map<String, Object> m = new HashMap<String, Object>();
        	m.put("testval", new Color(25,12,32));
        	pref.set("map", m);
        	
        	NNUPref pref2 = new NNUPref(file, null, null, true, true, new DefaultValueProcessor());
            assertEquals(24,((Color)pref2.get("Fak")).getRed());
            assertEquals(1,pref2.getInt("numba1"));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    }
    @Test void inputStream() throws IOException {
    	NNUPref pref = new NNUPref(null, AppTest.class.getResourceAsStream("/testpref.npref"),
    			null, true, true, new DefaultValueProcessor());
        assertEquals(1,pref.getInt("numba1"));
    }
    @Test void map() throws IOException {
    	NNUPref pref = new NNUPref(null, AppTest.class.getResourceAsStream("/testpref.npref"),
    			null, true, true, new DefaultValueProcessor());
    	Map<?, ?> m = (Map<?, ?>) pref.get("map");
        assertEquals(1d,m.get("testval"));
        assertEquals("UwU",m.get("otherval"));
    }
}
