package com.app.server.test;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 不能处理中文文件名
 */
public class Unzip 
{
	
	/**
     * Uncompress the incoming zip file.
     * @param inFileName Name of the file to be uncompressed
     * @param inFileName Name of the file to be uncompressed

     */
    static boolean unZip(String inFileName,String outFileDir) {
         
            if (!(inFileName.endsWith("war")||inFileName.endsWith("jar"))) {
                System.err.println("File name must have extension of \".jar or .war\"");
                System.exit(1);
            }
 
            //System.out.println("Opening the compressed file.");
            ZipInputStream myZipInputStream = null;
          
            try {
                myZipInputStream = new ZipInputStream(new FileInputStream(inFileName));
            } catch(FileNotFoundException e) {
                System.err.println("File not found. " + inFileName);
                return false;
            }
 
           

        
 
           // System.out.println("Transfering bytes from compressed file to the output file.");
            
            ZipEntry myZipEntry = null;  
            File myOutFile=null;  
            BufferedInputStream bufferdImputStream=new BufferedInputStream(myZipInputStream);  
            try {  
                while((myZipEntry = myZipInputStream.getNextEntry())!=null ){
                	if(myZipEntry.isDirectory()){
                		new File(outFileDir,myZipEntry.getName()).mkdir();
                		continue;
                	}
                    myOutFile=new File(outFileDir,myZipEntry.getName());  
                    if(!myOutFile.exists()){  
                        (new File(myOutFile.getParent())).mkdirs();  
                    }  
                    FileOutputStream tmpFileOutStream= null;
                    try {
                    	   tmpFileOutStream = new FileOutputStream(myOutFile);
                    } catch (FileNotFoundException e) {
                        System.err.println("Could not write to file. " + myOutFile.getAbsolutePath());
                       return false;
                    }
                    BufferedOutputStream tmpBufferedOupputStream=new BufferedOutputStream(tmpFileOutStream);  
                    int b;  
                    while((b=bufferdImputStream.read())!=-1){  
                        tmpBufferedOupputStream.write(b);  
                    }  
                    tmpBufferedOupputStream.close();  
                    tmpFileOutStream.close();  
                   // System.out.println(Fout+"解压成功");      
                }  
                bufferdImputStream.close();  
                myZipInputStream.close(); 
                return true;
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
                return false;
            }  

    }
}
