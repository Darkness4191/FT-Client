package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class UploadFileOption extends JMenuItem implements ActionListener {

    private FTPFrame frame;

    public UploadFileOption(FTPFrame frame) {
        super();
        this.frame = frame;
        this.setText("Upload");
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(frame.isInit()) {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int r = chooser.showDialog(null, "Upload");

            if(r == JFileChooser.APPROVE_OPTION) {
                for(File f : chooser.getSelectedFiles()) {
                    frame.getUpload().addToQueue(f);
                }
                JOptionPane.showMessageDialog(null, String.format("Upload of %d files successful", chooser.getSelectedFiles().length), "Info", JOptionPane.INFORMATION_MESSAGE);

            }

        } else {
            frame.printToConsole("Error: Upload not possible", Color.RED);
        }
    }
}
