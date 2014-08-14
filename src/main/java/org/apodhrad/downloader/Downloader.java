package org.apodhrad.downloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 
 * @author apodhrad
 *
 */
public class Downloader {

	public static final long SPACE_KB = 1024;
	public static final long SPACE_MB = 1024 * SPACE_KB;
	public static final long SPACE_GB = 1024 * SPACE_MB;
	public static final long SPACE_TB = 1024 * SPACE_GB;

	public static final byte[] BUFFER = new byte[8192];

	protected String target;
	protected List<String> sources;

	public Downloader(String target, String... source) {
		this.target = target;
		if (source.length == 0) {
			this.sources = Arrays.asList(target);
		} else {
			this.sources = Arrays.asList(source);
		}
	}
	
	public void addSource(String source) {
		sources.add(source);
	}

	public void download(String url) {
		String name = getName(url);
		File file = new File(target, name);
		if (file.exists()) {
			System.out.println("File '" + name + "' already exists");
			return;
		}
		System.out.println("Downloading file '" + name + "'");
		try {
			download(url, target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyFile(File file, String target) {
		throw new UnsupportedOperationException();
	}

	public static void download(String url, String target) throws IOException {
		long lastTime = Calendar.getInstance().getTimeInMillis();

		HttpURLConnection connection = null;

		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream input = null;
		OutputStream output = null;
		try {
			connection.connect();
			// Check if the request is handled successfully
			if (connection.getResponseCode() / 100 != 2) {
				throw new RuntimeException("No file available at '" + url + "'");
			}
			int totalSize = connection.getContentLength();
			String name = getName(url);
			input = connection.getInputStream();
			output = new FileOutputStream(new File(target, name));
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
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (input != null) {
				input.close();
			}
			if (output != null) {
				output.flush();
				output.close();
			}
			connection.disconnect();
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

	public static String bytesToString(long sizeInBytes) {
		NumberFormat nf = new DecimalFormat();
		nf.setMaximumFractionDigits(2);

		try {
			if (sizeInBytes < SPACE_KB) {
				return nf.format(sizeInBytes) + " Byte(s)";
			} else if (sizeInBytes < SPACE_MB) {
				return nf.format(sizeInBytes / SPACE_KB) + " KB";
			} else if (sizeInBytes < SPACE_GB) {
				return nf.format(sizeInBytes / SPACE_MB) + " MB";
			} else if (sizeInBytes < SPACE_TB) {
				return nf.format(sizeInBytes / SPACE_GB) + " GB";
			} else {
				return nf.format(sizeInBytes / SPACE_TB) + " TB";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return sizeInBytes + " Byte(s)";
		}
	}
}
