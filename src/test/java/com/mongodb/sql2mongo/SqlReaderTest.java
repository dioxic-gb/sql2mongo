package com.mongodb.sql2mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.sql2mongo.Record;
import com.mongodb.sql2mongo.SqlReader;

public class SqlReaderTest {

	@Test
	public void parse() {
		SqlReader reader = new SqlReader();
		Record rec;
		Map<String, Object> expected = new HashMap<>();

		rec = reader.parse("InSErT INTO db.table (col1,col2,  col3, col4,col5,col6,col7) VALUES (1, \"val,'somestuff'\", '\"val', true, FALSE, 5.678, null)");
		expected.put("col1", 1);
		expected.put("col2", "val,'somestuff'");
		expected.put("col3", "\"val");
		expected.put("col4", true);
		expected.put("col5", false);
		expected.put("col6", 5.678);

		assertThat(rec.getDatabase()).as("database").isEqualTo("db");
		assertThat(rec.getCollection()).as("collection").isEqualTo("table");
		assertThat(rec.getData()).as("values").isEqualTo(expected);
	}

	@Test
	public void parse2() {
		SqlReader reader = new SqlReader();
		Record rec;
		Map<String, Object> expected = new HashMap<>();

		rec = reader.parse("InSErT INTO table (col1,col2,  col3, col4,col5,col6,col7) VALUES (1, \"val,'somestuff'\", '\"val', true, FALSE, 5.678, null)");
		expected.put("col1", 1);
		expected.put("col2", "val,'somestuff'");
		expected.put("col3", "\"val");
		expected.put("col4", true);
		expected.put("col5", false);
		expected.put("col6", 5.678);

		assertThat(rec.getCollection()).as("collection").isEqualTo("table");
		assertThat(rec.getData()).as("values").isEqualTo(expected);
	}

	@Test
	@Ignore
	public void parseDate() {
		SqlReader reader = new SqlReader();
		Record rec;
		Map<String, Object> expected = new HashMap<>();

		rec = reader.parse("InSErT INTO db.table (col1,col2,col3) VALUES ("
				+ "to_date('02-Dec-14T22.25.41','DD-MON-RR\"T\"HH24.MI.SS'),"
				+ "to_date('02-December-2014 09.25.41','DD-MONTH-RRRR HH24.MI.SS'),"
				+ "to_date('02-Dec-14 22.25.41','DD-MON-RR HH24.MI.SS')"
				+ ")");
		expected.put("col1", DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse("2014-12-02T22:25:41"));
		expected.put("col2", DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse("2014-12-02T09:25:41"));
		expected.put("col3", DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse("2014-12-02T22:25:41"));

		assertThat(rec.getData()).isEqualTo(expected);
	}

}
