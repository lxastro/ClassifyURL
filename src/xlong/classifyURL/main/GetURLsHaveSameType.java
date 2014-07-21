package xlong.classifyURL.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class to get URLs have the same type.
 * 
 * @author Xiang Long (longx13@mails.tinghua.edu.cn)
 */
public class GetURLsHaveSameType {
	/**
	 * Get URLs have same type.
	 * 
	 * @param combineFile
	 *            the path of combine file
	 * @param outDir
	 *            the path of the output file
	 * @throws IOException
	 */
	public static void getURLsHaveSameType(String combineFile, String outDir)
			throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(combineFile));
		File outdir = new File(outDir);
		if (!outdir.exists()) {
			outdir.mkdir();
		}
		Map<String, ArrayList<String>> typeMap = new HashMap<String, ArrayList<String>>();
		String urls;
		String types;
		while ((in.readLine()) != null) {
			urls = in.readLine();
			types = in.readLine();
			String[] ulist = urls.split(" ");
			String[] tlist = types.split(" ");
			for (String type : tlist) {
				if (!typeMap.containsKey(type)) {
					typeMap.put(type, new ArrayList<String>());
				}
				ArrayList<String> tmp = typeMap.get(type);
				for (String url : ulist) {
					tmp.add(url);
				}
			}
		}
		String key;
		ArrayList<String> values;
		int cnt = 0;
		for (Entry<String, ArrayList<String>> entry : typeMap.entrySet()) {
			cnt++;

			key = entry.getKey();
			values = entry.getValue();

			String outFile = outDir + "/" + cnt;
			// String[] part = key.split("/");
			// String last = part[part.length-1];
			// String outFile = outDir + "/" + cnt + "_" + last;

			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			out.write(key + "\n ------ \n");
			for (String value : values) {
				out.write(value + "\n");
			}
			out.close();
		}
		in.close();
	}

	public static void run() {
		// String outCombineFile = "results/combine.txt";
		// String urlsHaveSameTypeDir = "results/URLsHaveSameTypeOri";
		String outCombineFile = "results/combineAndFilter.txt";
		String urlsHaveSameTypeDir = "results/URLsHaveSameType";
		try {
			System.out.println("Get URLs have same type...");
			getURLsHaveSameType(outCombineFile, urlsHaveSameTypeDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
