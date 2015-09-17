package org.apodhrad.jdownload.manager.util;

import java.io.File;

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

	public static void createDir(String dir) {
		createDir(new File(dir));
	}

	public static void createDir(File dir) {
		if (dir.mkdirs()) {
			System.out.println("Creating dir '" + dir + "'");
		}
	}
}
