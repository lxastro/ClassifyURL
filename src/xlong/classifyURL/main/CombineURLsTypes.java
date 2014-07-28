/**
 * Project : Classify URLs
 */
package xlong.classifyURL.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import xlong.classifyURL.core.NTripleReader;
import xlong.classifyURL.core.SimpleEntity;
import xlong.classifyURL.core.SubClassRelationReader;
import xlong.classifyURL.util.LineSort;
import xlong.classifyURL.util.PropertiesUtil;

public class CombineURLsTypes {

	/** Output logs or not. */
	protected static boolean outputLogs = true;

	/**
	 * Set output logs or not.
	 * 
	 * @param outputLogs
	 */
	public static void setLog(boolean _outputLogs) {
		outputLogs = _outputLogs;
	}
	
	/**
	 * Combine and filter the URLs and types file.
	 * 
	 * @param urlsFile
	 *            path of the file contain URLs
	 * @param typesFile
	 *            path of the file contain types
	 * @param combineFile
	 *            path of the output combineFile
	 * @param subClassMap
	 *            subclassof relationship map
	 * @throws IOException
	 */
	public static void combineAndFilter(String urlsFile, String typesFile,
			String combineFile, Map<String, HashSet<String>> subClassMap)
			throws IOException {
		BufferedReader urlsReader = new BufferedReader(new FileReader(urlsFile));
		BufferedReader typesReader = new BufferedReader(new FileReader(
				typesFile));
		BufferedWriter combineWriter = new BufferedWriter(new FileWriter(
				combineFile));

		SimpleEntity urlsEntity = null;
		SimpleEntity typesEntity = null;
		String urlFirstLine;
		String typeFirstLine;
		String[] nameAndInfo;

		urlFirstLine = urlsReader.readLine();
		typeFirstLine = typesReader.readLine();

		while (true) {
			if (urlsEntity == null) {
				if (urlFirstLine == null)
					break;
				nameAndInfo = urlFirstLine.split(" ");
				urlsEntity = new SimpleEntity(nameAndInfo[0]);
				urlsEntity.addURL(nameAndInfo[1]);
				while ((urlFirstLine = urlsReader.readLine()) != null) {
					nameAndInfo = urlFirstLine.split(" ");
					if (!nameAndInfo[0].equals(urlsEntity.getName()))
						break;
					urlsEntity.addURL(nameAndInfo[1]);
				}
				// System.out.println("next url entity");
				// System.out.println(urlsEntity.toString());
			}
			if (typesEntity == null) {
				if (typeFirstLine == null)
					break;
				nameAndInfo = typeFirstLine.split(" ");
				typesEntity = new SimpleEntity(nameAndInfo[0]);
				typesEntity.addType(nameAndInfo[1]);
				while ((typeFirstLine = typesReader.readLine()) != null) {
					nameAndInfo = typeFirstLine.split(" ");
					if (!nameAndInfo[0].equals(typesEntity.getName()))
						break;
					typesEntity.addType(nameAndInfo[1]);
				}
				// System.out.println("next type entity");
				// System.out.println(typesEntity.toString());
			}

			if (urlsEntity.equals(typesEntity)) {
				urlsEntity.combine(typesEntity);
				if (subClassMap != null) {
					urlsEntity.FilterTypes(subClassMap);
				}
				combineWriter.write(urlsEntity.toString());
				combineWriter.newLine();
				urlsEntity = null;
				typesEntity = null;
			} else if (urlsEntity.getName().compareTo(typesEntity.getName()) < 0) {
				urlsEntity = null;
			} else if (urlsEntity.getName().compareTo(typesEntity.getName()) > 0) {
				typesEntity = null;
			}
		}

		urlsReader.close();
		typesReader.close();
		combineWriter.close();
	}

	public static void run() throws IOException {
		String URLsFile = PropertiesUtil.getProperty("DBpedia_external_links.nt");
		String typesFile = PropertiesUtil.getProperty("DBpedia_instance_types.nt");
		String ontologyFile = PropertiesUtil.getProperty("DBpedia_ontology.owl");
		
		String outURLsFile = "temp/external_links.txt";
		String outTypesFile = "temp/instance_types.txt";
		String outCombineAndFilterFile = "temp/combineAndFilter.txt";
		Map<String, HashSet<String>> subClassMap = SubClassRelationReader
				.getSubClassOf(ontologyFile);
		int maxLines = 100000000;
		int maxPartsLines = 1000000;
//		int maxLines = 1000000;
//		int maxPartsLines = 100000;
		
		try {
			NTripleReader URLsReader = new NTripleReader(URLsFile);
			URLsReader.readAll(outURLsFile, maxLines);
			if (outputLogs) System.out.println("Read 1");

			NTripleReader TypesReader = new NTripleReader(typesFile);
			TypesReader.readAll(outTypesFile, maxLines);
			if (outputLogs)System.out.println("Read 2");

			LineSort.setMaxLine(maxPartsLines);

			LineSort.sortLines(outURLsFile, outURLsFile);
			if (outputLogs)System.out.println("Sort 1");

			LineSort.sortLines(outTypesFile, outTypesFile);
			if (outputLogs)System.out.println("Sort 2");
			
			combineAndFilter(outURLsFile, outTypesFile,
					outCombineAndFilterFile, subClassMap);
			if (outputLogs)System.out.println("CombineAndFilter");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
