package org.apodhrad.jdownload.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLogger;

/**
 * 
 * @author apodhrad
 *
 */
public class FileUtils extends org.codehaus.plexus.util.FileUtils {

	public static final byte[] BUFFER = new byte[4 * ONE_KB];
	private static MessageDigest MD5_DIGEST;

	public static void unpack(String file, String target) {
		unpack(new File(file), new File(target));
	}

	public static void unpack(File file, File target) {
		createDir(target);

		System.out.println("Unpacking file '" + file.getAbsolutePath() + "' to '" + target.getAbsolutePath() + "'");
		AbstractUnArchiver unarchiver = getUnArchiver(file);
		unarchiver.enableLogging(new ConsoleLogger(org.codehaus.plexus.logging.Logger.LEVEL_INFO, "console"));
		unarchiver.setSourceFile(file);
		unarchiver.setDestDirectory(target);
		unarchiver.extract();
	}

	protected static AbstractUnArchiver getUnArchiver(File file) {
		String name = file.getName().toLowerCase();
		if (name.endsWith(".zip") || name.endsWith(".jar")) {
			return new ZipUnArchiver();
		}
		if (name.endsWith(".tar.gz")) {
			return new TarGZipUnArchiver();
		}
		throw new IllegalArgumentException("Unsupported file format");
	}

	public static String generateMD5(File file) throws IOException {
		if (MD5_DIGEST == null) {
			try {
				MD5_DIGEST = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException ex) {
				throw new RuntimeException(ex);
			}
		}
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			int read;
			while ((read = is.read(BUFFER)) > 0) {
				MD5_DIGEST.update(BUFFER, 0, read);
			}
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (is != null) {
				is.close();
			}
		}
		byte[] md5sum = MD5_DIGEST.digest();
		BigInteger bigInt = new BigInteger(1, md5sum);
		return String.format("%32s", bigInt.toString(16)).replace(' ', '0');
	}

	public static void createDir(String dir) {
		createDir(new File(dir));
	}

	public static boolean matchMD5(File file, String expectedMD5) throws IOException {
		if (expectedMD5 == null) {
			return true;
		}
		String actualMD5 = generateMD5(file);
		return expectedMD5.equals(actualMD5);
	}

	public static void createDir(File dir) {
		if (dir.mkdirs()) {
			System.out.println("Creating dir '" + dir + "'");
		}
	}
}
