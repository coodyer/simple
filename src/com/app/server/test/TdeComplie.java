package com.app.server.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

public class TdeComplie {

	private static void doDeComplie(File classFile){
		try {
			InputStream clsFIps = new FileInputStream(classFile);
			ClassReader cr = new ClassReader(clsFIps);
			File deFile = new File(classFile.getCanonicalPath()+".txt");
			TraceClassVisitor cv = new TraceClassVisitor(null, new PrintWriter(deFile,"UTF-8"));
			cr.accept(cv, 0);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(RuntimeException e){
			// TODO Auto-generated catch block
						e.printStackTrace();
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public static void main(String[] args) {
		
		
		//String fileName = args[0]; 
		String fileName = "D:/WorkSpace/LiveServer/target/classes/lianai/app/guide/cmd/CmdGuideHandle.class";
		if(fileName.endsWith(".class")){
			doDeComplie(new File(fileName));
		}else if(fileName.endsWith("jar")||fileName.endsWith("war")){
			String outputDir = fileName.replace(".jar", "").replace(".war", "");
			Unzip.unZip(fileName,outputDir);
		    doDeComplieAllFiles(outputDir);	
		}
		
		System.out.println("DeComplied done.");

		
		

	}

	private static void doDeComplieAllFiles(String outputDir) {
	   
		File rootFile = new File(outputDir);
	    File[] tmpFiles = rootFile.listFiles();
	    for(File tmpFile:tmpFiles){
		if(tmpFile.isDirectory()){
			try {
				doDeComplieAllFiles(tmpFile.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(tmpFile.getName().endsWith(".class")){
			doDeComplie(tmpFile);
		}
	}
	}
}
