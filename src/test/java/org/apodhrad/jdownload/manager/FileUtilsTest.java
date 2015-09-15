package org.apodhrad.jdownload.manager;

import static org.apodhrad.jdownload.manager.FileUtils.generateMD5;
import static org.apodhrad.jdownload.manager.FileUtils.matchMD5;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileUtilsTest {

	private static final String JETTY_RESOURCE_BASE = System.getProperty("jetty.resourceBase");
	private static final String TEST_RESOURCE = "gradle-wrapper.jar";
	private static final String RESOURCE_DIR = FileUtilsTest.class.getResource("/").getPath();
	private static final File TEST_DIR = new File(RESOURCE_DIR, "test");

	@BeforeClass
	public static void checkProperties() {
		assertNotNull("System property jetty.resourceBase is not set", JETTY_RESOURCE_BASE);
	}

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

	@Test
	public void generateMD5Test() throws Exception {
		String md5 = generateMD5(new File(JETTY_RESOURCE_BASE, TEST_RESOURCE));
		assertEquals("M5 sum is not generated correctly", "e4cd368a8e06aa0d3f9f8c7b078df0a1", md5);
	}

	@Test
	public void matchCorrectMD5Test() throws Exception {
		assertTrue(matchMD5(new File(JETTY_RESOURCE_BASE, TEST_RESOURCE), "e4cd368a8e06aa0d3f9f8c7b078df0a1"));
	}

	@Test
	public void matchIncorrectMD5Test() throws Exception {
		assertFalse(matchMD5(new File(JETTY_RESOURCE_BASE, TEST_RESOURCE), "e4cd368a8e06aa0d3f9f8c7b078df0a2"));
	}

	@Test
	public void matchNullMD5Test() throws Exception {
		assertTrue(matchMD5(new File(JETTY_RESOURCE_BASE, TEST_RESOURCE), null));
	}

	@Test(expected = FileNotFoundException.class)
	public void generateMD5OfNonexistingFileTest() throws Exception {
		FileUtils.generateMD5(new File(JETTY_RESOURCE_BASE, "test.jar"));
	}
}
