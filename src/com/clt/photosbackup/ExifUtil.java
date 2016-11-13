package com.clt.photosbackup;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class ExifUtil {

	static public String getDate(Path path) 
	{		
		File file = new File(path.toUri());
		String result="";
		Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(file);
        } catch (Exception e) {
        	System.out.println("erreur:"+file.toString());
            //e.printStackTrace(System.err);
            //System.exit(1);
        	return null;
        }
        
        // iterate over the metadata and print to System.out
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                String tagName = tag.getTagName();
                String directoryName = directory.getName();
                String description = tag.getDescription();

                // truncate the description if it's too long
                if (description != null && description.length() > 1024) {
                    description = description.substring(0, 1024) + "...";
                }

                if (tagName.equalsIgnoreCase("Date/Time"))
                {
                	result = description.substring(0, 10);
                	result = result.replace(":", "_");
                	break;
                }
                {
                    //System.out.printf("[%s] %s = %s%n", directoryName, tagName, description);
                }
            }
        }
        
        return result;
	}
}
