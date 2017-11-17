package com.mongodb.sql2mongo;

import com.google.gson.Gson;

public class Config {

	private String host;
	private int port = 27017;
	private String user;
	private String password;
	private String inputFile;
	private String database;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public boolean isAuth() {
		return user != null && password != null;
	}

	public boolean isMongo() {
		return host != null;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
