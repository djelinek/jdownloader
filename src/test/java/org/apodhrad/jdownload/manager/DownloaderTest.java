package org.apodhrad.jdownload.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apodhrad.jdownload.manager.DownloadUtils;
import org.apodhrad.jdownload.manager.Downloader;
import org.apodhrad.jdownload.manager.FileUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

public class DownloaderTest {

	private static final String TEST_RESOURCE = "gradle-wrapper.jar";
	private static final int JETTY_SERVER_PORT = 8180;
	private static final String JETTY_SERVER_URL = "http://localhost:" + JETTY_SERVER_PORT;
	private static final String JETTY_TEST_RESOURCE_URL = JETTY_SERVER_URL + "/" + TEST_RESOURCE;
	private static final String JETTY_RESOURCE_BASE = System.getProperty("jetty.resourceBase");
	private static final String RESOURCE_DIR = DownloaderTest.class.getResource("/").getPath();
	private static final String CACHE_DIR = new java.io.File(RESOURCE_DIR, "cache").getAbsolutePath();
	private static final String TARGET_DIR = new java.io.File(RESOURCE_DIR, "target").getAbsolutePath();

	private static Server server;

	@Rule
	public final StandardOutputStreamLog out = new StandardOutputStreamLog();

	@BeforeClass
	public static void startServer() throws Exception {
		assertNotNull("Systemproperty jetty.resourceBase is not set", JETTY_RESOURCE_BASE);
		if (!new java.io.File(JETTY_RESOURCE_BASE).exists()) {
			fail("'" + JETTY_RESOURCE_BASE + "' doesn't exist");
		}
		if (!new java.io.File(JETTY_RESOURCE_BASE, TEST_RESOURCE).exists()) {
			fail("'" + TEST_RESOURCE + "' doesn't exist");
		}
		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "index.html" });
		resource_handler.setResourceBase(JETTY_RESOURCE_BASE);
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
		server = new Server(JETTY_SERVER_PORT);
		server.setHandler(handlers);
		server.start();
	}

	@AfterClass
	public static void stopServer() throws Exception {
		server.stop();
	}

	@Before
	@After
	public void deleteCacheAndTarget() throws IOException {
		FileUtils.deleteDirectory(CACHE_DIR);
		FileUtils.deleteDirectory(TARGET_DIR);
	}

	@Test
	public void downloadTest() throws Exception {
		Downloader testResource = new Downloader();
		testResource.setUrl(JETTY_TEST_RESOURCE_URL);
		testResource.setCache(CACHE_DIR);
		testResource.setTarget(TARGET_DIR);
		testResource.download();

		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).isFile());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());

		String expectedOutput = "Creating dir '" + CACHE_DIR + "'\n" + "Downloading '" + JETTY_TEST_RESOURCE_URL
				+ "' to '" + new File(CACHE_DIR, TEST_RESOURCE).getAbsolutePath() + "'\n"
				+ "Downloaded      51348 /      51348 (100%)\n" + "Copying file '"
				+ new File(CACHE_DIR, TEST_RESOURCE).getAbsolutePath() + "' to '"
				+ new File(TARGET_DIR, TEST_RESOURCE).getAbsolutePath() + "'\n";
		assertEquals(expectedOutput, out.getLog());
	}

	@Test
	public void downloadUnpackTest() throws Exception {
		Downloader testResource = new Downloader();
		testResource.setUrl(JETTY_TEST_RESOURCE_URL);
		testResource.setCache(CACHE_DIR);
		testResource.setTarget(TARGET_DIR);
		testResource.setUnpack(true);
		testResource.download();

		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).isFile());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
		assertTrue(new File(TARGET_DIR, "META-INF").exists());
		assertTrue(new File(TARGET_DIR, "META-INF").isDirectory());
	}

	@Test
	public void downloadTargetExistingResourceTest() throws Exception {
		DownloadUtils.download(JETTY_TEST_RESOURCE_URL, TARGET_DIR);

		Downloader testResource = new Downloader();
		testResource.setUrl(JETTY_TEST_RESOURCE_URL);
		testResource.setCache(CACHE_DIR);
		testResource.setTarget(TARGET_DIR);
		testResource.download();

		assertFalse(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
	}

	@Test
	public void downloadCacheExistingResourceTest() throws Exception {
		DownloadUtils.download(JETTY_TEST_RESOURCE_URL, CACHE_DIR);

		Downloader testResource = new Downloader();
		testResource.setUrl(JETTY_TEST_RESOURCE_URL);
		testResource.setCache(CACHE_DIR);
		testResource.setTarget(TARGET_DIR);
		testResource.download();

		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).isFile());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
	}

	@Test
	public void downloadWithoutCacheTest() throws Exception {
		Downloader testResource = new Downloader();
		testResource.setUrl(JETTY_TEST_RESOURCE_URL);
		testResource.setTarget(TARGET_DIR);
		testResource.download();

		assertFalse(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
	}

	@Test
	public void downloadWithoutCacheUnpackTest() throws Exception {
		Downloader testResource = new Downloader();
		testResource.setUrl(JETTY_TEST_RESOURCE_URL);
		testResource.setTarget(TARGET_DIR);
		testResource.setUnpack(true);
		testResource.download();

		assertFalse(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
		assertTrue(new File(TARGET_DIR, "META-INF").exists());
		assertTrue(new File(TARGET_DIR, "META-INF").isDirectory());
	}

	@Test
	public void getNameTest() {
		assertEquals(TEST_RESOURCE, Downloader.getName(JETTY_TEST_RESOURCE_URL));
	}

	@Test
	public void getTargetNameIfSetTest() {
		Downloader testResource = new Downloader();
		testResource.setUrl(JETTY_TEST_RESOURCE_URL);
		testResource.setTargetName("test.jar");

		assertEquals("test.jar", testResource.getTargetName());
	}

	@Test
	public void getTargetNameIfNotSetTest() {
		Downloader testResource = new Downloader();
		testResource.setUrl(JETTY_TEST_RESOURCE_URL);

		assertEquals(TEST_RESOURCE, testResource.getTargetName());
	}

}
