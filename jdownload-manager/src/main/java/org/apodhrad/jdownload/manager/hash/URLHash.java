package org.apodhrad.jdownload.manager.hash;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;

import org.apodhrad.jdownload.manager.JDownloadManagerException;

/**
 * URL hash implementation.
 * 
 * @author apodhrad
 *
 */
public class URLHash extends Hash {

	public static final int NUMBER_OF_RETRIES = 5;

	private Hash hash;

	public URLHash(String url) {
		super(getHashSumFromUrl(url, NUMBER_OF_RETRIES));
		if (url.toLowerCase().endsWith("md5")) {
			hash = new MD5Hash(sum);
		} else if (url.toLowerCase().endsWith("sha1")) {
			hash = new SHA1Hash(sum);
		} else if (url.toLowerCase().endsWith("sha256")) {
			hash = new SHA256Hash(sum);
		} else {
			throw new IllegalArgumentException("Unsupported hash sum");
		}
	}

	private static String getHashSumFromUrl(String url, int numberOfRetries) {
		Exception exception = null;
		String sum = null;
		int count = 0;
		while (sum == null) {
			if (count++ > numberOfRetries) {
				throw new JDownloadManagerException(
						"Cannot get hash sum from " + url + " after " + numberOfRetries + " retries", exception);
			}
			try {
				sum = getHashSumFromUrl(url);
			} catch (Exception e) {
				exception = e;
			}
		}
		return sum;
	}

	private static String getHashSumFromUrl(String url) throws IOException {
		BufferedReader in = null;
		String sum = null;
		try {
			in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			sum = in.readLine();
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sum;
	}

	@Override
	public MessageDigest getMessageDigest() {
		return hash.getMessageDigest();
	}

	@Override
	public boolean matches(File file) throws IOException {
		return hash.matches(file);
	}

	@Override
	public String getLastMatchingMessage() {
		return hash.getLastMatchingMessage();
	}

	@Override
	public String toString() {
		return hash.toString();
	}

}
