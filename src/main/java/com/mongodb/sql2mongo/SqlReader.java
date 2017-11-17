package com.mongodb.sql2mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlReader {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Pattern sqlPattern = Pattern.compile("INSERT INTO (([\\w\\d]+?)\\.){0,1}([\\w\\d]*?) \\((.*?)\\) VALUES \\((.*)\\);*", Pattern.CASE_INSENSITIVE);
	private Pattern stringPattern = Pattern.compile("['\"](.*?)['\"]");
	private Pattern floatPattern = Pattern.compile("\\d+\\.\\d+");
	private Pattern booleanPattern = Pattern.compile("(true|false)", Pattern.CASE_INSENSITIVE);

	public Record parse(String line) {
		Record rec = null;

		Matcher matcher = sqlPattern.matcher(line);

		if (matcher.matches()) {
			rec = new Record();
			rec.setDatabase(matcher.group(2));
			rec.setCollection(matcher.group(3));

			String[] fields = matcher.group(4).split("\\s*,\\s*");
			String[] values = split(matcher.group(5));

			if (fields.length != values.length) {
				throw new RuntimeException("Could not parse SQL statement - number of columns does not match number of values" + line);
			}

			for (int i = 0; i < fields.length; i++) {
				rec.addField(fields[i], convertValue(values[i]));
			}
		}
		else {
			logger.warn("Could not parse line: \"{}\"", line);
		}

		return rec;
	}

	private String[] split(String s) {
		List<String> items = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		int depth = 0;
		boolean inString = false;
		char stringChar = '-';

		for (char c : s.toCharArray()) {
			if (c == ',' && depth == 0) {
				items.add(sb.toString().trim());
				sb = new StringBuffer();
				continue;
			}
			else if (c == '"' || c == '\'') {
				if (c == stringChar && inString) {
					depth--;
					stringChar = '-';
					inString = false;
				}
				else if (!inString) {
					depth++;
					stringChar = c;
					inString = true;
				}
			}
			else if (!inString && c == '(') {
				depth++;
			}
			else if (!inString && c == ')') {
				depth--;
			}
			sb.append(c);
		}

		items.add(sb.toString().trim());

		return items.toArray(new String[0]);
	}

	private Object convertValue(String value) {
		Matcher matcher;

		if ("null".equals(value.toLowerCase())) {
			return null;
		}

		matcher = stringPattern.matcher(value);
		if (matcher.matches()) {
			return matcher.group(1);
		}

		matcher = booleanPattern.matcher(value);
		if (matcher.matches()) {
			return Boolean.parseBoolean(value);
		}

		matcher = floatPattern.matcher(value);
		if (matcher.matches()) {
			return Double.parseDouble(value);
		}

		return Integer.parseInt(value);
	}

}
