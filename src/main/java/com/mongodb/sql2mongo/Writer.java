package com.mongodb.sql2mongo;

public interface Writer {

	public void initialise();

	public void write(String sql);

	public void close();

	public int getCount();

	public void flush();
}
