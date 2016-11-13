package com.clt.photosbackup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.StringTokenizer;
import java.util.zip.CheckedInputStream;
import java.util.zip.CRC32;

import com.sun.xml.internal.ws.api.pipe.NextAction;

public class CheckFileUtil {
	
	public static long getCRC32(Path file) {
		
		long checksum=0;
		try {

            CheckedInputStream cis = null;
            long fileSize = 0;
            try {
                // Computer CRC32 checksum
                cis = new CheckedInputStream(
                        new FileInputStream(file.toString()), new CRC32());               
                
            } catch (FileNotFoundException e) {
                System.err.println("File not found.");
                System.exit(1);
            }

            byte[] buf = new byte[128];
            while(cis.read(buf) >= 0) {
            }

            checksum = cis.getChecksum().getValue();
            //System.out.println(checksum + " " + fileSize + " " + fileName);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
		
		return checksum;
	}
	
	static public Path getSubDirectory(Path root, String subdir) throws IOException
	{
		String p = root.toAbsolutePath().toString()+
				root.getFileSystem().getSeparator()+
				subdir;
		Path result = Paths.get(p,null);
		
		if (Files.exists(result, LinkOption.NOFOLLOW_LINKS)==false) {
			try {
				Files.createDirectory(result);
			}
			catch (FileAlreadyExistsException fe) {
				System.out.println("file already exists:"+p);
			}
		}
		
		return result;
	}
	
	static public Path getFilePath(Path dir, String fileName) 
	{
		String p = dir.toAbsolutePath().toString()+
				dir.getFileSystem().getSeparator()+
				fileName;
		Path filePath = Paths.get(p);
		
		return filePath;
				
	}
	
	static public Path getTargetFile(Path dir, String fileName)
	{
		Path filePath = getFilePath(dir, fileName);
		
		return filePath;
	}
	
	static public boolean checkExists(Path dir, String fileName) 
	{		
		Path filePath = getFilePath(dir, fileName);
		
		if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)==false) {			
			return false;
		}
		else 
			return true;
	}
	
	static public boolean checkExists(Path file)
	{
		if (Files.exists(file, LinkOption.NOFOLLOW_LINKS)==false) {			
			return false;
		}
		else 
			return true;
	}
	
	static public int getExtensionIndex(String fileName) {
		int idx = -1;
		int nextId;
		do {
			nextId = fileName.indexOf(".", idx+1);
			if (nextId>idx) {
				idx=nextId;
			}
		} while (nextId>-1);
		
		return idx;
	}
	
	static public String getNewChkFilename(String fileName, long chk) {
		String result;
		
		int idx = getExtensionIndex(fileName);
		
		if (idx>0 && idx<fileName.length()) {
			String before = fileName.substring(0, idx);
			String after = fileName.substring(idx+1, fileName.length());
			
			result = before+"_"+chk+"."+after;
		}
		else {
			result = fileName+"_"+chk;
		}
		
		return result;
	}
	
	static public String correctBadChkFileName(String fileName) 
	{
		String result = fileName;
		int idx = getExtensionIndex(fileName);
		
		if (idx>0 && idx<fileName.length()) {
			String before = fileName.substring(0, idx);
			String after = fileName.substring(idx+1, fileName.length());
			
			if (after.contains("_")) {
				int id = after.indexOf("_");
				
				String ext = after.substring(0, id);
				String chk = after.substring(id+1, after.length());
				
				result = before+"_"+chk+"."+ext;
			}
		}
		
		return result;
	}
}
