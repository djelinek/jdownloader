package org.apodhrad.jdownload.manager;

import static org.junit.Assert.assertEquals;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

public class JDownloadManagerTest {

	private static final String TEST_RESOURCE = "gradle-wrapper.jar";
	private static final int JETTY_SERVER_PORT = 8180;
	private static final String JETTY_SERVER_URL = "http://localhost:" + JETTY_SERVER_PORT;
	private static final String JETTY_TEST_RESOURCE_URL = JETTY_SERVER_URL + "/" + TEST_RESOURCE;
	private static final String JETTY_RESOURCE_BASE = System.getProperty("jetty.resourceBase");
	private static final String RESOURCE_DIR = JDownloadManagerTest.class.getResource("/").getPath();
	private static final File CACHE_DIR = new java.io.File(RESOURCE_DIR, "cache");
	private static final File TARGET_DIR = new java.io.File(RESOURCE_DIR, "target");

	private static Server server;

	@Rule
	public final StandardOutputStreamLog out = new StandardOutputStreamLog();

	@BeforeClass
	public static void startServer() throws Exception {
		assertNotNull("System property jetty.resourceBase is not set", JETTY_RESOURCE_BASE);
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
		JDownloadManager manager = new JDownloadManager(CACHE_DIR);
		manager.download(JETTY_TEST_RESOURCE_URL, TARGET_DIR);

		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).isFile());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());

		String expectedOutput = "Downloading '" + JETTY_TEST_RESOURCE_URL + "' to '"
				+ new File(CACHE_DIR, TEST_RESOURCE).getAbsolutePath() + "'\n"
				+ "Downloaded      51348 /      51348 (100%)\n";
		assertEquals(expectedOutput, out.getLog());
	}

	@Test
	public void downloadUnpackTest() throws Exception {
		JDownloadManager manager = new JDownloadManager(CACHE_DIR);
		manager.download(JETTY_TEST_RESOURCE_URL, TARGET_DIR, true);

		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).isFile());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
		assertTrue(new File(TARGET_DIR, "META-INF").exists());
		assertTrue(new File(TARGET_DIR, "META-INF").isDirectory());
	}

	@Test
	public void downloadTargetExistingResourceTest() throws Exception {
		DownloadUtils.download(JETTY_TEST_RESOURCE_URL, new File(TARGET_DIR, TEST_RESOURCE));

		JDownloadManager manager = new JDownloadManager(CACHE_DIR);
		manager.download(JETTY_TEST_RESOURCE_URL, TARGET_DIR);

		assertFalse(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
	}

	@Test
	public void downloadTargetExistingResourceWithCorrectMD5Test() throws Exception {
		DownloadUtils.download(JETTY_TEST_RESOURCE_URL, new File(TARGET_DIR, TEST_RESOURCE));

		JDownloadManager manager = new JDownloadManager(CACHE_DIR);
		manager.download(JETTY_TEST_RESOURCE_URL, TARGET_DIR, TEST_RESOURCE, "e4cd368a8e06aa0d3f9f8c7b078df0a1");

		assertFalse(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
	}

	@Test
	public void downloadTargetExistingResourceWithIncorrectMD5Test() throws Exception {
		DownloadUtils.download(JETTY_TEST_RESOURCE_URL, new File(TARGET_DIR, TEST_RESOURCE));

		JDownloadManager manager = new JDownloadManager(CACHE_DIR);
		try {
			manager.download(JETTY_TEST_RESOURCE_URL, TARGET_DIR, TEST_RESOURCE, "e4cd368a8e06aa0d3f9f8c7b078df0a2");
		} catch (RuntimeException re) {
			// ok, this is expected
			assertTrue(new File(CACHE_DIR, TEST_RESOURCE).exists());
			assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
			assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
			return;
		}

		fail("Manager should throw an exception about incorrect MD5!");
	}

	@Test
	public void downloadCacheExistingResourceTest() throws Exception {
		DownloadUtils.download(JETTY_TEST_RESOURCE_URL, new File(CACHE_DIR, TEST_RESOURCE));

		JDownloadManager manager = new JDownloadManager(CACHE_DIR);
		manager.download(JETTY_TEST_RESOURCE_URL, TARGET_DIR);

		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).isFile());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
	}

	@Test
	public void downloadCacheExistingResourceWithCorrectMD5Test() throws Exception {
		DownloadUtils.download(JETTY_TEST_RESOURCE_URL, new File(CACHE_DIR, TEST_RESOURCE));
		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).isFile());

		long lastModified = new File(CACHE_DIR, TEST_RESOURCE).lastModified();
		Thread.sleep(1000);

		JDownloadManager manager = new JDownloadManager(CACHE_DIR);
		manager.download(JETTY_TEST_RESOURCE_URL, TARGET_DIR, TEST_RESOURCE, "e4cd368a8e06aa0d3f9f8c7b078df0a1");

		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).isFile());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
		assertTrue(lastModified == new File(CACHE_DIR, TEST_RESOURCE).lastModified());
	}

	@Test
	public void downloadCacheExistingResourceWithIncorrectMD5Test() throws Exception {
		DownloadUtils.download(JETTY_TEST_RESOURCE_URL, new File(CACHE_DIR, TEST_RESOURCE));
		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(CACHE_DIR, TEST_RESOURCE).isFile());

		long lastModified = new File(CACHE_DIR, TEST_RESOURCE).lastModified();
		Thread.sleep(1000);

		JDownloadManager manager = new JDownloadManager(CACHE_DIR);
		try {
			manager.download(JETTY_TEST_RESOURCE_URL, TARGET_DIR, TEST_RESOURCE, "e4cd368a8e06aa0d3f9f8c7b078df0a2");
		} catch (RuntimeException re) {
			assertTrue(new File(CACHE_DIR, TEST_RESOURCE).exists());
			assertTrue(new File(CACHE_DIR, TEST_RESOURCE).isFile());
			assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
			assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
			assertTrue(lastModified < new File(CACHE_DIR, TEST_RESOURCE).lastModified());
			return;
		}
		fail("Manager should throw an exception about incorrect MD5!");
	}

	@Test
	public void downloadWithoutCacheTest() throws Exception {
		JDownloadManager manager = new JDownloadManager(null);
		manager.download(JETTY_TEST_RESOURCE_URL, TARGET_DIR);

		assertFalse(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
	}

	@Test
	public void downloadWithoutCacheUnpackTest() throws Exception {
		JDownloadManager manager = new JDownloadManager(null);
		manager.download(JETTY_TEST_RESOURCE_URL, TARGET_DIR, true);

		assertFalse(new File(CACHE_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
		assertTrue(new File(TARGET_DIR, "META-INF").exists());
		assertTrue(new File(TARGET_DIR, "META-INF").isDirectory());
	}

	@Test
	public void getNameTest() {
		assertEquals(TEST_RESOURCE, JDownloadManager.getName(JETTY_TEST_RESOURCE_URL));
	}

}
