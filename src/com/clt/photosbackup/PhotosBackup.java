package com.clt.photosbackup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import sun.swing.BakedArrayList;


public class PhotosBackup {
	
	public static Path photosDir;
	public static Path backupRoot;
	public static Path trashDir;
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		Properties props = System.getProperties();
		//props.load(new FileInputStream("backup.properties"));
		
		
		String photosDir = props.getProperty("photosDir");
		String backupDir = props.getProperty("backupDir");
		String trashDir  = props.getProperty("trashDir");
		
		PhotosBackup bkp = new PhotosBackup(photosDir, backupDir, trashDir);		
		bkp.startBackup();
		
	}
	
	 
	public PhotosBackup(String _photosDir, String _backupDir, String _trashDir)
	{
		photosDir = Paths.get(_photosDir);
		backupRoot = Paths.get(_backupDir);
		trashDir = Paths.get(_trashDir);		
	}
	
	public void startBackup()
	{
		SynchronousQueue<Runnable> queue = new SynchronousQueue<Runnable>();
		ThreadPoolExecutor thrPool = new ThreadPoolExecutor(20, 50, 10, TimeUnit.SECONDS, queue);
		
		/**
		 * Extraire liste des fichiers à copier
		 */
		ExtractFilesList extFiles = new ExtractFilesList(photosDir.toString());
		
		int nb=0;
		ArrayList<Path> listPaths = extFiles.getFilesList();
		int total = listPaths.size();
		for (Path currFile : listPaths) {
			String date = ExifUtil.getDate(currFile);
			if (date==null) {
				continue;
			}
			
			while (queue.size()>=10) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("execute :"+currFile.toString());
			
			boolean submit = false;
			do {				
				try {
						thrPool.execute(new ProcessFileRunnable(currFile));
						submit = true;
				}
				catch (Exception e) {
					//e.printStackTrace();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
				}
			}
			while (submit==false);
			
			nb++;
			
			if (nb%100==0) {
				System.out.println("**************  Processed:"+nb+ "total="+total);
			}
		}
		
		System.out.println("********************   fin listing  **********************");
		
		thrPool.shutdown();
	}
		
	
	
	
}
