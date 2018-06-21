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

	public static String normalizeSearchKey(String searchKey) {
		if(searchKey == null || searchKey.trim().length() == 0) {
			return null;
		}

		return "%" + searchKey.trim().toLowerCase() + "%";
	}

	public static String buildInsertSql(String table, String[] attributes) {
		return String.format(
				"insert into %s (%s) values (%s)",
				table,
				join(attributes, ", "),
				join(prepend(attributes, ":"), ", ")
		);
	}

	public static String buildUpdateSql(String table, String[] attributes, String criteria) {
		String[] settings = new String[attributes.length];
		String[] prependedAttributes = prepend(attributes, ":");
		for(int i=0; i<attributes.length; i++) {
			settings[i] = attributes[i] + " = " + prependedAttributes[i];
		}

		return String.format(
				"update %s set %s where %s",
				table,
				join(settings, ", "),
				criteria
		);
	}

	/**
	 * join pieces into one piece and delimited by given delimiter
	 * @param pieces
	 * @param delimiter
	 * @return
	 */
	public static String join(String[] pieces, String delimiter) {
		if(pieces == null) return null;
		if(pieces.length == 0) return null;

		StringBuffer sb = new StringBuffer();
		sb.append(pieces[0]);
		for(int i=1; i<pieces.length; i++) {
			sb.append(delimiter + pieces[i]);
		}

		return sb.toString();
	}

	public static String[] prepend(String[] pieces, String prepend) {
		String[] result = new String[pieces.length];

		for(int i=0; i<pieces.length; i++) {
			result[i] = prepend + pieces[i];
		}

		return result;
	}
}
