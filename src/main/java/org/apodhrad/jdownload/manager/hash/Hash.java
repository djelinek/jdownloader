package org.apodhrad.jdownload.manager.hash;

import static org.apache.commons.io.FileUtils.ONE_KB;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * 
 * @author apodhrad
 *
 */
public abstract class Hash {

	public static final byte[] BUFFER = new byte[4 * (int) ONE_KB];

	private String sum;

	public Hash(String sum) {
		this.sum = sum;
	}

	public abstract MessageDigest getMessageDigest();

	public boolean matches(File file) throws IOException {
		MessageDigest messageDigest = getMessageDigest();
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			int read;
			while ((read = is.read(BUFFER)) > 0) {
				messageDigest.update(BUFFER, 0, read);
			}
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (is != null) {
				is.close();
			}
		}
		byte[] hash = messageDigest.digest();
		return sum.equals(convertToHex(hash));
	}

	public static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}
}
