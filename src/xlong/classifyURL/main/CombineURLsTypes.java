package xlong.classifyURL.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import xlong.classifyURL.beans.SimpleEntity;
import xlong.classifyURL.util.LineSort;
import xlong.classifyURL.util.NTripleReader;
import xlong.classifyURL.util.SubClassRelationReader;

public class CombineURLsTypes {
	public static void combine(String urlsFile, String typesFile, String combineFile) throws IOException{
		BufferedReader urlsReader = new BufferedReader(new FileReader(urlsFile));
		BufferedReader typesReader = new BufferedReader(new FileReader(typesFile));
		BufferedWriter combineWriter = new BufferedWriter(new FileWriter(combineFile));
		
		SimpleEntity urlsEntity = null;
		SimpleEntity typesEntity = null;
		String urlFirstLine;
		String typeFirstLine;
		String[] nameAndInfo;
		
		urlFirstLine = urlsReader.readLine();
		typeFirstLine = typesReader.readLine();
		
		while (true){
			if (urlsEntity == null){
				if (urlFirstLine == null) break;
				nameAndInfo = urlFirstLine.split(" ");
				urlsEntity = new SimpleEntity(nameAndInfo[0]);
				urlsEntity.addUrl(nameAndInfo[1]);
				while((urlFirstLine = urlsReader.readLine())!=null){
					nameAndInfo = urlFirstLine.split(" ");
					if (!nameAndInfo[0].equals(urlsEntity.name)) break;
					urlsEntity.addUrl(nameAndInfo[1]);
				}
//				System.out.println("next url entity");
//				System.out.println(urlsEntity.toString());
			}
			if (typesEntity == null){
				if (typeFirstLine == null) break;
				nameAndInfo = typeFirstLine.split(" ");
				typesEntity = new SimpleEntity(nameAndInfo[0]);
				typesEntity.addType(nameAndInfo[1]);
				while((typeFirstLine = typesReader.readLine())!=null){
					nameAndInfo = typeFirstLine.split(" ");
					if (!nameAndInfo[0].equals(typesEntity.name)) break;
					typesEntity.addType(nameAndInfo[1]);
				}
//				System.out.println("next type entity");
//				System.out.println(typesEntity.toString());
			}
			
			if (urlsEntity.equal(typesEntity)){
				urlsEntity.combine(typesEntity);
				combineWriter.write(urlsEntity.toString());
				combineWriter.newLine();
				urlsEntity = null;
				typesEntity = null;
			}
			else if (urlsEntity.name.compareTo(typesEntity.name) < 0){
				urlsEntity = null;
			}
			else if (urlsEntity.name.compareTo(typesEntity.name) > 0){
				typesEntity = null;
			}
		}
		
		urlsReader.close();
		typesReader.close();
		combineWriter.close();
	}
	
	public static void combineAndFilter(String urlsFile, String typesFile, String combineFile, Map<String,HashSet<String>> subClassMap) throws IOException{
		BufferedReader urlsReader = new BufferedReader(new FileReader(urlsFile));
		BufferedReader typesReader = new BufferedReader(new FileReader(typesFile));
		BufferedWriter combineWriter = new BufferedWriter(new FileWriter(combineFile));
		
		SimpleEntity urlsEntity = null;
		SimpleEntity typesEntity = null;
		String urlFirstLine;
		String typeFirstLine;
		String[] nameAndInfo;
		
		urlFirstLine = urlsReader.readLine();
		typeFirstLine = typesReader.readLine();
		
		while (true){
			if (urlsEntity == null){
				if (urlFirstLine == null) break;
				nameAndInfo = urlFirstLine.split(" ");
				urlsEntity = new SimpleEntity(nameAndInfo[0]);
				urlsEntity.addUrl(nameAndInfo[1]);
				while((urlFirstLine = urlsReader.readLine())!=null){
					nameAndInfo = urlFirstLine.split(" ");
					if (!nameAndInfo[0].equals(urlsEntity.name)) break;
					urlsEntity.addUrl(nameAndInfo[1]);
				}
//				System.out.println("next url entity");
//				System.out.println(urlsEntity.toString());
			}
			if (typesEntity == null){
				if (typeFirstLine == null) break;
				nameAndInfo = typeFirstLine.split(" ");
				typesEntity = new SimpleEntity(nameAndInfo[0]);
				typesEntity.addType(nameAndInfo[1]);
				while((typeFirstLine = typesReader.readLine())!=null){
					nameAndInfo = typeFirstLine.split(" ");
					if (!nameAndInfo[0].equals(typesEntity.name)) break;
					typesEntity.addType(nameAndInfo[1]);
				}
//				System.out.println("next type entity");
//				System.out.println(typesEntity.toString());
			}
			
			if (urlsEntity.equal(typesEntity)){
				urlsEntity.combineAndFilter(typesEntity, subClassMap);
				combineWriter.write(urlsEntity.toString());
				combineWriter.newLine();
				urlsEntity = null;
				typesEntity = null;
			}
			else if (urlsEntity.name.compareTo(typesEntity.name) < 0){
				urlsEntity = null;
			}
			else if (urlsEntity.name.compareTo(typesEntity.name) > 0){
				typesEntity = null;
			}
		}
		
		urlsReader.close();
		typesReader.close();
		combineWriter.close();
	}
	
	public static void run() throws IOException {
		String URLsFile = "E:/LXResearch/DBpedia/external_links_en.nt";
		String typesFile = "E:/LXResearch/DBpedia/instance_types_en.nt";
		String outURLsFile = "results/external_links.txt";
		String outTypesFile = "results/instance_types.txt";
//		String outCombineFile = "results/combine.txt";
		String outCombineAndFilterFile = "results/combineAndFilter.txt";
		Map<String, HashSet<String>> subClassMap = SubClassRelationReader.getSubClassOf("E:/LXResearch/DBpedia/dbpedia_3.9.owl");
		int maxLines = 100000000;
		int maxPartsLines = 1000000;
//		int maxLines = 100000;
//		int maxPartsLines = 10000;		
		try {
			NTripleReader URLsReader = new NTripleReader(URLsFile);
			URLsReader.readAll(outURLsFile, maxLines);
			System.out.println("Read 1");
			
			NTripleReader TypesReader = new NTripleReader(typesFile);
			TypesReader.readAll(outTypesFile, maxLines);
			System.out.println("Read 2");
			
			LineSort.setMaxLine(maxPartsLines);
			
			LineSort.sortLines(outURLsFile, outURLsFile);
			System.out.println("Sort 1");
			
			LineSort.sortLines(outTypesFile, outTypesFile);
			System.out.println("Sort 2");
			
//			combine(outURLsFile, outTypesFile, outCombineFile);
//			System.out.println("Combine");
			
			combineAndFilter(outURLsFile, outTypesFile, outCombineAndFilterFile, subClassMap);
			System.out.println("CombineAndFilter");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}