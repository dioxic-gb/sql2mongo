package com.mongodb.sql2mongo;

import java.util.HashMap;
import java.util.Map;

public class Record {

	private String database;
	private String collection;
	private Map<String, Object> data;

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public void addField(String field, Object value) {
		if (data == null) {
			data = new HashMap<>();
		}

		if (value != null) {
			data.put(field, value);
		}
	}

}
