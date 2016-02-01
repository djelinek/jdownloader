package org.apodhrad.jdownload.manager.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

/**
 * A utility for downloading a file.
 * 
 * @author apodhrad
 *
 */
public class DownloadUtils {

	public static final byte[] BUFFER = new byte[4 * 1024];

	public static void download(String url, File targetFile) throws IOException {
		UnpackUtils.createDir(targetFile.getParentFile());

		long lastTime = Calendar.getInstance().getTimeInMillis();

		System.out.println("Downloading '" + url + "' to '" + targetFile.getAbsolutePath() + "'");
		URLConnection connection = null;
		connection = new URL(url).openConnection();

		InputStream input = null;
		OutputStream output = null;
		try {
			connection.connect();
			int totalSize = connection.getContentLength();
			input = connection.getInputStream();
			output = new FileOutputStream(targetFile);
			int read = 0;
			int count = 0;
			while ((read = input.read(BUFFER)) != -1) {
				output.write(BUFFER, 0, read);
				count += read;
				long currentTime = Calendar.getInstance().getTimeInMillis();
				if (currentTime - lastTime > 1000) {
					lastTime = currentTime;
					printStatus(count, totalSize);
				}
			}
			printStatus(count, totalSize, '\n');
		} finally {
			if (input != null) {
				input.close();
			}
			if (output != null) {
				output.flush();
				output.close();
			}
		}
	}

	protected static void printStatus(int currentSize, int totalSize) {
		printStatus(currentSize, totalSize, '\r');
	}

	protected static void printStatus(int currentSize, int totalSize, char c) {
		float percentage = currentSize / totalSize * 100;
		System.out.printf("Downloaded %10d / %10d (%2.0f%%)" + c, currentSize, totalSize, percentage);
	}

	public static String getName(String url) {
		String[] parser = url.split("/");
		return parser[parser.length - 1];
	}

}
