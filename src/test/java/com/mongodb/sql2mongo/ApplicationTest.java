package com.mongodb.sql2mongo;

import org.junit.Test;

import com.mongodb.sql2mongo.Application;

public class ApplicationTest {

	@Test
	public void applicationTest() {
		Application.main("-i src/test/resources/test.sql -d test".split(" "));
	}
}
