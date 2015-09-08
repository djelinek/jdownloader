package org.apodhrad.jdownload.manager;

import java.io.File;
import java.io.IOException;

/**
 * Cache-Managed Downloader.
 * 
 * @author apodhrad
 *
 */
public class Downloader {

	public static final byte[] BUFFER = new byte[8192];

	public static final String TARGET_DEFAULT = new File(System.getProperty("user.dir")).getAbsolutePath();
	public static final String CACHE_DEFAULT = new File(System.getProperty("user.home"), ".downloads")
			.getAbsolutePath();
	public static final String CACHE_PROPERTY = "downloader.source";

	private String url;
	private String cache;
	private String target;
	private String targetName;
	private String md5;
	private boolean unpack;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	/*
	 * /** Returns cache folder absolute path from system property {@value #CACHE_PROPERTY}. If the property doesn't
	 * exist then the cache folder is set to ~/.downloads. The cache folder is automatically created.
	 * 
	 * @return cache folder absolute path
	 */
	public String getCache() {
		return cache;
		// String cache = System.getProperty(CACHE_PROPERTY, CACHE_DEFAULT);
		// File cacheFile = new File(cache);
		// if (!cacheFile.exists() && !cacheFile.mkdirs()) {
		// throw new RuntimeException("Cannot create " + cache);
		// }
		// return cache;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getTargetName() {
		return targetName != null ? targetName : DownloadUtils.getName(url);
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public boolean isUnpack() {
		return unpack;
	}

	public void setUnpack(boolean unpack) {
		this.unpack = unpack;
	}

	public void download() throws IOException {
		String cache = getCache();
		String target = getTarget();
		String targetName = getTargetName();
		String originalName = DownloadUtils.getName(url);

		File file = new File(target, targetName);
		if (file.exists()) {
			System.out.println("File '" + targetName + "' already exists in " + target);
			return;
		}
		if (cache != null) {
			file = new File(cache, originalName);
			if (!file.exists()) {
				DownloadUtils.download(url, cache, originalName);
			}
			FileUtils.copy(new File(cache, originalName).getAbsolutePath(), target, targetName);
		} else {
			DownloadUtils.download(url, target, targetName);
		}
		if (unpack) {
			FileUtils.unpack(new File(target, targetName), new File(target));
		}
	}

	public static String getName(String url) {
		String[] parser = url.split("/");
		return parser[parser.length - 1];
	}

}
