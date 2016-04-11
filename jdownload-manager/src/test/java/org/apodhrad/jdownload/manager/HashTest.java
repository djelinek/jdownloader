package org.apodhrad.jdownload.manager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apodhrad.jdownload.manager.hash.MD5Hash;
import org.apodhrad.jdownload.manager.hash.NullHash;
import org.apodhrad.jdownload.manager.hash.SHA1Hash;
import org.apodhrad.jdownload.manager.hash.SHA256Hash;
import org.apodhrad.jdownload.manager.hash.URLHash;
import org.junit.Test;

/**
 * 
 * @author apodhrad
 *
 */
public class HashTest {

	private static final String JETTY_RESOURCE_BASE = System.getProperty("jetty.resourceBase", "src/test/resources");
	private static final String TEST_RESOURCE = "gradle-wrapper.jar";

	@Test
	public void md5CorrectHashTest() throws IOException {
		File file = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE);
		assertTrue(new MD5Hash("e4cd368a8e06aa0d3f9f8c7b078df0a1").matches(file));
	}

	@Test
	public void md5InorrectHashTest() throws IOException {
		File file = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE);
		assertFalse(new MD5Hash("e4cd368a8e06aa0d3f9f8c7b078df0a2").matches(file));
	}

	@Test
	public void sha1CorrectHashTest() throws IOException {
		File file = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE);
		assertTrue(new SHA1Hash("2269ad58cd50940d79c813208af2ceea87a9bd6b").matches(file));
	}

	@Test
	public void sha1InorrectHashTest() throws IOException {
		File file = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE);
		assertFalse(new SHA1Hash("2269ad58cd50940d79c813208af2ceea87a9bd6c").matches(file));
	}

	@Test
	public void sha256CorrectHashTest() throws IOException {
		File file = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE);
		assertTrue(new SHA256Hash("dea5ceba47b58df0b7f69a65b24357527c1927ccc72b6d4ed90658d39e461b29").matches(file));
	}

	@Test
	public void sha256InorrectHashTest() throws IOException {
		File file = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE);
		assertFalse(new SHA256Hash("dea5ceba47b58df0b7f69a65b24357527c1927ccc72b6d4ed90658d39e461b30").matches(file));
	}

	@Test
	public void nullHashTest() throws IOException {
		File file = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE);
		assertTrue(new NullHash().matches(file));
	}

	@Test
	public void urlMd5HashTest() throws IOException {
		File file = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE);
		File hashFile = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE + ".md5");
		assertTrue(new URLHash("file://" + hashFile.getAbsolutePath()).matches(file));
	}

	@Test
	public void urlSha1HashTest() throws IOException {
		File file = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE);
		File hashFile = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE + ".sha1");
		assertTrue(new URLHash("file://" + hashFile.getAbsolutePath()).matches(file));
	}

	@Test
	public void urlSha256HashTest() throws IOException {
		File file = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE);
		File hashFile = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE + ".sha256");
		assertTrue(new URLHash("file://" + hashFile.getAbsolutePath()).matches(file));
	}

	@Test
	public void urlMalformedHashTest() throws IOException {
		File hashFile = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE + ".md5");
		try {
			new URLHash("fil://" + hashFile.getAbsolutePath());
		} catch (JDownloadManagerException e) {
			assertTrue("Expected MalformedURLException", e.getCause() instanceof MalformedURLException);
			return;
		}
		fail("Malformed URL wasn't detected");
	}

	@Test
	public void urlUnexistingHashTest() throws IOException {
		File hashFile = new File(JETTY_RESOURCE_BASE, TEST_RESOURCE + ".foo");
		try {
			new URLHash("file://" + hashFile.getAbsolutePath());
		} catch (JDownloadManagerException e) {
			assertTrue("Expected FileNotFoundException", e.getCause() instanceof FileNotFoundException);
			return;
		}
		fail("Nonexisting URL wasn't detected");
	}

}
