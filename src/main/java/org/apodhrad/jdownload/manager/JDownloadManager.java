package org.apodhrad.jdownload.manager;

import java.io.File;
import java.io.IOException;

/**
 * JDownload manager is a cache-managed download manager.
 * 
 * @author apodhrad
 *
 */
public class JDownloadManager {

	public static final byte[] BUFFER = new byte[8192];

	public static final String TARGET_DEFAULT = new File(System.getProperty("user.dir")).getAbsolutePath();
	public static final File CACHE_DEFAULT = new File(System.getProperty("user.home"), ".downloads");
	public static final String CACHE_PROPERTY = "downloader.source";

	private File cache;

	public JDownloadManager() {
		this(CACHE_DEFAULT);
	}

	public JDownloadManager(File cache) {
		if (cache == null) {
			return;
		}

		if (!cache.exists() && !cache.mkdirs()) {
			throw new RuntimeException("Cannot make dirs '" + cache.getAbsolutePath() + "'");
		}

		this.cache = isDirectory(cache);
	}

	public File getCache() {
		return cache;
	}

	public boolean isCacheManaged() {
		return getCache() != null;
	}

	public void download(String url, File target) throws IOException {
		download(url, target, false);
	}

	public void download(String url, File target, boolean unpack) throws IOException {
		download(url, target, DownloadUtils.getName(url), unpack);
	}

	public void download(String url, File target, String targetName) throws IOException {
		download(url, target, targetName, false);
	}

	public void download(String url, File target, String targetName, boolean unpack) throws IOException {
		checkNotNull(url, "url");
		checkNotNull(target, "target");
		checkNotNull(targetName, "targetName");

		File targetFile = new File(target, targetName);
		if (targetFile.exists()) {
			System.out.println("File '" + targetName + "' already exists in " + target);
			return;
		}

		if (isCacheManaged()) {
			File cacheFile = new File(getCache(), targetName);
			if (!cacheFile.exists()) {
				DownloadUtils.download(url, cacheFile);
			}
			FileUtils.copyFile(cacheFile, targetFile);
		} else {
			DownloadUtils.download(url, targetFile);
		}

		if (unpack) {
			FileUtils.unpack(targetFile, target);
		}

	}

	public static String getName(String url) {
		String[] parser = checkNotNull(url, "url").split("/");
		return parser[parser.length - 1];
	}

	private static <T> T checkNotNull(T object, String parameterName) {
		if (object == null) {
			throw new NullPointerException("The parameter '" + parameterName + "' cannot be null!");
		}
		return object;
	}

	private static File isDirectory(File file) {
		if (!file.isDirectory()) {
			throw new IllegalArgumentException("File '" + file.getAbsolutePath() + "' must be a directory!");
		}
		return file;
	}

}
