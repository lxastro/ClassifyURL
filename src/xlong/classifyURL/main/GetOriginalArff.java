package xlong.classifyURL.main;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import xlong.classifyURL.converter.FilesToInstances;

public class GetOriginalArff {
	public static void getOriginalArff(int nAll) throws Exception{
		Instances data; 
		
		String dir = "results/URLsHaveSameType/";

		String[] allFiles = new String[nAll];
		for (int i=0;i<nAll;i++){
			allFiles[i] = dir + (i+1);
		}
		data = FilesToInstances.createInstances("URLClasses", allFiles);	
		//DataSink.write("results/URLAll_Ori.arff", data);
		
		// Randomize
		data.randomize(new java.util.Random());
		
		// Percentage split
		int percent = 66;
		int trainSize = (int) Math.round(data.numInstances() * percent/ 100);
		int testSize = data.numInstances() - trainSize;
		Instances train = new Instances(data, 0, trainSize);
		Instances test = new Instances(data, trainSize, testSize);
		DataSink.write("results/URLAll_Train.arff", train);
		DataSink.write("results/URLAll_Test.arff", test);
	}
	
	public static void getOriginalArff() throws Exception{
		getOriginalArff(337);
	}
	
	public static void run() throws Exception {
		System.out.println("Get Orignal Arff...");
		getOriginalArff();

	}

}
