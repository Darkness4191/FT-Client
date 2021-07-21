package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class UploadFileOption extends FMenuItem {

    private FTPFrame frame;

    public UploadFileOption(FTPFrame frame) {
        super();
        this.frame = frame;
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
                for (File f : chooser.getSelectedFiles()) {
                    try {
                        frame.getUpload().upload(f);
                        frame.refreshView(false);
                    } catch (IOException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                JOptionPane.showMessageDialog(frame.getDropField(), String.format("Upload of %d files successful", chooser.getSelectedFiles().length), "Info", JOptionPane.INFORMATION_MESSAGE);

            }

        }
    }
}
