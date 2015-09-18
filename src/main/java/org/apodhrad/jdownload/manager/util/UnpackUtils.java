package org.apodhrad.jdownload.manager.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.ArchiverFactory;

/**
 * A utility for extracting archives.
 * 
 * @author apodhrad
 *
 */
public class UnpackUtils extends FileUtils {

	public static void unpack(String file, String target) throws IOException {
		unpack(new File(file), new File(target));
	}

	public static void unpack(File file, File target) throws IOException {
		createDir(target);
		System.out.println("Unpacking file '" + file.getAbsolutePath() + "' to '" + target.getAbsolutePath() + "'");
		ArchiverFactory.createArchiver(file).extract(file, target);
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
