/**
 * Project : Classify URLs
 */
package xlong.classifyURL.main;



public class Run {

	public static void main(String[] args) throws Exception {
		Init.run();
		CombineURLsTypes.run();
		GetURLsHaveSameType.run();
		GetOriginalArff.run();
		ConvertURLsToFeatures.run();
		UpdateClassifyURLs.run();
	}

}
