package xlong.classifyURL.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.stemmers.NullStemmer;
import weka.core.tokenizers.Tokenizer;
import xlong.classifyURL.util.MyWriter;

/**
 * Class to convert string to word vectore
 * 
 * @author Xiang Long (longx13@mails.tsinghua.edu.cn)
 */
public class StringToWordVectorConverter {
	private HashSet<Integer> selectedRange;
	private Tokenizer tokenizer;
	private NullStemmer stemmer;

	private Instances structure;
	private HashSet<String> stopwords;
	private boolean s_lowerCaseTokens = true;
	private boolean s_useStoplist = false;
    private boolean s_OutputCounts = false;
	private int s_wordsToKeep = 10000;
	private int s_minTermFreq = 1;
	private String s_prefix = "";

	private Instances outputFormat;
	private TreeMap<String, Integer> dictionary;
	private TreeMap<String, Count> wordMap;

	private class Count {
		int count = 0;

		public Count(int x) {
			count = x;
		}

		public void addOne() {
			count++;
		}
	}

	public Instances getStructure(){
		return outputFormat;
	}
	
	public StringToWordVectorConverter(Instances structure, Tokenizer tokenizer, int wordsToKeep) {
		this.wordMap = new TreeMap<String, Count>();
		this.tokenizer = tokenizer;
		stemmer = new NullStemmer();
		this.structure = structure;
		determineSelectedRange(structure);
		s_wordsToKeep = wordsToKeep;
	}

	private static void sortArray(int[] array) {

		int i, j, h, N = array.length - 1;

		for (h = 1; h <= N / 9; h = 3 * h + 1) {
			;
		}

		for (; h > 0; h /= 3) {
			for (i = h + 1; i <= N; i++) {
				int v = array[i];
				j = i;
				while (j > h && array[j - h] > v) {
					array[j] = array[j - h];
					j -= h;
				}
				array[j] = v;
			}
		}
	}

	private void determineSelectedRange(Instances structure) {
		selectedRange = new HashSet<Integer>();
		for (int j = 0; j < structure.numAttributes(); j++) {
			if (structure.attribute(j).type() == Attribute.STRING) {
				selectedRange.add(j);
			}
		}
	}

	public void determineDictionary() {
		// Figure out the minimum required word frequency
		int array[] = new int[wordMap.size()];
		Iterator<Count> it = wordMap.values().iterator();
		int pos = 0;
		int prune;
		int totalsize = wordMap.size();
		while (it.hasNext()) {
			Count count = it.next();
			array[pos] = count.count;
			pos++;
		}
		// sort the array
		sortArray(array);
		if (array.length < s_wordsToKeep) {
			// if there aren't enough words, set the threshold to minFreq
			prune = s_minTermFreq;
		} else {
			// otherwise set it to be at least minFreq
			prune = Math
					.max(s_minTermFreq, array[array.length - s_wordsToKeep]);
		}

		// Convert the wordMap into an attribute index
		// and create one attribute per word
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(totalsize
				+ structure.numAttributes());
		;

		// Add the non-converted attributes
		int classIndex = -1;
		for (int i = 0; i < structure.numAttributes(); i++) {
			if (!selectedRange.contains(i)) {
				if (structure.classIndex() == i) {
					classIndex = attributes.size();
				}
				attributes.add((Attribute) structure.attribute(i).copy());
			}
		}

		// Add the word vector attributes
		TreeMap<String, Integer> newDictionary = new TreeMap<String, Integer>();
		Iterator<Entry<String, Count>> ite = wordMap.entrySet().iterator();
		int index = attributes.size();
		while (ite.hasNext()) {
			Entry<String, Count> en = ite.next();
			String word = en.getKey();
			Count count = en.getValue();
			if (count.count >= prune) {
				if (newDictionary.get(word) == null) {
					newDictionary.put(word, new Integer(index++));
					attributes.add(new Attribute(s_prefix + word));
				}
			}
		}
		attributes.trimToSize();
		dictionary = newDictionary;

		// Set the filter's output format
		outputFormat = new Instances(structure.relationName(), attributes, 0);
		outputFormat.setClassIndex(classIndex);
		
		// Release memory
		wordMap = null;
	}

	public void buildDictionary(Instance instance) {

		for (int j = 0; j < instance.numAttributes(); j++) {
			if (selectedRange.contains(j) && (instance.isMissing(j) == false)) {

				tokenizer.tokenize(instance.stringValue(j));

				while (tokenizer.hasMoreElements()) {
					String word = ((String) tokenizer.nextElement()).intern();

					if (this.s_lowerCaseTokens == true) {
						word = word.toLowerCase();
					}

					word = stemmer.stem(word);

					if (this.s_useStoplist == true) {
						if (stopwords.contains(word)) {
							continue;
						}
					}

					if (!(wordMap.containsKey(word))) {
						wordMap.put(word, new Count(1));
					} else {
						wordMap.get(word).addOne();
					}

				}
			}
		}
	}

