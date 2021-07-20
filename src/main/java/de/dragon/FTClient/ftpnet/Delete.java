package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.frame.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Delete implements ActionListener {

    private Parser parser;
    private FTPFrame frame;
    private Connector connector;

    public Delete(Parser parser) {
        this.parser = parser;
        this.frame = parser.getFrame();
        this.connector = parser.getConnector();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION) && parser.getFrame().getTask() == Task.delete) {
            File[] selectedFiles = frame.getFtpChooser().getSelectedFiles();

            int answer = JOptionPane.showConfirmDialog(null, "Do you really want to delete the selected files?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {

                frame.getFtpChooser().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                for (int i = 0; i < selectedFiles.length; i++) {
                    try {
                        parser.getAsyncParser().waitForRelease();
                        parser.getConnector().getClient().deleteFile(parser.getPathToFileOnServer(selectedFiles[i].getName()));
                        parser.getAsyncParser().interuptComplete();
                    } catch (IOException ioException) {
                        frame.criticalError(ioException);
                        ioException.printStackTrace();
                    }
                }

                frame.getFtpChooser().setCursor(null);

                JOptionPane.showMessageDialog(frame.getDropField(), String.format("Deleted %d files", selectedFiles.length), "Info", JOptionPane.INFORMATION_MESSAGE);

                try {
                    frame.refreshView(false);
                } catch (IOException ioException) {
                    frame.criticalError(ioException);
                    ioException.printStackTrace();
                }


            }

            frame.getFtpChooser().setSelectedFile(new File(""));

        } else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
            frame.collectTrashandExit();
        }
    }
}
