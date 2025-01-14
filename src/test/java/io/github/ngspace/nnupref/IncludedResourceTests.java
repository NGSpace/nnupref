package io.github.ngspace.nnupref;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

class IncludedResourceTests {
	@Test void processPref() {
		assertDoesNotThrow(() -> {
			NNUPrefBuilder builder = new NNUPrefBuilder(AppTest.class.getResourceAsStream("/testpref.npref"));
			builder.build();
		});
	}
    @Test void inputStream() throws IOException {
    	NNUPref pref = buildNormalNNUPref("testpref.npref");
        assertEquals(1,pref.getInt("numba1"));
    }
    @Test void map() throws IOException {
    	NNUPref pref = buildNormalNNUPref("testpref.npref");
    	Map<?, ?> m = (Map<?, ?>) pref.get("map");
        assertEquals(1d,m.get("testval"));
        assertEquals("UwU",m.get("otherval"));
    }
    
    NNUPref buildNormalNNUPref(String filename) throws IOException {
		NNUPrefBuilder builder = new NNUPrefBuilder(AppTest.class.getResourceAsStream("/"+filename));
		return builder.build();
    }
}
