/**
 * Project : Classify URLs
 */
package xlong.classifyURL.main;
import java.io.File;

import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.bayes.NaiveBayesMultinomialUpdateable;
//import weka.classifiers.meta.MultiClassClassifierUpdateable;
//import weka.classifiers.trees.HoeffdingTree;
//import weka.classifiers.functions.SGD;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import xlong.classifyURL.util.MyWriter;


public class UpdateClassifyURLs {

	public static void run() {
		Instances structure = null;

		try {
			System.out.println("Classify...");
			File trainFile = new File("results/Train.arff");
			File testFile = new File("results/Test.arff");
			ArffLoader loader;
			Instance current;
			int cnt;

			NaiveBayesMultinomialUpdateable cfs = new NaiveBayesMultinomialUpdateable();
			//HoeffdingTree cfs = new HoeffdingTree();
			//MultiClassClassifierUpdateable cfs = new MultiClassClassifierUpdateable();
			UpdateableClassifier upcfs = cfs;
			
			System.out.println("Training...");
			loader = new ArffLoader();
			loader.setFile(trainFile);
			structure = loader.getStructure();
			structure.setClassIndex(0);
			
			cfs.buildClassifier(structure);
			for (int time = 0; time < 1; time++){
			    cnt = 0;
				while ((current = loader.getNextInstance(structure))!=null){
					cnt ++;
					if (cnt%1000 == 0){
						System.out.println(cnt);
					}
					upcfs.updateClassifier(current);
				}
			}

			System.out.println("Testing...");
			Evaluation testingEvaluation = new Evaluation(structure);
			loader = new ArffLoader();
			loader.setFile(testFile);
			structure = loader.getStructure();
			structure.setClassIndex(0);
			for (int time = 0; time < 1; time++){
			    cnt = 0;
				while ((current = loader.getNextInstance(structure))!=null){
					cnt ++;
					if (cnt%100000 == 0){
						System.out.println(cnt);
					}
					testingEvaluation.evaluateModelOnce(cfs, current);
					//testingEvaluation.evaluateModelOnceAndRecordPrediction(cfs, current);
				}
			}

			// Print result
			MyWriter.setFile("results/result.txt", false);
			MyWriter.writeln(testingEvaluation.toSummaryString());
			MyWriter.writeln(testingEvaluation.toClassDetailsString());
			MyWriter.close();
			MyWriter.setFile("results/matrix.txt", false);
			MyWriter.writeln(testingEvaluation.toMatrixString());
			MyWriter.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
