package com.clt.pbkp.gui;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class PbkpController {
	@FXML private TextField nasText;
	
	@FXML protected void handleBckFolderBtnAction(ActionEvent event) {
        System.out.println("handleBckFolderBtnAction");
        DirectoryChooser fileCh = new DirectoryChooser();
        
        File file = fileCh.showDialog(null);
        nasText.setText(file.getPath());
    }
}