	public Instance convert(Instance instance) {
		// Convert the instance into a sorted set of indexes
		TreeMap<Integer, Double> contained = new TreeMap<Integer, Double>();

		// Copy all non-converted attributes from input to output
		int firstCopy = 0;
		for (int i = 0; i < structure.numAttributes(); i++) {
			if (!selectedRange.contains(i)) {
				if (structure.attribute(i).type() != Attribute.STRING
						&& structure.attribute(i).type() != Attribute.RELATIONAL) {
					// Add simple nominal and numeric attributes directly
					if (instance.value(i) != 0.0) {
						contained.put(new Integer(firstCopy), new Double(
								instance.value(i)));
					}
				} else {
					if (instance.isMissing(i)) {
						contained.put(new Integer(firstCopy),
								new Double(Utils.missingValue()));
					} else if (structure.attribute(i).type() == Attribute.STRING) {
						// If this is a string attribute, we have to first add
						// this value to the range of possible values, then add
						// its new internal index.
						if (outputFormat.attribute(firstCopy).numValues() == 0) {
							// Note that the first string value in a
							// SparseInstance doesn't get printed.
							outputFormat.attribute(firstCopy).addStringValue(
									"Hack to defeat SparseInstance bug");
						}
						int newIndex = outputFormat.attribute(firstCopy)
								.addStringValue(instance.stringValue(i));
						contained.put(new Integer(firstCopy), new Double(
								newIndex));
					} else {
						// relational
						if (outputFormat.attribute(firstCopy).numValues() == 0) {
							Instances relationalHeader = outputFormat
									.attribute(firstCopy).relation();

							// hack to defeat sparse instances bug
							outputFormat.attribute(firstCopy).addRelation(
									relationalHeader);
						}
						int newIndex = outputFormat.attribute(firstCopy)
								.addRelation(instance.relationalValue(i));
						contained.put(new Integer(firstCopy), new Double(
								newIndex));
					}
				}
				firstCopy++;
			}
		}

		for (int j = 0; j < instance.numAttributes(); j++) {
			// if ((getInputFormat().attribute(j).type() == Attribute.STRING)
			if (selectedRange.contains(j)
					&& (instance.isMissing(j) == false)) {

				tokenizer.tokenize(instance.stringValue(j));

				while (tokenizer.hasMoreElements()) {
					String word = tokenizer.nextElement();
					if (this.s_lowerCaseTokens == true) {
						word = word.toLowerCase();
					}
					word = stemmer.stem(word);
					Integer index = dictionary.get(word);
					if (index != null) {
						if (s_OutputCounts) { // Separate if here rather than
												// two lines down
												// to avoid hashtable lookup
							Double count = contained.get(index);
							if (count != null) {
								contained.put(index,
										new Double(count.doubleValue() + 1.0));
							} else {
								contained.put(index, new Double(1));
							}
						} else {
							contained.put(index, new Double(1));
						}
					}
				}
			}
		}
		
	    // Convert the set to structures needed to create a sparse instance.
	    double[] values = new double[contained.size()];
	    int[] indices = new int[contained.size()];
	    Iterator<Integer> it = contained.keySet().iterator();
	    for (int i = 0; it.hasNext(); i++) {
	      Integer index = it.next();
	      Double value = contained.get(index);
	      values[i] = value.doubleValue();
	      indices[i] = index.intValue();
	    }

	    Instance inst = new SparseInstance(instance.weight(), values, indices,
	      outputFormat.numAttributes());
	    inst.setDataset(outputFormat);
	    
		return inst;
	}
	
	public static void main(String[] args){
		try{
			File testFile = new File("results/URLAll_Test.arff");
			File trainFile = new File("URLAll_Train.arff");
			File outTestFile = new File("Test.arff");
			File outTrainFile = new File("Train.arff");
			ArffLoader loader;
			ArffSaver saver;
			int cnt;
			loader = new ArffLoader();
			loader.setFile(trainFile);
			Instances structure = loader.getStructure();
			//System.out.println(structure);
			
		    Tokenizer tokenizer = new NCharGramTokenizer();
		    //String[] options = weka.core.Utils.splitOptions("-min 1 -max 0 -delimiters 0-9_\\W");	
		    String[] options = weka.core.Utils.splitOptions("-min " + 3 + " -max " + 6 + " -delimiters 0-9_\\W");
		    tokenizer.setOptions(options);
		    
		    StringToWordVectorConverter converter = new StringToWordVectorConverter(structure, tokenizer, 20000);
		    
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
