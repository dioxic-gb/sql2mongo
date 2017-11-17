package com.mongodb.sql2mongo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonFileWriter implements Writer {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private SqlReader sqlReader = new SqlReader();
	private BufferedWriter writer;
	private Path path;
	private int count = 0;

	public JsonFileWriter(Config cfg) {
		String outputFile = cfg.getInputFile() + ".json";
		path = Paths.get(outputFile);
	}

	@Override
	public void write(String sql) {
		if (writer == null) {
			throw new IllegalStateException("json writer not initialised!");
		}

		Record rec = sqlReader.parse(sql);

		if (rec != null) {
			Document doc = new Document(rec.getData());
			try {
				writer.write(doc.toJson());
				writer.newLine();
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
			count++;

			if (count % 1000 == 0) {
				logger.info("{} documents written", count);
			}
		}
	}

	@Override
	public void close() {
		if (writer != null) {
			try {
				writer.close();
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public void flush() {
		if (writer != null) {
			try {
				writer.flush();
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void initialise() {
		logger.info("initialising json file writer");
		try {
			writer = Files.newBufferedWriter(path);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
