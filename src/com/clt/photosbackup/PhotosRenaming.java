package com.clt.photosbackup;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PhotosRenaming {

	public static String backupDir;
	public static String trashDir;
	
	public static void main(String[] args) {
		Properties props = System.getProperties();		
				
		backupDir = props.getProperty("backupDir");
		trashDir  = props.getProperty("trashDir");
		
		startRenaming();
	}

	static public void startRenaming()
	{
		SynchronousQueue<Runnable> queue = new SynchronousQueue<Runnable>();
		ThreadPoolExecutor thrPool = new ThreadPoolExecutor(20, 50, 10, TimeUnit.SECONDS, queue);
		
		/**
		 * Extraire liste des fichiers à copier
		 */
		ExtractFilesList extFiles = new ExtractFilesList(backupDir);
		
		int nb=0;
		ArrayList<Path> listPaths = extFiles.getFilesList();
		int total = listPaths.size();
		for (Path currFile : listPaths) {
			
			String fileName = currFile.getFileName().toString();
			
			String f2 = CheckFileUtil.correctBadChkFileName(fileName);
			
			if (fileName.equals(f2)==false) {
				System.out.println("correct file:"+currFile.toString());
				System.out.println("result:"+f2);
			}
			
			
			nb++;
			
			if (nb%100==0) {
				System.out.println("**************  Processed:"+nb+ "total="+total);
			}
		}
		
		System.out.println("********************   fin listing  **********************");
		
		thrPool.shutdown();
	}
}
