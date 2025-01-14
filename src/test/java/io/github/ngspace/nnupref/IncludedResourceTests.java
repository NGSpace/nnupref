package io.github.ngspace.nnupref;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

class IncludedResourceTests {
	@Test void buildPrefWithInternalResource() {
		assertDoesNotThrow(() -> {
			NNUPrefBuilder builder = new NNUPrefBuilder(IncludedResourceTests.class.getResourceAsStream("/testpref.npref"));
			builder.build();
		});
	}
    @Test void primitives() throws IOException {
    	NNUPref pref = buildNormalNNUPref("primitives.npref");
        assertEquals(1,pref.getInt("numba1"));
        assertEquals(2d,pref.getDouble("numba2"));
        assertEquals('g',pref.getChar("TheLetterH"));
    }
    @Test void map() throws IOException {
    	NNUPref pref = buildNormalNNUPref("map.npref");
    	Map<String, Object> m = pref.getMap("map");
        assertEquals(1d,m.get("testval"));
        assertEquals("UwU",m.get("otherval"));
    }
    @Test void objects() throws IOException {
    	NNUPref pref = buildNormalNNUPref("objects.npref");
    	assertDoesNotThrow(() -> assertInstanceOf(Color.class,pref.getOfType("Fak", Color.class)));
    	Map<String, Object> m = pref.getMap("map");
        assertInstanceOf(Color.class,m.get("testval"));
    }
    
    NNUPref buildNormalNNUPref(String filename) throws IOException {
		NNUPrefBuilder builder = new NNUPrefBuilder(IncludedResourceTests.class.getResourceAsStream("/"+filename));
		return builder.build();
    }
}
