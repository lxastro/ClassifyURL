/**
 * Project : Classify URLs
 */
package xlong.classifyURL.main;

import java.io.File;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.tokenizers.Tokenizer;
import xlong.classifyURL.core.NCharGramTokenizer;
import xlong.classifyURL.core.StringToWordVectorConverter;
import xlong.classifyURL.util.MyWriter;

public class ConvertURLsToFeatures {
	
	public static void run(){
		try{
			System.out.println("Convert URls to features...");
			File testFile = new File("temp/URLAll_Test.arff");
			File trainFile = new File("temp/URLAll_Train.arff");
			File outTestFile = new File("temp/Test.arff");
			File outTrainFile = new File("temp/Train.arff");
			ArffLoader loader;
			ArffSaver saver;
			int cnt;
			loader = new ArffLoader();
			loader.setFile(trainFile);
			Instances structure = loader.getStructure();
			//System.out.println(structure);
			
		    Tokenizer tokenizer = new NCharGramTokenizer();
		    String[] options = weka.core.Utils.splitOptions("-min 1 -max 0 -delimiters 0-9_\\W");	
		    //String[] options = weka.core.Utils.splitOptions("-min " + 1 + " -max " + 6 + " -delimiters 0-9_\\W  -exword");
		    //String[] options = weka.core.Utils.splitOptions("-min " + 3 + " -max " + 6 + " -delimiters 0-9_\\W");
		    tokenizer.setOptions(options);
		    
		    StringToWordVectorConverter converter = new StringToWordVectorConverter(structure, tokenizer, 10000);
		    
		    System.out.println("Build Dictionary");
		    cnt = 0;
			Instance ins = null;
			while ((ins = loader.getNextInstance(structure))!=null){
				cnt ++;
				if (cnt%100000 == 0){
					System.out.println(cnt);
				}
				converter.buildDictionary(ins);
			}
			
			//System.out.println(converter.wordMap.size());
			converter.determineDictionary();
			
			//System.out.println(converter.dictionary.size());
			
		    // Output train		
			saver = new ArffSaver();
			saver.setFile(outTrainFile);
			saver.setStructure(converter.getStructure());
			saver.writeBatch();
			//saver.setRetrieval(Saver.INCREMENTAL);
	
			loader = new ArffLoader();
			loader.setFile(trainFile);
			structure = loader.getStructure();
			
			MyWriter.setFile(outTrainFile.getPath(), true);
		    System.out.println("Convert Train");
		    cnt = 0;
			while ((ins = loader.getNextInstance(structure))!=null){
				cnt ++;
				if (cnt%100000 == 0){
					System.out.println(cnt);
				}
				Instance tmp = converter.convert(ins);
				MyWriter.writeln(tmp.toString());
				//saver.writeIncremental(tmp);
			}
			MyWriter.close();
	
		    // Output test	
			saver = new ArffSaver();
			saver.setFile(outTestFile);
			saver.setStructure(converter.getStructure());
			saver.writeBatch();
			//saver.setRetrieval(Saver.INCREMENTAL);
			
			loader = new ArffLoader();
			loader.setFile(testFile);
			structure = loader.getStructure();
		
			MyWriter.setFile(outTestFile.getPath(), true);
		    System.out.println("Convert Test");
		    cnt = 0;
			while ((ins = loader.getNextInstance(structure))!=null){
				cnt ++;
				if (cnt%100000 == 0){
					System.out.println(cnt);
				}
				Instance tmp = converter.convert(ins);
				MyWriter.writeln(tmp.toString());
//				saver.writeIncremental(converter.convert(ins));
			}	
			MyWriter.close();
		}
		catch(Exception e){
			System.err.println(e.getStackTrace());
		}
	}
}
