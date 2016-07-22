package org.apodhrad.jdownload.maven.plugin;

import java.io.File;

/**
 * 
 * @author apodhrad
 *
 */
public class DownloaderTest extends BetterAbstractMojoTestCase {

	public void testDownload() throws Exception {
		File pom = getTestFile("src/test/resources/download-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Downloader myMojo = (Downloader) lookupConfiguredMojo("download", pom);
		assertNotNull(myMojo);
		myMojo.execute();

		assertTrue(new File(pom.getParent(), "target/apache-maven-3.3.1-bin.zip").exists());
	}

	public void testDownloadUnpack() throws Exception {
		File pom = getTestFile("src/test/resources/download-unpack-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Downloader myMojo = (Downloader) lookupConfiguredMojo("download", pom);
		assertNotNull(myMojo);
		myMojo.execute();

		assertTrue(new File(pom.getParent(), "target/downloads/apache-maven-3.3.1-bin.zip").exists());
	}

	public void testDownloadUrlHash() throws Exception {
		File pom = getTestFile("src/test/resources/download-urlhash-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Downloader myMojo = (Downloader) lookupConfiguredMojo("download", pom);
		assertNotNull(myMojo);
		myMojo.execute();
	}

}
