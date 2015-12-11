package org.apodhrad.jdownload.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apodhrad.jdownload.manager.JDownloadManager;
import org.apodhrad.jdownload.manager.hash.Hash;
import org.apodhrad.jdownload.manager.hash.MD5Hash;
import org.apodhrad.jdownload.manager.hash.NullHash;
import org.apodhrad.jdownload.manager.hash.SHA1Hash;
import org.apodhrad.jdownload.manager.hash.SHA256Hash;

/**
 * 
 * Maven plugin for JDownload Manager.
 * 
 * @author Andrej Podhradsky
 * 
 */
@Mojo(name = "download", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class Downloader extends AbstractMojo {

	@Component
	private BuildPluginManager manager;

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Parameter(defaultValue = "${session}")
	private MavenSession session;

	@Parameter(defaultValue = "${project.build.directory}")
	private String target;

	@Parameter(required = true)
	private URL url;

	@Parameter
	private File outpuDirectory;

	@Parameter(defaultValue = "false")
	private boolean unpack;

	@Parameter
	private String cache;

	@Parameter(defaultValue = "false")
	private boolean useCache;

	@Parameter(defaultValue = "false")
	private boolean skip;

	@Parameter
	private String md5;

	@Parameter
	private String sha1;

	@Parameter
	private String sha256;

	public void execute() throws MojoExecutionException {
		if (skip) {
			getLog().info("Downloading " + url + " is skipped.");
		}
		getLog().info("Downloading " + url + " has started");
		if (useCache) {
			System.setProperty(JDownloadManager.NOCACHE_PROPERTY, "true");
		}
		if (isDefined(cache)) {
			System.setProperty(JDownloadManager.CACHE_PROPERTY, cache);
		}
		JDownloadManager jDownloadManager = new JDownloadManager();
		File jDownloadCache = jDownloadManager.getCache();
		if (jDownloadCache != null) {
			getLog().info("The folder '" + new JDownloadManager().getCache().getAbsolutePath()
					+ "' will be used for caching.");
		} else {
			getLog().info("Download manager won't use any cache folder.");
		}

		Hash hash = new NullHash();
		if (isDefined(md5)) {
			hash = new MD5Hash(md5);
		}
		if (isDefined(sha1)) {
			hash = new SHA1Hash(sha1);
		}
		if (isDefined(sha256)) {
			hash = new SHA256Hash(sha256);
		}

		if (!isDefined(outpuDirectory)) {
			outpuDirectory = new File(target);
		}

		try {
			jDownloadManager.download(url.toString(), outpuDirectory, unpack, hash);
		} catch (IOException ioe) {
			throw new MojoExecutionException("I/O exception occured during downloading " + url.getPath(), ioe);
		}

		getLog().info("Downloading has finished");
	}

	private static boolean isDefined(Object parameter) {
		return parameter != null && parameter.toString().length() > 0;
	}

}
