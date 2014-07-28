/**
 * 
 */
package xlong.classifyURL.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import xlong.classifyURL.util.PropertiesUtil;


/**
 * @author Xiang Long (longx13@mails.tsinghua.edu.cn)
 *
 */
public class Init {

	public static void run() throws IOException {
		
		// load properties
		if (!PropertiesUtil.loadProperties()) {
			PropertiesUtil.WriteDefaultProperties();
			PropertiesUtil.loadProperties();
		}
		
		// create temporary and result directories
		Path temp = Paths.get("temp");
		Path result = Paths.get("result");
		Files.createDirectories(temp);
		Files.createDirectories(result);
		
	}

}
