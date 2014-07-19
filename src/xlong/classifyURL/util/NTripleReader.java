package xlong.classifyURL.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;


public class NTripleReader {
	private NxParser nxp;
	int cnt;
	
	public NTripleReader(String filePath) throws FileNotFoundException, IOException{
		nxp = new NxParser(new FileInputStream(filePath),false);
		cnt = 0;
	}
	
	public Node[] getNextTriple(){
		while (nxp.hasNext()) 
		{
			Node[] ns = nxp.next();
			if (ns.length == 3) {
				cnt ++;
				return ns;
			}
		}
		return null;
	}
	
	public void readAll(String outFile, int maxLines){
		if (!MyWriter.setFile(outFile, false)){
			System.err.println("MyWriter setFile fail.");
			System.exit(0);
		}
		
		Node[] ns;
		while ((ns = getNextTriple()) != null && cnt <= maxLines){
			MyWriter.writeln(ns[0].toString().substring(28) + " " + ns[2].toString());
		}
		System.out.println("Read lines: " + cnt);
		
		MyWriter.close();		
	}

	public static void main(String[] args) {
		int maxLines = 100000000;
		
		try {
			NTripleReader instanceTypesReader = new NTripleReader("E:/LXResearch/DBpedia/instance_types_en.nt");
			instanceTypesReader.readAll("results/instance_types.txt", maxLines);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			NTripleReader instanceTypesReader = new NTripleReader("E:/LXResearch/DBpedia/external_links_en.nt");
			instanceTypesReader.readAll("results/external_links.txt", maxLines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
