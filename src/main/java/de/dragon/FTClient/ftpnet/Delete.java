package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.frame.Task;
import de.dragon.FTClient.frame.progressbar.ProgressBar;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
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

                int deleted = 0;
                parser.getAsyncParser().interrupt();
                ProgressBar bar = new ProgressBar(frame);
                for (int i = 0; i < selectedFiles.length; i++) {
                    try {
                        bar.updatePercent(i * 1D / selectedFiles.length, selectedFiles[i].getName());
                        deleted += delete(parser.getPathToFileOnServer(selectedFiles[i].getName()), selectedFiles[i].getName(), selectedFiles[i].isDirectory());
                    } catch (IOException ioException) {
                        parser.getAsyncParser().release();
                        frame.criticalError(ioException);
                        ioException.printStackTrace();
                        break;
                    }
                }
                bar.dispose();
                parser.getAsyncParser().release();

                JOptionPane.showMessageDialog(frame.getDropField(), String.format("Deleted %d files", deleted), "Info", JOptionPane.INFORMATION_MESSAGE);

                try {
                    frame.refreshView(false);
                } catch (IOException ioException) {
                    frame.criticalError(ioException);
                    ioException.printStackTrace();
                }


            }

            frame.getFtpChooser().setSelectedFile(new File(""));

        }
    }

    public int delete(String pathOnServer, String filename, boolean isDirectory) throws IOException {
        if(isDirectory) {
            int sum = 0;
            for(FTPFile file : parser.getConnector().getClient().listFiles(pathOnServer)) {
                sum += delete(pathOnServer + "/" + file.getName(), file.getName(), file.isDirectory());
            }
            parser.getConnector().getClient().removeDirectory(pathOnServer);
            return sum + 1;
        } else {
            parser.getConnector().getClient().deleteFile(pathOnServer);
            return 1;
        }
    }
}
