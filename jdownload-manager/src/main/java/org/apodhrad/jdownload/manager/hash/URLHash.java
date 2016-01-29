package org.apodhrad.jdownload.manager.hash;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

/**
 * URL hash implementation.
 * 
 * @author apodhrad
 *
 */
public class URLHash extends Hash {

	private Hash hash;

	public URLHash(String url) {
		super(getHashSumFromUrl(url));
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

	private static String getHashSumFromUrl(String url) {
		BufferedReader in = null;
		String sum = null;
		try {
			in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			sum = in.readLine();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Cannot get hash sum from " + url, e);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot get hash sum from " + url, e);
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

	public static void main(String[] args) throws Exception {
		new URL("asf56g7df68g7s7g");
	}

}
