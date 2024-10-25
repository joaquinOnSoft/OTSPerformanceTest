package com.opentext.ots.performance;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.opentext.ots.util.FileUtil;
import com.opentext.ots.util.OSUtil;

public class OTSPerformanceMeter {


	private static final int DEFAULT_NUM_COPIES = 1;

	private static final int DEFAULT_POOLING_TIME_MS = 2000;

	private static final String HELP = """
			This command line tool is designed to measure the time that OTS spends to process the files
			copied in and `input folder` and write the result of the process in the `output folder`.

			Invocation example:

			java -jar OTSPerformanceTest-24.10.25.jar /
				--file C:\\performance\\example.pdf /
				--input C:\\performance\\input /
				--output C:\\performance\\output /
				--pooling 5000
				--copies 10000
			""";

	private static final String LONG_PARAM_INPUT = "input";
	private static final String LONG_PARAM_OUTPUT = "output";
	private static final String LONG_PARAM_COPIES = "copies";
	private static final String LONG_PARAM_FILE = "file";
	private static final String LONG_PARAM_POOLING_TIME = "pooling";

	private static final String SHORT_PARAM_INPUT = "i";	
	private static final String SHORT_PARAM_OUTPUT = "o";
	private static final String SHORT_PARAM_COPIES = "c";
	private static final String SHORT_PARAM_FILE = "f";
	private static final String SHORT_PARAM_POOLING_TIME = "p";


	public static void main(String[] args) {
		Options options = new Options();

		options.addRequiredOption(SHORT_PARAM_FILE, LONG_PARAM_FILE, true, "Path to input file (required)");
		options.addOption(SHORT_PARAM_COPIES, LONG_PARAM_COPIES, true, "Number of copies (required)");
		options.addRequiredOption(SHORT_PARAM_INPUT, LONG_PARAM_INPUT, true, "Input directory (required)");
		options.addRequiredOption(SHORT_PARAM_OUTPUT, LONG_PARAM_OUTPUT, true, "Output directory (required)");
		options.addOption(SHORT_PARAM_POOLING_TIME, LONG_PARAM_POOLING_TIME, true, "Pooling time (in miliseconds)");

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);

			String filePath = getOptionValueFilePath(cmd, SHORT_PARAM_FILE, "The path for -f must be a file.");
			int copies = getOptionValueInt(cmd, SHORT_PARAM_COPIES, DEFAULT_NUM_COPIES, "The value for -c must be a positive integer greater than zero.");
			String inputDir = getOptionValueDirPath(cmd, SHORT_PARAM_INPUT, "The path for -i must be a directory.");
			String outputDir = getOptionValueDirPath(cmd, SHORT_PARAM_OUTPUT, "The path for -o must be a directory.");
			int poolingTimeMs = getOptionValueInt(cmd, SHORT_PARAM_POOLING_TIME, DEFAULT_POOLING_TIME_MS, "The value for -p must be a positive integer greater than zero.");

			ExecutorService executor = Executors.newFixedThreadPool(2);
			executor.submit(() -> {
				try {
					FileUtil.cloneFile(filePath, inputDir, copies);
				} catch (IOException e) {
					System.err.print(e.getMessage());
				}
			} );
			executor.submit(() -> waitUntilNumFilesReached(outputDir, copies, poolingTimeMs));
			executor.shutdown();

		} catch (Exception e) {
			formatter.printHelp(HELP, options);
			System.err.print(e.getMessage() + "\n\n");
		}
	}

	private static String getOptionValueFilePath(CommandLine cmd, String param, String errorMsg) throws IllegalArgumentException {
		File file = new File(cmd.getOptionValue(param));
		if (!file.isFile()) {
			throw new IllegalArgumentException(errorMsg);
		}

		return file.getAbsolutePath();
	}

	private static String getOptionValueDirPath(CommandLine cmd, String param, String errorMsg) throws IllegalArgumentException {
		File inputDir = new File(cmd.getOptionValue(param));
		if (!inputDir.isDirectory()) {
			throw new IllegalArgumentException(errorMsg);
		}	

		return inputDir.getAbsolutePath();
	}

	private static int getOptionValueInt(CommandLine cmd, String param, int defaultValue, String errorMsg) throws IllegalArgumentException{
		int copies = defaultValue;

		if(cmd.hasOption(param)) {
			try {
				copies = Integer.parseInt(cmd.getOptionValue(param));
				if (copies <= 0) {
					throw new IllegalArgumentException(errorMsg);
				}
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(errorMsg);
			}
		}		

		return copies;
	}

	public static void waitUntilNumFilesReached(String targetDir, int targetCount, int poolingTimeMs) {
		Date init = GregorianCalendar.getInstance().getTime();
		Date now =null;
		long diff = 0;
		boolean isClosed = true;

		System.out.println("File count \t time (ms)");

		AtomicInteger fileCount = new AtomicInteger(0);

		do {
			try {
				Thread.sleep(poolingTimeMs);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}

			fileCount.set( FileUtil.countFilesByType(targetDir, ".pdf") );
			
			if(OSUtil.isLinux()) {
				isClosed = true;
				
				Set<String> pdfFiles = FileUtil.getFilesInFolderByExtension(targetDir, ".pdf");
				for(String pdfFile: pdfFiles) {
					isClosed &= FileUtil.isFileClosed(new File(pdfFile));
				}
			}

			now = GregorianCalendar.getInstance().getTime();
			diff = now.getTime() - init.getTime();

			System.out.println(fileCount + "\t" + diff);				
		}
		while (fileCount.get() < targetCount && !isClosed);

		System.out.println("\nTarget file count reached: " + fileCount);
	}	
}
