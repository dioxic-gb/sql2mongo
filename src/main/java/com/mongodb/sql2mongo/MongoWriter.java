package com.mongodb.sql2mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoWriter implements Writer {
	private static final int BATCH_SIZE = 1000;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private MongoClient client;
	private SqlReader sqlReader = new SqlReader();
	private List<Record> recordBuffer = new ArrayList<>(BATCH_SIZE);
	private MongoDatabase db;
	private MongoCollection<Document> collection;
	private String currentCollectionName;
	private String currentDatabaseName;
	private int count;
	private Config cfg;

	public MongoWriter(Config cfg) {
		this.cfg = cfg;
	}

	@Override
	public void initialise() {
		logger.info("initialising mongo writer");
		ServerAddress serverAddr = new ServerAddress(cfg.getHost(), cfg.getPort());

		if (cfg.isAuth()) {
			MongoCredential credential = MongoCredential.createCredential(cfg.getUser(), "admin", cfg.getPassword().toCharArray());
			client = new MongoClient(serverAddr, Arrays.asList(credential));
		}
		else {
			client = new MongoClient(serverAddr);
		}
		currentDatabaseName = cfg.getDatabase();
		db = client.getDatabase(cfg.getDatabase());
	}

	@Override
	public void write(String sql) {
		if (client == null) {
			throw new IllegalStateException("mongo writer not initialised!");
		}

		Record rec = sqlReader.parse(sql);

		if (rec != null) {
			// if the current database switches flush the data and create new
			// mongo database
			if (rec.getDatabase() != null && !rec.getDatabase().equals(currentDatabaseName)) {
				logger.debug("setting database to {}", rec.getDatabase());
				flush();
				currentDatabaseName = rec.getDatabase();
				db = client.getDatabase(rec.getDatabase());
			}

			// if the current collection switches flush the data and create new
			// mongo collection
			if (!rec.getCollection().equals(currentCollectionName)) {
				logger.debug("setting collection to {}", rec.getCollection());
				flush();
				currentCollectionName = rec.getCollection();
				collection = db.getCollection(rec.getCollection());
			}

			recordBuffer.add(rec);

			if (recordBuffer.size() >= BATCH_SIZE) {
				flush();
			}

			count++;
			if (count % 1000 == 0) {
				logger.info("{} documents written", count);
			}
		}
	}

	@Override
	public void flush() {
		if (!recordBuffer.isEmpty()) {
			logger.debug("flushing data");
			collection.insertMany(recordBuffer.stream().map(r -> new Document(r.getData())).collect(Collectors.toList()));
			recordBuffer.clear();
		}
	}

	@Override
	public void close() {
		client.close();
	}

	@Override
	public int getCount() {
		return count;
	}

}
