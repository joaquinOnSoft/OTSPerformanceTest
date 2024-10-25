package com.opentext.ots.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {

	/**
	 * Get file from classpath, resources folder
	 * SEE: Java Read a file from resources folder
	 * https://www.mkyong.com/java/java-read-a-file-from-resources-folder/
	 * @param fileName
	 * @return
	 */
	public static File getFileFromResources(String fileName) {
        URL resource = FileUtil.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }		
	
	public static InputStream getStreamFromResources(String fileName) {
		InputStream resource = FileUtil.class.getClassLoader().getResourceAsStream(fileName);
		if (resource == null) {
			throw new IllegalArgumentException("file is not found!");
		} else {
			return resource;
		}
	}

	public static Set<String> getFilesInFolderByExtension(String dir, String extension) {
		return Stream.of(new File(dir)
				.listFiles())
				.filter(file -> file.getAbsolutePath().endsWith(extension))
				.map(File::getName).collect(Collectors.toSet());
	}

	public static void cloneFile(String sourceFilePath, String destinationDirPath, int numCopies) throws IOException {
		int nDigits = String.valueOf(numCopies).length();

		Path sourcePath = Paths.get(sourceFilePath);
		for (int i = 1; i <= numCopies; i++) {
			Path destinationPath = Paths.get(destinationDirPath, sourcePath.getFileName().toString().replace(".",
					"_" + String.format("%0" + nDigits + "d", i) + "."));
			Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	/**
	 * @param targetDir
	 * @return
	 */
	public static int countFilesByType(String targetDir, String extension) {
		int numFiles = -1;

		File dir = new File(targetDir);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(extension);
			}
		});

		if (files != null) {
			numFiles = files.length;
		}

		return numFiles;
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @see <a href=
	 *      "https://stackoverflow.com/questions/9341505/how-to-check-if-a-file-is-open-by-another-process-java-linux">
	 *      How to check if a file is open by another process (Java/Linux)? </a>
	 */
	public static boolean isFileClosed(File file) {
		boolean isClosed = true;
		Process plsof = null;
		BufferedReader reader = null;

		try {
			plsof = new ProcessBuilder(new String[] { "lsof", "|", "grep", file.getAbsolutePath() }).start();
			reader = new BufferedReader(new InputStreamReader(plsof.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(file.getAbsolutePath())) {
					reader.close();
					plsof.destroy();
					isClosed = false;
				}
			}
		} catch (IOException ex) {
			isClosed = true;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					isClosed = true;
				}
			}

			if (plsof != null) {
				plsof.destroy();
			}
		}

		return isClosed;
	}
}
