package com.opentext.ots.performance;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class FileCopyApp {

	private static final String LONG_PARAM_OUTPUT = "output";
	private static final String LONG_PARAM_COPIES = "copies";
	private static final String LONG_PARAM_FILE = "file";

	private static final String SHORT_PARAM_OUTPUT = "o";
	private static final String SHORT_PARAM_COPIES = "c";
	private static final String SHORT_PARAM_FILE = "f";

	public static void main(String[] args) {
		Options options = new Options();

		options.addOption(SHORT_PARAM_FILE, LONG_PARAM_FILE, true, "Path to input file (required)");
		options.addOption(SHORT_PARAM_COPIES, LONG_PARAM_COPIES, true, "Number of copies (required)");
		options.addOption(SHORT_PARAM_OUTPUT, LONG_PARAM_OUTPUT, true, "Output directory (required)");

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
			validateParams(cmd);
			String filePath = cmd.getOptionValue(SHORT_PARAM_FILE);
			int copies = Integer.parseInt(cmd.getOptionValue(SHORT_PARAM_COPIES));
			String outputDir = cmd.getOptionValue(SHORT_PARAM_OUTPUT);

			ExecutorService executor = Executors.newFixedThreadPool(2);
			executor.submit(() -> {
				try {
					copyFiles(filePath, outputDir, copies);
				} catch (IOException e) {
					System.err.print(e.getMessage());
				}
			} );
			executor.submit(() -> countFiles(outputDir, copies));
			executor.shutdown();

		} catch (Exception e) {
			formatter.printHelp("OTS performance test", options);
			System.err.print(e.getMessage());
		}
	}

	private static void validateParams(CommandLine cmd) throws IllegalArgumentException {
		if (!cmd.hasOption(SHORT_PARAM_FILE) || !cmd.hasOption(SHORT_PARAM_COPIES) || !cmd.hasOption(SHORT_PARAM_OUTPUT)) {
			throw new IllegalArgumentException("All parameters are required.");
		}

		File file = new File(cmd.getOptionValue(SHORT_PARAM_FILE));
		if (!file.isFile()) {
			throw new IllegalArgumentException("The path for -f must be a file.");
		}

		File outputDir = new File(cmd.getOptionValue(SHORT_PARAM_OUTPUT));
		if (!outputDir.isDirectory()) {
			throw new IllegalArgumentException("The path for -o must be a directory.");
		}

		int copies;
		try {
			copies = Integer.parseInt(cmd.getOptionValue(SHORT_PARAM_COPIES));
			if (copies <= 0) {
				throw new IllegalArgumentException("The value for -c must be a positive integer greater than zero.");
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("The value for -c must be a positive integer.");
		}
	}

	private static void copyFiles(String filePath, String inputDir, int copies) throws IOException {
		int nDigits = String.valueOf(copies).length();

		Path sourcePath = Paths.get(filePath);
		for (int i = 1; i <= copies; i++) {
			Path destinationPath = Paths.get(inputDir, sourcePath.getFileName().toString().replace(".", "_" + String.format("%0"+nDigits+"d", i) + "."));
			Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private static void countFiles(String outputDir, int targetCount) {
		Date init = GregorianCalendar.getInstance().getTime();
		Date now =null;
		long diff = 0;

		AtomicInteger fileCount = new AtomicInteger(0);
		while (fileCount.get() < targetCount) {
			File dir = new File(outputDir);
			File[] files = dir.listFiles();
			if (files != null) {
				now = GregorianCalendar.getInstance().getTime();
				diff = now.getTime() - init.getTime();

				fileCount.set(files.length);
				System.out.println("File count: " + fileCount.get() + " in " + diff + " ms");
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		System.out.println("Target file count reached: " + fileCount.get());
	}
}
