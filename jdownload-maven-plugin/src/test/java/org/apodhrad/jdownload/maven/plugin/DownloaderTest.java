package org.apodhrad.jdownload.maven.plugin;

import java.io.File;

public class DownloaderTest extends BetterAbstractMojoTestCase {

	/**
	 * @throws Exception
	 *             if any
	 */
	public void testSomething() throws Exception {
		File pom = getTestFile("src/test/resources/download-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		Downloader myMojo = (Downloader) lookupConfiguredMojo("download", pom);
		assertNotNull(myMojo);
		myMojo.execute();
	}
}
