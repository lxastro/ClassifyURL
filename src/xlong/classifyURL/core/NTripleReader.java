/**
 * Project : Classify URLs
 */
package xlong.classifyURL.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import xlong.classifyURL.util.MyWriter;

/**
 * Class for reading Ntriples.
 * 
 * @author Xiang Long (longx13@mails.tinghua.edu.cn)
 */
public class NTripleReader {
	/** The NxParser used to parse file */
	protected NxParser nxp;
	/** Counts of triples */
	protected int cnt;
	/** Output logs or not. */
	protected static boolean outputLogs = true;
	/** Normalize URL or not. */
	protected static boolean normalize = true;
	/**
	 * Constructor
	 * 
	 * @param filePath
	 *            the path of the file to read.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public NTripleReader(String filePath) throws FileNotFoundException,
			IOException {
		nxp = new NxParser(new FileInputStream(filePath), false);
		cnt = 0;
	}

	/**
	 * Set output logs or not.
	 * 
	 * @param outputLogs
	 */
	public static void setLog(boolean _outputLogs) {
		outputLogs = _outputLogs;
	}

	/**
	 * Get next triple
	 * 
	 * @return next triple
	 */
	protected Node[] getNextTriple() {
		while (nxp.hasNext()) {
			Node[] ns = nxp.next();
			if (ns.length == 3) {
				cnt++;
				return ns;
			}
		}
		return null;
	}

	/**
	 * Read Ntriples and write result into a file.
	 * 
	 * @param outFile
	 *            the file output reading result.
	 * @param maxNum
	 *            the max number of triples to read.
	 */
	public void readAll(String outFile, int maxNum) {
		if (!MyWriter.setFile(outFile, false)) {
			System.err.println("MyWriter setFile fail.");
			System.exit(0);
		}

		Node[] ns;
		String name;
		String prop;
		while ((ns = getNextTriple()) != null && cnt <= maxNum) {
			if (normalize){
				name = UrlNormalizer.normalize(ns[0].toString()).substring(28);
				prop = UrlNormalizer.normalize(ns[2].toString());
			}
			else{
				name = ns[0].toString().substring(28);
				prop = ns[2].toString();
			}
			
			MyWriter.writeln(name + " " + prop);
		}
		if (outputLogs) {
			System.out.println("Read lines: " + cnt);
		}

		MyWriter.close();
	}

	/**
	 * Testing code
	 */
	public static void main(String[] args) {
		int maxLines = 100000000;

		try {
			NTripleReader instanceTypesReader = new NTripleReader(
					"E:/LXResearch/DBpedia/instance_types_en.nt");
			instanceTypesReader.readAll("results/instance_types.txt", maxLines);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			NTripleReader instanceTypesReader = new NTripleReader(
					"E:/LXResearch/DBpedia/external_links_en.nt");
			instanceTypesReader.readAll("results/external_links.txt", maxLines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
