package org.apodhrad.jdownload.manager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apodhrad.jdownload.manager.util.DownloadUtils;
import org.apodhrad.jdownload.manager.util.UnpackUtils;
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

public class DownloadUtilsTest {

	private static final int JETTY_SERVER_PORT = 8180;
	private static final String JETTY_RESOURCE_BASE = System.getProperty("jetty.resourceBase", "src/test/resources");
	private static final String TEST_RESOURCE = "gradle-wrapper.jar";
	private static final String RESOURCE_DIR = DownloadUtilsTest.class.getResource("/").getPath();
	private static final File TARGET_DIR = new File(RESOURCE_DIR, "target");

	private static Server server;

	@BeforeClass
	public static void startServer() throws Exception {
		assertNotNull("Systemproperty jetty.resourceBase is not set", JETTY_RESOURCE_BASE);
		if (!new File(JETTY_RESOURCE_BASE).exists()) {
			fail("'" + JETTY_RESOURCE_BASE + "' doesn't exist");
		}
		if (!new File(JETTY_RESOURCE_BASE, TEST_RESOURCE).exists()) {
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
	public void deleteTargetDirectory() throws IOException {
		UnpackUtils.deleteDirectory(TARGET_DIR);
	}

	@Test
	public void downloadWithSameTargetNameTest() throws Exception {
		DownloadUtils.download("http://localhost:8180/" + TEST_RESOURCE, new File(TARGET_DIR, TEST_RESOURCE));

		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).exists());
		assertTrue(new File(TARGET_DIR, TEST_RESOURCE).isFile());
	}

	@Test
	public void downloadWithDifferentTargetNameTest() throws Exception {
		DownloadUtils.download("http://localhost:8180/" + TEST_RESOURCE, new File(TARGET_DIR, "test.jar"));

		assertTrue(new File(TARGET_DIR, "test.jar").exists());
		assertTrue(new File(TARGET_DIR, "test.jar").isFile());
	}

	@Test(expected = FileNotFoundException.class)
	public void downloadNonExistingResourceTest() throws Exception {
		DownloadUtils.download("http://localhost:8180/test.jar", TARGET_DIR);
	}

	@Test
	public void downloadLocalFileTest() throws Exception {
		File localFile = new File("src/test/resources", TEST_RESOURCE);
		DownloadUtils.download("file://" + localFile.getAbsolutePath(), new File(TARGET_DIR, "test.jar"));

		assertTrue(new File(TARGET_DIR, "test.jar").exists());
		assertTrue(new File(TARGET_DIR, "test.jar").isFile());
	}

	@Test(expected = FileNotFoundException.class)
	public void downloadNonExistingLocalFileTest() throws Exception {
		File localFile = new File("src/test/resources", "test.jar");
		DownloadUtils.download("file://" + localFile.getAbsolutePath(), new File(TARGET_DIR, "test.jar"));
	}

}
