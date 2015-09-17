package org.apodhrad.jdownload.manager.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author apodhrad
 *
 */
public class SHA256Hash extends Hash {

	private static MessageDigest MESSAGE_DIGEST;

	public SHA256Hash(String sha1sum) {
		super(sha1sum);
	}

	public MessageDigest getMessageDigest() {
		if (MESSAGE_DIGEST == null) {
			try {
				MESSAGE_DIGEST = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException ex) {
				throw new RuntimeException(ex);
			}
		}
		return MESSAGE_DIGEST;
	}

}
