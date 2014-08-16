package org.apodhrad.downloader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DownloaderTest {

	private static final int JETTY_SERVER_PORT = 8180;
	private static final String JETTY_RESOURCE_BASE = System.getProperty("jetty.resourceBase");
	private static final String TEST_RESOURCE = "gradle-wrapper.jar";
	private static final String RESOURCE_DIR = DownloaderTest.class.getResource("/").getPath();
	private static final String CACHE_DIR = new java.io.File(RESOURCE_DIR, "cache").getAbsolutePath();
	private static final String TARGET_DIR = new java.io.File(RESOURCE_DIR, "target").getAbsolutePath();

	private static Server server;

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
		testResource.setUrl("http://localhost:8180/" + TEST_RESOURCE);
		testResource.setCache(CACHE_DIR);
		testResource.setTarget(TARGET_DIR);
		testResource.download();

		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).isFile());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
	}

	@Test
	public void downloadUnpackTest() throws Exception {
		Downloader testResource = new Downloader();
		testResource.setUrl("http://localhost:8180/" + TEST_RESOURCE);
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
	public void downloadWithoutCacheTest() throws Exception {
		Downloader testResource = new Downloader();
		testResource.setUrl("http://localhost:8180/" + TEST_RESOURCE);
		testResource.setTarget(TARGET_DIR);
		testResource.download();

		assertFalse(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
	}

	@Test
	public void downloadWithoutCacheUnpackTest() throws Exception {
		Downloader testResource = new Downloader();
		testResource.setUrl("http://localhost:8180/" + TEST_RESOURCE);
		testResource.setTarget(TARGET_DIR);
		testResource.setUnpack(true);
		testResource.download();

		assertFalse(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
		assertTrue(new File(TARGET_DIR, "META-INF").exists());
		assertTrue(new File(TARGET_DIR, "META-INF").isDirectory());
	}
}
