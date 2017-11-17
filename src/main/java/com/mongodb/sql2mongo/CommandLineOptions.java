package com.mongodb.sql2mongo;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineOptions {
	private final Logger logger;
	private final Config config = new Config();

	public CommandLineOptions(String[] args) throws ParseException {
		logger = LoggerFactory.getLogger(CommandLineOptions.class);
		logger.info("Parsing Command Line");

		CommandLineParser parser = new DefaultParser();

		Options cliopt = new Options();

		cliopt.addOption("help", "help", false, "Show Help");
		cliopt.addOption("h", "host", true, "MongoDB host");
		cliopt.addOption("d", "database", true, "database");
		cliopt.addOption("p", "port", true, "MongoDB port");
		cliopt.addOption("u", "user", true, "MongoDB username");
		cliopt.addOption("pwd", "password", true, "MongoDB password");
		cliopt.addOption("i", "input file", true, "SQL input file");

		CommandLine cmd = parser.parse(cliopt, args);

		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Sql2Mql", cliopt);
			System.exit(0);
		}

		if (cmd.hasOption("p")) {
			config.setPort(Integer.parseInt(cmd.getOptionValue("p")));
		}
		if (cmd.hasOption("h")) {
			config.setHost(cmd.getOptionValue("h"));
		}
		if (cmd.hasOption("d")) {
			config.setDatabase(cmd.getOptionValue("d"));
		}
		if (cmd.hasOption("u")) {
			config.setUser(cmd.getOptionValue("u"));
			if (!cmd.hasOption("pwd")) {
				throw new IllegalStateException("username option set - you need to provide a password as well!");
			}
		}
		if (cmd.hasOption("pwd")) {
			config.setPassword(cmd.getOptionValue("pwd"));
			if (!cmd.hasOption("u")) {
				throw new IllegalStateException("password option set - you need to provide a username as well!");
			}
		}
		if (cmd.hasOption("i")) {
			config.setInputFile(cmd.getOptionValue("i"));
		}
	}

	public Config getConfig() {
		return config;
	}

}
