package org.apodhrad.jdownload.manager.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 hash implementation.
 * 
 * @author apodhrad
 *
 */
public class MD5Hash extends Hash {

	private static MessageDigest MESSAGE_DIGEST;

	public MD5Hash(String md5sum) {
		super(md5sum);
	}

	@Override
	public MessageDigest getMessageDigest() {
		if (MESSAGE_DIGEST == null) {
			try {
				MESSAGE_DIGEST = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException ex) {
				throw new RuntimeException(ex);
			}
		}
		return MESSAGE_DIGEST;
	}

	@Override
	public String toString() {
		return "MD5 " + sum;
	}

}
