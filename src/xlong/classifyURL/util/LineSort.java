/**
 * Project : Classify URLs
 */
package xlong.classifyURL.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class for sort big file by line.
 * 
 * @author Xiang Long (longx13@mails.tsinghua.edu.cn)
 */
public class LineSort {
	/** the MaxLine to sort directly */
	protected static int MaxLine = 1000000;

	/**
	 * Set maxLine
	 * 
	 * @param maxLine
	 *            the MaxLine to sort directly
	 */
	public static void setMaxLine(int maxLine) {
		MaxLine = maxLine;
	}

	/**
	 * Sort a big file by lines
	 * 
	 * @param filePath
	 *            the path of the file to sort
	 * @param outfilePath
	 *            the path of the output file
	 * @throws IOException
	 */
	public static void sortLines(String filePath, String outfilePath)
			throws IOException {
		ArrayList<String> partFilePaths = divParts(filePath);
		for (String partFilePath : partFilePaths) {
			sortPart(partFilePath);
		}
		unionParts(partFilePaths, outfilePath);
	}

	// Divide a big file into several small files which have at most MaxLine
	// lines.
	private static ArrayList<String> divParts(String filePath)
			throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(filePath));
		ArrayList<String> partFilePaths = new ArrayList<String>();
		BufferedWriter output = null;
		String s;
		int partID = 0;
		int cnt = 0;
		while ((s = input.readLine()) != null) {
			if (cnt % MaxLine == 0) {
				partID++;
				if (output != null)
					output.close();
				output = new BufferedWriter(new FileWriter(filePath + partID));
				partFilePaths.add(filePath + partID);
			}
			cnt++;
			output.write(s);
			output.newLine();
		}
		input.close();
		output.close();
		return partFilePaths;
	}

	// Sort a small file.
	private static void sortPart(String filePath) throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(filePath));
		String s;
		ArrayList<String> ls = new ArrayList<String>();
		while ((s = input.readLine()) != null) {
			ls.add(s);
		}
		input.close();
		Collections.sort(ls);
		BufferedWriter output = new BufferedWriter(new FileWriter(filePath));
		for (String ss : ls) {
			output.write(ss);
			output.newLine();
		}
		output.close();
	}

	// Union small files.
	private static void unionParts(ArrayList<String> partFilePaths,
			String outFilePath) throws IOException {
		int nParts = partFilePaths.size();
		BufferedWriter output = new BufferedWriter(new FileWriter(outFilePath));
		BufferedReader[] pinput = new BufferedReader[nParts];
		String[] curline = new String[nParts];
		for (int i = 0; i < nParts; i++) {
			pinput[i] = new BufferedReader(new FileReader(partFilePaths.get(i)));
			curline[i] = "";
		}
		while (true) {
			for (int i = 0; i < nParts; i++) {
				while (curline[i] != null && curline[i].equals("")) {
					curline[i] = pinput[i].readLine();
				}
			}
			int sID = -1;
			for (int i = 0; i < nParts; i++) {
				if (curline[i] != null) {
					if (sID == -1) {
						sID = i;
					} else if (curline[sID].compareTo(curline[i]) > 0) {
						sID = i;
					}
				}
			}
			if (sID == -1) {
				break;
			} else {
				output.write(curline[sID]);
				output.newLine();
				curline[sID] = "";
			}
		}
		output.close();
		for (int i = 0; i < nParts; i++) {
			pinput[i].close();
		}
		for (int i = 0; i < nParts; i++) {
			File f = new File(partFilePaths.get(i));
			f.delete();
		}
	}

	/**
	 * Testing code
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String filePath1 = "results/external_links.txt";
		String filePath2 = "results/instance_types.txt";
		try {
			sortLines(filePath1, filePath1);
			System.out.println("Finish 1");
			sortLines(filePath2, filePath2);
			System.out.println("Finish 2");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
