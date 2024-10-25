package com.opentext.ots.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class FileUtilTest {
	private static final String EXAMPLE_FILE_NAME = "pdf-examples/file-sample_150kB.pdf";

	@Test
	public void getFilesInFolderByExtension() {
		File f = FileUtil.getFileFromResources(EXAMPLE_FILE_NAME);
		Set<String> files = FileUtil.getFilesInFolderByExtension(f.getParentFile().getAbsolutePath(), ".pdf");

		assertNotNull(files);
		assertEquals(1, files.size());
	}
	
	@Test
	public void getFileFromResources() {
		File f = FileUtil.getFileFromResources(EXAMPLE_FILE_NAME);
		assertNotNull(f);
		assertTrue(f.exists());
	}	
}
