package io.github.ngspace.nnupref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class NNUPrefUtils {private NNUPrefUtils() {}
	
	static List<byte[]> processParemeters(byte[] strtoprocess) {
		if (strtoprocess.length==0) return new ArrayList<byte[]>();
		
		int parentheses = 0;
		int squareparentheses = 0;

		byte[] parameterBuilder = new byte[0];
		List<byte[]> tokenizedParemeters = new ArrayList<byte[]>();
		for (int i = 0;i<strtoprocess.length;i++) {
			char c = (char)strtoprocess[i];
			if (c==','&&parentheses==0&&squareparentheses==0) {
				tokenizedParemeters.add(parameterBuilder);
				parameterBuilder = new byte[0];
				continue;
			}
			if (c=='"') {
				parameterBuilder=append(parameterBuilder,'"');
				i++;
				boolean safe = false;
				for (;i<strtoprocess.length;i++) {
					c = (char) strtoprocess[i];
					if (!safe) {
						if (c=='\\') {safe = true;continue;}
					} else {
						if (c=='n') parameterBuilder=append(parameterBuilder,'\n');
						else if (c=='"') parameterBuilder=addToArray(parameterBuilder,"\\\"".getBytes());
						else if (c=='\\') parameterBuilder=append(parameterBuilder,'\\');
						else parameterBuilder=append(parameterBuilder,c);
						safe = false;
						continue;
					}
					parameterBuilder=append(parameterBuilder,c);
					if (c=='"') {
						break;
					}
				}
				continue;
			}
			if (c=='(') parentheses++;
			if (c==')') parentheses--;
			if (c=='[') squareparentheses++;
			if (c==']') squareparentheses--;
			
			parameterBuilder=append(parameterBuilder,c);
		}
		tokenizedParemeters.add(parameterBuilder);
		return tokenizedParemeters;
	}
	private static byte[] addToArray(byte[] arr, byte t) {
		byte[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
	private static byte[] addToArray(byte[] arr, byte[] t) {
		byte[] newarr = Arrays.copyOf(arr, arr.length+t.length);
		for (int i = 0;i<t.length;i++) {
			newarr[arr.length+i] = t[i];
		}
		return newarr;
	}
	private static byte[] append(byte[] arr, char c) {
		return addToArray(arr, (byte)c);
	}
}
