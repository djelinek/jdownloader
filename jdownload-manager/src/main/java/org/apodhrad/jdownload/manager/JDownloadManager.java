package org.apodhrad.jdownload.manager;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;

import org.apodhrad.jdownload.manager.hash.Hash;
import org.apodhrad.jdownload.manager.hash.NullHash;
import org.apodhrad.jdownload.manager.util.DownloadUtils;
import org.apodhrad.jdownload.manager.util.UnpackUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDownload manager is a cache-managed download manager.
 * 
 * @author apodhrad
 *
 */
public class JDownloadManager {

	public static final byte[] BUFFER = new byte[8192];

	public static final String CACHE_PROPERTY = "jdownload.cache";

	public static final String CACHE_VARIABLE = "JDOWNLOAD_CACHE";

	public static final String NOCACHE_PROPERTY = "jdownload.nocache";
	
	private static Logger log = LoggerFactory.getLogger(JDownloadManager.class);

	private File cache;

	/**
	 * Creates a new download manager with the default cache folder ~/Downloads.
	 * You can override the default cache by setting the system property
	 * jdownload.cache or by setting the environment variable JDOWNLOAD_CACHE.
	 * If you don't want to use any cache, set the system property
	 * jdownload.nocache to true.
	 */
	public JDownloadManager() {
		this(getDefaultCache());
	}

	/**
	 * Creates a new download manager with a given cache folder. If you don't
	 * want to use a cache folder then pass null argument.
	 * 
	 * @param cache
	 *            Cache folder
	 */
	public JDownloadManager(File cache) {
		if (cache == null) {
			return;
		}

		if (!cache.exists() && !cache.mkdirs()) {
			throw new RuntimeException("Cannot make dirs '" + cache.getAbsolutePath() + "'");
		}

		this.cache = isDirectory(cache);
	}

	private static File getDefaultCache() {
		String nocacheProperty = System.getProperty(NOCACHE_PROPERTY, "false");
		if (Boolean.valueOf(nocacheProperty)) {
			return null;
		}
		String cacheProperty = System.getProperty(CACHE_PROPERTY);
		if (cacheProperty != null && cacheProperty.length() > 0) {
			return new File(cacheProperty);
		}
		String cacheVariable = System.getenv(CACHE_VARIABLE);
		if (cacheVariable != null && cacheVariable.length() > 0) {
			return new File(cacheVariable);
		}
		return new File(System.getProperty("user.home"), "Downloads");
	}

	/**
	 * Returns the cache folder.
	 * 
	 * @return Cache folder
	 */
	public File getCache() {
		return cache;
	}

	/**
	 * Returns whether the download manager uses a cache.
	 * 
	 * @return true if a cache folder is defined
	 */
	public boolean isCacheManaged() {
		return getCache() != null;
	}

	/**
	 * Downloads a file from a given url to the specified target folder.
	 * 
	 * @param url
	 *            Url
	 * @param target
	 *            Target folder
	 * @return The downloaded file
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public File download(String url, File target) throws IOException {
		return download(url, target, new NullHash());
	}

	/**
	 * Downloads a file from a given url to the specified target folder.
	 * 
	 * @param url
	 *            Url
	 * @param target
	 *            Target folder
	 * @param hash
	 *            Check the downloaded file with a hash
	 * @return The downloaded file
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public File download(String url, File target, Hash hash) throws IOException {
		return download(url, target, false, hash);
	}

	/**
	 * Downloads a file from a given url to the specified target folder. The
	 * downloaded file is extracted if the parameter unpack is set to true.
	 * 
	 * @param url
	 *            Url
	 * @param target
	 *            Target folder
	 * @param unpack
	 *            Extract the downloaded file?
	 * @return The downloaded file
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public File download(String url, File target, boolean unpack) throws IOException {
		return download(url, target, DownloadUtils.getName(url), unpack);
	}

	/**
	 * Downloads a file from a given url to the specified target folder. The
	 * downloaded file is extracted if the parameter unpack is set to true.
	 * 
	 * @param url
	 *            Url
	 * @param target
	 *            Target folder
	 * @param unpack
	 *            Extract the downloaded file?
	 * @param hash
	 *            Check the downloaded file with a hash
	 * @return The downloaded file
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public File download(String url, File target, boolean unpack, Hash hash) throws IOException {
		return download(url, target, DownloadUtils.getName(url), unpack, hash);
	}

	/**
	 * Downloads a file from a given url to the specified target folder with the
	 * target name.
	 * 
	 * @param url
	 *            Url
	 * @param target
	 *            Target folder
	 * @param targetName
	 *            Target name
	 * @return The downloaded file
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public File download(String url, File target, String targetName) throws IOException {
		return download(url, target, targetName, false);
	}

	/**
	 * Downloads a file from a given url to the specified target folder with the
	 * target name.
	 * 
	 * @param url
	 *            Url
	 * @param target
	 *            Target folder
	 * @param targetName
	 *            Target name
	 * @param hash
	 *            Check the downloaded file with a hash
	 * @return The downloaded file
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public File download(String url, File target, String targetName, Hash hash) throws IOException {
		return download(url, target, targetName, false, hash);
	}

	/**
	 * Downloads a file from a given url to the specified target folder with the
	 * target name. The downloaded file is extracted if the parameter unpack is
	 * set to true.
	 * 
	 * @param url
	 *            Url
	 * @param target
	 *            Target folder
	 * @param targetName
	 *            Target name
	 * @param unpack
	 *            Extract the downloaded file?
	 * @return The downloaded file
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public File download(String url, File target, String targetName, boolean unpack) throws IOException {
		return download(url, target, targetName, unpack, new NullHash());
	}

	/**
	 * Downloads a file from a given url to the specified target folder with the
	 * target name. The downloaded file is extracted if the parameter unpack is
	 * set to true.
	 * 
	 * @param url
	 *            Url
	 * @param target
	 *            Target folder
	 * @param targetName
	 *            Target name
	 * @param unpack
	 *            Extract the downloaded file?
	 * @param hash
	 *            Check the downloaded file with a hash
	 * @return The downloaded file
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public File download(String url, File target, String targetName, boolean unpack, Hash hash) throws IOException {
		requireNonNull(url, "url caanot be null");
		requireNonNull(target, "target cannot be null");
		requireNonNull(targetName, "targetName cannot be null");
		requireNonNull(targetName, "hash cannot be null, use NullHash");

		File targetFile = new File(target, targetName);
		if (targetFile.exists() && hash.matches(targetFile)) {
			log.info("File '" + targetName + "' already exists in " + target);
			return targetFile;
		}

		if (isCacheManaged()) {
			File cacheFile = new File(getCache(), targetName);
			if (!cacheFile.exists() || !hash.matches(cacheFile)) {
				DownloadUtils.download(url, cacheFile);
			}
			UnpackUtils.copyFile(cacheFile, targetFile);
		} else {
			DownloadUtils.download(url, targetFile);
		}

		if (!hash.matches(targetFile)) {
			throw new RuntimeException(hash.getLastMatchingMessage());
		}

		if (unpack) {
			UnpackUtils.unpack(targetFile, target);
		}

		return targetFile;
	}

	public static String getName(String url) {
		String[] parser = requireNonNull(url, "url cannot be null").split("/");
		return parser[parser.length - 1];
	}

	public static File isDirectory(File file) {
		if (!file.isDirectory()) {
			throw new IllegalArgumentException("File '" + file.getAbsolutePath() + "' must be a directory!");
		}
		return file;
	}

}
