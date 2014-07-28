package xlong.classifyURL.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;


/**
 * This is a utility class for managing properties.
 * 
 * @author Xiang Long (longx13@mails.tsinghua.edu.cn)
 *
 */
public class PropertiesUtil {
	
	private static final String propertiesFile = "info.properties";
	private static Properties properties = null;
    
    public static void ListAllProperties(String filePath) throws IOException {
        Properties pps = new Properties();
        
        BufferedInputStream in = 
        		new BufferedInputStream(
        				new FileInputStream(filePath));
        pps.load(in);
        in.close();
        Enumeration<?> en = pps.propertyNames();
        
        while(en.hasMoreElements()) {
            String strKey = (String) en.nextElement();
            String strValue = pps.getProperty(strKey);
            System.out.println(strKey + "=" + strValue);
        }
        
    }
    
    public static boolean loadProperties() {
    	Properties pps = new Properties();
        try {
        	BufferedInputStream in = 
        			new BufferedInputStream (
        					new FileInputStream(propertiesFile));  
            pps.load(in);
            in.close();
            properties = pps;
            return true;
            
        }catch (IOException e) {
        	System.out.println("Can't find default properties file.");
            return false;
        }  	
    }
    
    public static String getProperty(String key) {
    	if (properties == null) {
    		return null;
    	} else {
    		return properties.getProperty(key);
    	}
    }
    

    public static void WriteDefaultProperties() throws IOException {
    	
    	System.out.println("Create default properties file.");
    	
        Properties pps = new Properties();
        
        BufferedOutputStream out =
        		new BufferedOutputStream(
        				new FileOutputStream(propertiesFile));
        
        pps.setProperty(
        		"DBpedia_external_links.nt",
        		"D:\\longx\\data\\external_links_en.nt");
        pps.setProperty(
        		"DBpedia_instance_types.nt", 
        		"D:\\longx\\data\\instance_types_en.nt");
        pps.setProperty(
        		"DBpedia_ontology.owl", 
        		"D:\\longx\\data\\dbpedia_3.9.owl");
        
        pps.store(out, "Default properties.");
        out.close();
    }
    
    public static void main(String [] args) throws IOException{
    	WriteDefaultProperties();
    	ListAllProperties(propertiesFile);
    	loadProperties();
    	System.out.println(getProperty("test1"));
    }

}
