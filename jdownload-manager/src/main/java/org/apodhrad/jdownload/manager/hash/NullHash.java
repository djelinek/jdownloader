package org.apodhrad.jdownload.manager.hash;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * A hash implementation which always returns true.
 * 
 * @author apodhrad
 *
 */
public class NullHash extends Hash {

	public NullHash() {
		super(null);
	}

	@Override
	public boolean matches(File file) throws IOException {
		return true;
	}

	@Override
	public MessageDigest getMessageDigest() {
		return null;
	}

}
