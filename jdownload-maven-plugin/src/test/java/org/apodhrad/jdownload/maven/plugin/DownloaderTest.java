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
