package org.apodhrad.jdownload.manager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileUtilsTest {

	private static final String JETTY_RESOURCE_BASE = System.getProperty("jetty.resourceBase");
	private static final String TEST_RESOURCE = "gradle-wrapper.jar";
	private static final String RESOURCE_DIR = FileUtilsTest.class.getResource("/").getPath();
	private static final File TEST_DIR = new File(RESOURCE_DIR, "test");

	@Before
	@After
	public void deleteTestDir() throws IOException {
		FileUtils.deleteDirectory(TEST_DIR);
	}

	@Test
	public void unpackJarTest() throws Exception {
		FileUtils.unpack(new File(JETTY_RESOURCE_BASE, TEST_RESOURCE), TEST_DIR);

		assertTrue(new File(TEST_DIR, "META-INF").exists());
		assertTrue(new File(TEST_DIR, "META-INF").isDirectory());
	}

	@Test
	public void copyTest() throws Exception {
		FileUtils.copyFileToDirectory(new File(JETTY_RESOURCE_BASE, TEST_RESOURCE), TEST_DIR);

		assertTrue(new File(TEST_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TEST_DIR, TEST_RESOURCE).isFile());
	}

	@Test
	public void copyWithSameNameTest() throws Exception {
		FileUtils.copyFile(new File(JETTY_RESOURCE_BASE, TEST_RESOURCE), new File(TEST_DIR, TEST_RESOURCE));

		assertTrue(new File(TEST_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TEST_DIR, TEST_RESOURCE).isFile());
	}

	@Test
	public void copyWithDifferentNameTest() throws Exception {
		FileUtils.copyFile(new File(JETTY_RESOURCE_BASE, TEST_RESOURCE), new File(TEST_DIR, "test.jar"));

		assertFalse(new File(TEST_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TEST_DIR, "test.jar").exists());
		assertTrue(new File(TEST_DIR, "test.jar").isFile());
	}

	@Test
	public void createDirTest() {
		FileUtils.createDir(TEST_DIR);

		assertTrue(TEST_DIR.exists());
		assertTrue(TEST_DIR.isDirectory());
	}

	@Test
	public void createExistingDirTest() {
		FileUtils.createDir(TEST_DIR);
		FileUtils.createDir(TEST_DIR);

		assertTrue(TEST_DIR.exists());
		assertTrue(TEST_DIR.isDirectory());
	}
}
