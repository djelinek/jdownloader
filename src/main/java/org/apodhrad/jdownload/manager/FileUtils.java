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

	public static void copy(File source, File target) throws IOException {
		System.out.println("Copying file '" + source.getAbsolutePath() + "' to '" + target.getAbsolutePath() + "'");
		FileUtils.copyFileToDirectory(source, target);
	}

	public static void copy(File source, File target, String targetName) throws IOException {
		System.out.println("Copying file '" + source.getAbsolutePath() + "' to '"
				+ new File(target, targetName).getAbsolutePath() + "'");
		FileUtils.copyFile(source, new File(target, targetName));
	}

	public static String generateMD5(File file) throws NoSuchAlgorithmException, IOException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			int read;
			while ((read = is.read(BUFFER)) > 0) {
				digest.update(BUFFER, 0, read);
			}
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (is != null) {
				is.close();
			}
		}
		byte[] md5sum = digest.digest();
		BigInteger bigInt = new BigInteger(1, md5sum);
		return String.format("%32s", bigInt.toString(16)).replace(' ', '0');
	}

	public static void createDir(String dir) {
		createDir(new File(dir));
	}

	public static void createDir(File dir) {
		if (dir.mkdirs()) {
			System.out.println("Creating dir '" + dir + "'");
		}
	}
}
