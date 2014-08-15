package org.apodhrad.downloader;

import java.io.IOException;

/**
 * 
 * @author apodhrad
 *
 */
public class File {

	private String url;
	private String cache;
	private String target;
	private String targetName;
	private String md5;
	private boolean unpack;
	private boolean useCache;

	public File() {
		unpack = false;
		useCache = true;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCache() {
		return cache != null ? cache : Downloader.getCache();
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getTargetName() {
		return targetName != null ? targetName : Downloader.getName(url);
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

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public void download() throws IOException {
		if (useCache) {
			Downloader.download(getUrl(), getTarget(), getTargetName(), isUnpack());
		} else {
			Downloader.downloadWithoutCache(getUrl(), getTarget(), getTargetName());
			if (unpack) {
				Downloader.unpack(getTarget() + "/" + getTargetName(), getTarget());
			}
		}
	}
}
