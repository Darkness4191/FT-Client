package de.dragon.FTClient.listeners;

import de.dragon.FTClient.ftpnet.Delete;
import de.dragon.FTClient.ftpnet.Download;
import de.dragon.FTClient.ftpnet.Parser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainListener implements ActionListener {

    private Parser parser;

    public MainListener(Parser parser) {
        this.parser = parser;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            switch (parser.getFrame().getTask()) {
                case download -> {
                    Download download = new Download(parser);
                    download.addFiles(parser.getFrame().getFtpChooser().getSelectedFiles());
                    parser.getFrame().getMasterQueue().send(download);
                }
                case delete -> {
                    int answer = JOptionPane.showConfirmDialog(parser.getFrame().getDropField(), "Do you really want to delete the selected files?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (answer == JOptionPane.YES_OPTION) {
                        Delete delete = new Delete(parser);
                        delete.addFiles(parser.getFrame().getFtpChooser().getSelectedFiles());
                        parser.getFrame().getMasterQueue().send(delete);
                    }
                }
            }
            parser.getFrame().getFtpChooser().setSelectedFile(new File(""));
            parser.getFrame().getFtpChooser().setSelectedFiles(new File[]{new File("")});
        } else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
            parser.getFrame().collectTrashandExit();
        }
    }
}
