package de.dragon.FTClient.listeners;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class FileDisplay implements PropertyChangeListener {

    private JFileChooser chooser;
    private JTextField textField;

    public FileDisplay(JFileChooser fileChooser, JTextField textField) {
        this.chooser = fileChooser;
        this.textField = textField;
        fileChooser.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("SelectedFileChangedProperty") || evt.getPropertyName().equals("SelectedFilesChangedProperty")) {
            textField.setText("");
            File[] filelist = chooser.getSelectedFiles();
            for(File f : filelist) {
                if(filelist.length == 1 && !f.getName().equals("^^^")) {
                    textField.setText(f.getName());
                } else if(!f.getName().equals("^^^")) {
                    textField.setText(textField.getText() + " \"" + f.getName() + "\"");
                }
            }
        }
    }
}
