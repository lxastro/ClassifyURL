package xlong.classifyURL.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MyWriter {
	private static BufferedWriter curBufWriter= null;
	
	public static boolean setFile(File aFile, boolean append){
		if (curBufWriter != null){
			close();
		}
		if (aFile.isDirectory()) {
			return false;
		}
		aFile = aFile.getAbsoluteFile();
		File parentDir = new File(aFile.getParent());
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		try {
			curBufWriter = new BufferedWriter(
								new OutputStreamWriter(
										new FileOutputStream(aFile, append)));
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
		}
		return true;
	}
	
	public static boolean setFile(String path, boolean append){
		File aFile = new File(path);
		return setFile(aFile, append);
	}

	public static void close(){
		try{
			curBufWriter.close();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void write(String str){
		if (curBufWriter == null){
			System.err.println("Current BufferWriter not define.");
		}
		try{
			curBufWriter.write(str);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void writeln(String str){
		write(str+"\n");
	}
}
