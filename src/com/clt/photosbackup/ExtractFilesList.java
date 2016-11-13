package com.clt.photosbackup;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ExtractFilesList {
	
	ArrayList<Path> filesList;
	String rootPath;
	
	public ExtractFilesList(String _rootPath) {
		rootPath = _rootPath;
	}
	
	void extractFiles(ArrayList<Path> _filesList) {
		Path dir = Paths.get(rootPath);		
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
		    for (Path file: stream) {
		    	if (Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS)) {
		    		ExtractFilesList extFiles = new ExtractFilesList(rootPath+"/"+file.getFileName());
		    		extFiles.extractFiles( _filesList);
		    	}
		    	else {
		    		_filesList.add(file);
		    		//System.out.println(file.toAbsolutePath().toString());
		    		System.out.println("fileName="+file.getFileName().toString());
		    	}
		    }
		} catch (IOException | DirectoryIteratorException x) {
		    // IOException can never be thrown by the iteration.
		    // In this snippet, it can only be thrown by newDirectoryStream.
		    System.err.println(x);
		}
	}

	public ArrayList<Path> getFilesList() {
		filesList = new ArrayList<Path>();
		
		extractFiles(filesList);
		
		return filesList;
	}
}
