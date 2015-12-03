package org.apodhrad.jdownload.maven.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.junit.BeforeClass;
import org.junit.Test;

public class DownloaderIntegrationTest {

	private static String TARGET;

	@BeforeClass
	public static void beforeClass() throws IOException {
		TARGET = systemProperty("project.build.directory");
	}

	@Test
	public void downloadMavenTest() throws Exception {
		File pomFile = prepareMavenProject("download_maven");
		
		System.out.println("*** " + pomFile.getAbsolutePath() + " ***");

		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(pomFile);
		request.setGoals(Collections.singletonList("package"));

		Properties systemProperties = new Properties();
		systemProperties.put("jdownload.version", System.getProperty("jdownload.version"));
		request.setProperties(systemProperties);

		Invoker invoker = new DefaultInvoker();
		InvocationResult result = invoker.execute(request);

		int exitCode = result.getExitCode();
		assertEquals("Build failed (exit code " + exitCode + ")", 0, exitCode);
	}

	static public String systemProperty(String key) {
		String value = System.getProperty(key);
		assertTrue("The system property '" + key + "' must be defined!", value != null && value.length() > 0);
		return value;
	}

	private File prepareMavenProject(String name) throws IOException {
		URL url = DownloaderIntegrationTest.class.getResource("/" + name + ".xml");
		File target = new File(TARGET, name);
		target.mkdir();
		File pomFile = new File(target, "pom.xml");
		FileUtils.copyURLToFile(url, pomFile);
		return pomFile;
	}
}
