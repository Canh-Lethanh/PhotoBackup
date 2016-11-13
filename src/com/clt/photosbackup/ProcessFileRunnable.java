package com.clt.photosbackup;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProcessFileRunnable implements Runnable {

	Path file;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		processFile(file);
	}

	public ProcessFileRunnable(Path currFile)
	{
		file = currFile;
	}
	
	void processFile(Path currFile) {
		String date = ExifUtil.getDate(currFile);		
		
		String fileName = currFile.getFileName().toString();
		
		/**
		 * Si la date existe dans EXIF
		 */
		if (date != "") {
			System.out.println(currFile.toString() + " date=" + date);
			
			/**
			 * Determiner repertoire cible
			 */
			Path toCopyDir=null;
			try {
				toCopyDir = CheckFileUtil.getSubDirectory(PhotosBackup.backupRoot, date);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			/**
			 * Determiner fichier cible
			 */
			
			Path targetFile = CheckFileUtil.getTargetFile(toCopyDir, fileName);
			
			/**
			 * Tester si fichier existe dans cible
			 */
			if (CheckFileUtil.checkExists(targetFile)==true) {
				/**
				 * Fichier existe : verifier checksum
				 */
				System.out.println("Fichier meme nom existe:"+currFile.toString());
				
				long chkCurr = CheckFileUtil.getCRC32(currFile);
				long chkTarget = CheckFileUtil.getCRC32(targetFile);
				
				if (chkCurr!=chkTarget) {
					/**
					 * fichiers differents
					 */
					//String newFileName = fileName+"_"+chkCurr;
					String newFileName = CheckFileUtil.getNewChkFilename(fileName, chkCurr);
					
					Path newTargetFile = CheckFileUtil.getTargetFile(toCopyDir, newFileName);
					
					if (CheckFileUtil.checkExists(newTargetFile)==false) {						
						backupFile(toCopyDir, currFile, newFileName);
					}
					else {
						// Fichier meme contenu existe deja
						System.out.println("fichier meme contenu :"+newFileName+" -- "+currFile.toString());
						moveToTrash(currFile, fileName);
					}
				}
				else {
					// Fichier identique
					System.out.println("fichier identique:"+currFile.toString());
					moveToTrash(currFile, fileName);
				}
			}
			else {
				/**
				 * Fichier n'existe pas : recopier
				 */
				boolean status = backupFile(toCopyDir, currFile, fileName);
				if (status == true) {					
					moveToTrash(currFile, fileName);
				}
									
			}
		}
	}
	
	boolean backupFile(Path toCopyDir, Path file, String targetFileName)
	{
		boolean result = false;
		Path targetPath = CheckFileUtil.getFilePath(toCopyDir, targetFileName);
		try {
			Files.copy(file, targetPath);
			System.out.println("Fichier copié:"+file.toString());
			result = true;
		} catch (IOException e) {
			e.printStackTrace();			
		}
		
		return result;
	}
	
	void moveToTrash(Path currFile, String targetFileName)
	{
		try {
			Path rel = PhotosBackup.photosDir.relativize(currFile);
			Path target = PhotosBackup.trashDir.resolve(rel);

			if (Files.exists(target.getParent(),  LinkOption.NOFOLLOW_LINKS)==false) {			
				Files.createDirectories(target.getParent());
			}
			
			Files.copy(currFile, target);
						
			Files.delete(currFile);			
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}
}
