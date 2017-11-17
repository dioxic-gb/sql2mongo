package com.mongodb.sql2mongo;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.LogManager;
import java.util.stream.Stream;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Application implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	private static Config cfg;

	public static void main(String[] args) {
		LogManager.getLogManager().reset();

		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		try {
			CommandLineOptions clo = new CommandLineOptions(args);

			cfg = clo.getConfig();

			new Application().run();
		}
		catch (ParseException e) {
			logger.error("Failed to parse command line options");
			logger.error(e.getMessage());
			System.exit(1);
		}
		catch (RuntimeException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
	}

	@Override
	public void run() {
		Writer writer = cfg.isMongo() ? new MongoWriter(cfg) : new JsonFileWriter(cfg);
		writer.initialise();

		logger.info("Config: {}", cfg.toString());

		logger.info("Reading file {}", cfg.getInputFile());
		try (Stream<String> stream = Files.lines(Paths.get(cfg.getInputFile()))) {
			stream.forEach(writer::write);
		}
		catch (Exception e) {
			logger.error("Failed to complete", e);
			throw new RuntimeException(e);
		}
		finally {
			writer.flush();
			writer.close();
		}
		logger.info("Work complete, {} records written", writer.getCount());
	}
}
