package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.ftpnet.Parser;
import de.dragon.FTClient.ftpnet.Upload;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class UploadFileOption extends FMenuItem {

    private FTPFrame frame;
    private Parser parser;

    public UploadFileOption(FTPFrame frame, Parser parser) {
        super();
        this.frame = frame;
        this.parser = parser;
        this.setText("Upload");
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (frame.isInit()) {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int r = chooser.showDialog(null, "Upload");

            if (r == JFileChooser.APPROVE_OPTION) {
                try {
                    Upload upload = new Upload(parser);
                    upload.addFiles(chooser.getSelectedFiles());
                    frame.getMasterQueue().send(upload);
                    frame.refreshView(false);
                } catch (IOException interruptedException) {
                    interruptedException.printStackTrace();
                }
                JOptionPane.showMessageDialog(frame.getDropField(), String.format("Upload of %d files successful", chooser.getSelectedFiles().length), "Info", JOptionPane.INFORMATION_MESSAGE);

            }

        }
    }
}
