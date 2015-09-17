package org.apodhrad.jdownload.manager.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA-1 hash implementation.
 * 
 * @author apodhrad
 *
 */
public class SHA1Hash extends Hash {

	private static MessageDigest MESSAGE_DIGEST;

	public SHA1Hash(String sha1sum) {
		super(sha1sum);
	}

	@Override
	public MessageDigest getMessageDigest() {
		if (MESSAGE_DIGEST == null) {
			try {
				MESSAGE_DIGEST = MessageDigest.getInstance("SHA-1");
			} catch (NoSuchAlgorithmException ex) {
				throw new RuntimeException(ex);
			}
		}
		return MESSAGE_DIGEST;
	}

}
