package de.dragon.FTClient.listeners;

import de.dragon.FTClient.ftpnet.Delete;
import de.dragon.FTClient.ftpnet.Download;
import de.dragon.FTClient.ftpnet.Parser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileChooserListener implements ActionListener {

    private Parser parser;

    public FileChooserListener(Parser parser) {
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
                    Delete delete = new Delete(parser);
                    delete.addFiles(parser.getFrame().getFtpChooser().getSelectedFiles());
                    parser.getFrame().getMasterQueue().send(delete);
                }
            }
        } else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
            parser.getFrame().collectTrashandExit();
        }
    }
}
