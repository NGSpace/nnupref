package io.github.ngspace.nnupref;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultValueProcessor implements IValueProcessor {
	
	/**
	 * The default implementation of {@link IValueProcessor}
	 */
	public DefaultValueProcessor() {/**/}

	@Override
	public Object readValue(byte[] valuee, int line) {
		
		String value = new String(valuee).trim();
		
		
		
		if (value.isEmpty()) throw new ValueProcessingException("Invalid arguement, Len=" + valuee.length + " value=" +
				Arrays.toString(valuee), line);

		// Chars and null
		if (value.matches("'.'")) return value.charAt(1);
		if (value.equals("null")) return null;
		
		
		
		// Numbers
		if (value.matches("((0x|#)[\\daAbBcCdDeEfF]+|[-+]*\\d*(\\.?(\\d+)?[ILDBFS]?))")) return parseNumber(value);
		
		
		
		// Booleans
		if (value.equalsIgnoreCase("false")) return false;
		if (value.equalsIgnoreCase("true")) return true;
		
		
		
		// Strings
		if (value.startsWith("\"")||value.endsWith("\"")) {
			
			boolean isString = true;
			
			// Probably String :D
			value = value.substring(1,value.length()-1);
			StringBuilder string = new StringBuilder();
			
			boolean safe = false;
			for (int i = 0;i<value.length();i++) {
				char c = value.charAt(i);
				if (c=='n'&&safe) {
					string.append('\n');
					continue;
				}
				if (c=='\\'&&!safe) safe = true; else {
					if (c=='"'&&!safe) {isString=false;break;} // Not String ;_;
					safe = false;
					string.append(c);
				}
			}
			// String! :D
			if (isString) return string.toString();
		}
		
		

		// Array
		// Accepts the follow format: "[(any char)]"
		if (value.matches("\\[[\\s\\S]*\\]")) {
			List<byte[]> args = NNUPrefUtils.processParemeters(Arrays.copyOfRange(valuee, 1, valuee.length-1));
			Object[] processedarg = new Object[args.size()];
			
			for (int i = 0;i<args.size();i++) {
				processedarg[i] = readValue(args.get(i), line);
			}
			
			return processedarg;
		}
		
		
		
		// Map
		// Accepts the follow format: "{(any char)}"
		if (value.matches("\\{[\\s\\S]*\\}")) {
			List<byte[]> args = NNUPrefUtils.processParemeters(Arrays.copyOfRange(valuee, 1, valuee.length-1));
			HashMap<Object, Object> map = new HashMap<>();
			
			for (int i = 0;i<args.size();i++) {
				
				String[] keyandval = new String(args.get(i)).split("=", 2);
				
				int start = (keyandval[0]).getBytes().length+1;
				int valueelength = start + keyandval[1].getBytes().length;
				
				byte[] bytes = Arrays.copyOfRange(args.get(i),start,valueelength);

				map.put(keyandval[0].trim(), readValue(bytes, line));
			}
			
			return map;
		}
		
		if (value.startsWith("Obj")) {
			try {
				byte[] bytes = Arrays.copyOfRange(valuee, 3, valuee.length);
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
				return inputStream.readObject();
			} catch (Exception e) {
				e.printStackTrace();
				throw new ValueProcessingException(e, value, line);
			}
		}
		
		throw new ValueProcessingException(value, line);
	}

	private Number parseNumber(String value) {
		if (value.startsWith("0x")||value.startsWith("#")) return Long.decode(value);
		else {
			char lastchar = value.charAt(value.length()-1);
			String val = value.substring(0,value.length()-(Character.isDigit(lastchar)?0:1));
			return switch (lastchar) {//ILDBFS
				case 'I': yield Integer.parseInt(val);
				case 'L': yield Long.parseLong(val);
				case 'B': yield Byte.parseByte(val);
				case 'F': yield Float.parseFloat(val);
				case 'S': yield Short.parseShort(val);
				default:// D
					yield Double.parseDouble(val);
			};
		}
	}

	@Override
	public byte[] writeValue(Object value) throws IOException {
		return switch (value) {
			case Integer n: yield (n+"I").getBytes();
			case Long    n: yield (n+"L").getBytes();
			case Double  n: yield (n+"D").getBytes();
			case Byte    n: yield (n+"B").getBytes();
			case Float   n: yield (n+"F").getBytes();
			case Short   n: yield (n+"S").getBytes();
			
			case Number num: yield String.valueOf(num.doubleValue()).getBytes();
			case String str: yield ('"'+str.replace("\"", "\\\"").replace("\n", "\\n")+'"').getBytes();
			case Character c: yield ("'"+c.charValue()+"'").getBytes();
			case Boolean b: yield String.valueOf(b).getBytes();

			case long[] o: yield Arrays.toString(o).getBytes();
			case int[] o: yield Arrays.toString(o).getBytes();
			case byte[] o: yield Arrays.toString(o).getBytes();
			case short[] o: yield Arrays.toString(o).getBytes();
			case float[] o: yield Arrays.toString(o).getBytes();
			case double[] o: yield Arrays.toString(o).getBytes();
			case boolean[] o: yield Arrays.toString(o).getBytes();
			case char[] o: yield Arrays.toString(o).getBytes();

			case Object[] o: {
				byte[] bytes = new byte[0];
				bytes = addToArray(bytes, "[".getBytes());
				for (int i = 0;i<o.length;i++) {
					bytes = addToArray(bytes, writeValue(o[i]));
					if (i+1!=o.length)bytes = addToArray(bytes, ", ".getBytes());
				}
				bytes = addToArray(bytes, "]".getBytes());
				yield bytes;
			}
			
			case Map<?, ?> m: {
				byte[] bytes = new byte[1];
				bytes[0] = '{';
				var v = new ArrayList<>(m.entrySet());
				for (int i = 0;i<v.size();i++) {
					Entry<?, ?> entry = v.get(i);
					bytes = addToArray(bytes, entry.getKey().toString().getBytes());
					bytes = addToArray(bytes, "=".getBytes());
					bytes = addToArray(bytes, writeValue(entry.getValue()));
					if (i+1!=v.size())bytes = addToArray(bytes, ", ".getBytes());
				}
				bytes = addToArray(bytes, "}".getBytes());
				yield bytes;
			}
			
			case Serializable s: {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				for (byte b : "Obj".getBytes()) output.write(b);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(output);
				objectOutputStream.writeObject(s);
				objectOutputStream.close();
				yield output.toByteArray();
			}
			default:
				throw new IllegalArgumentException("Object of type " + value.getClass()
					+ " does not implement Serializable and therefore could not be written to disk");
		};
	}
	private static byte[] addToArray(byte[] arr, byte[] t) {
		byte[] newarr = Arrays.copyOf(arr, arr.length+t.length);
		for (int i = 0;i<t.length;i++) {
			newarr[arr.length+i] = t[i];
		}
		return newarr;
	}

	@Override public boolean isCompatibleType(Object value) {
		return value==null || value instanceof Serializable || value.getClass().isArray();
	}
	
}
