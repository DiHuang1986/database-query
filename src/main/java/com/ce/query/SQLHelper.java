package com.ce.query;

public class SQLHelper {
	public static String[] generateArrayOfNamedParameters(String name, int quantity) {
		String[] names = new String[quantity];
		
		for(int i=0; i<quantity; i++) {
			names[i] = String.format("%s__%04d", name, i);
		}
		
		return names;
	}
	
	public static String generateArrayOfNamedParameterString(String name, int quantity) {
		String[] names = generateArrayOfNamedParameters(name, quantity);
		
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<quantity; i++) {
			if(i == 0)
				buffer.append(String.format(":%s", names[i]));
			else
				buffer.append(String.format(", :%s", names[i]));
		}
		
		return buffer.toString();
	}
	
}
