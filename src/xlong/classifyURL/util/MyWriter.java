package xlong.classifyURL.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class for simple file output.
 * 
 * @author Xiang Long (longx13@mails.tsinghua.edu.cn)
 */
public class MyWriter {
	/** Buffered writer for simple output */
	protected static BufferedWriter curBufWriter = null;

	/**
	 * Set the file for output.
	 * 
	 * @param aFile
	 *            the file for output
	 * @param append
	 *            append or not
	 * @return success or not
	 */
	public static boolean setFile(File aFile, boolean append) {
		if (curBufWriter != null) {
			close();
		}
		if (aFile.isDirectory()) {
			return false;
		}
		aFile = aFile.getAbsoluteFile();
		File parentDir = new File(aFile.getParent());
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		try {
			curBufWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(aFile, append)));
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
		}
		return true;
	}

	/**
	 * Set the file for output.
	 * 
	 * @param path
	 *            the path of the file for output
	 * @param append
	 *            append or not
	 * @return success or not
	 */
	public static boolean setFile(String path, boolean append) {
		File aFile = new File(path);
		return setFile(aFile, append);
	}

	/**
	 * Close the file.
	 */
	public static void close() {
		try {
			curBufWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write the string to the file.
	 * 
	 * @param str
	 *            the string to write
	 */
	public static void write(String str) {
		if (curBufWriter == null) {
			System.err.println("Current BufferWriter not define.");
		}
		try {
			curBufWriter.write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write the string to the file with a endline mark.
	 * 
	 * @param str
	 *            the string to write
	 */
	public static void writeln(String str) {
		write(str + "\n");
	}
}
