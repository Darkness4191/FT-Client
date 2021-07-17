package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.UserApproveDownload;
import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.frame.Task;
import de.dragon.UsefulThings.misc.DebugPrinter;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ApproveActions implements ActionListener {

    private Parser parser;
    private String download_dir;
    private FTPFrame frame;
    private Connector connector;

    private boolean always_approve = false;

    public ApproveActions(Parser parser) {
        this.parser = parser;
        this.frame = parser.getFrame();
        this.connector = parser.getConnector();

        File exi = new File(System.getProperty("user.home") + File.separator + "FTPDownloads" + File.separator + frame.token);
        if(!exi.exists()) {
            exi.mkdirs();
        }

        download_dir = exi.getAbsolutePath();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {

            if(parser.getFrame().getTask() == Task.download) {
                File[] selectedFiles = frame.getFtpChooser().getSelectedFiles();
                frame.getFtpChooser().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                int passed = 0;
                int failed = 0;

                for(int i = 0; i < selectedFiles.length; i++) {
                    try {
                        if(download(parser.parseFTPFileBack(selectedFiles[i]), download_dir)) {
                            DebugPrinter.println("Download successful " + download_dir + File.separator + selectedFiles[i].getName());
                            passed++;
                        } else {
                            DebugPrinter.println("Download failed " + selectedFiles[i].getName());
                            failed++;
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }

                frame.getFtpChooser().setCursor(null);

                JOptionPane.showMessageDialog(null, String.format("Successful: %d, Failed: %d. Saved file to %s", passed, failed, download_dir), "Info", JOptionPane.INFORMATION_MESSAGE);
            } else if(parser.getFrame().getTask() == Task.delete) {
                File[] selectedFiles = frame.getFtpChooser().getSelectedFiles();

                int answer = JOptionPane.showConfirmDialog(null, "Do you really want to delete the selected files?", "Confirm", JOptionPane.YES_NO_OPTION);
                if(answer == JOptionPane.YES_OPTION) {

                    frame.getFtpChooser().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    for(int i = 0; i < selectedFiles.length; i++) {
                        try {
                            parser.getConnector().getClient().deleteFile(parser.getConnector().getClient().printWorkingDirectory() + "/" + selectedFiles[i].getName());
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }

                    frame.getFtpChooser().setCursor(null);

                    JOptionPane.showMessageDialog(null, String.format("Deleted %d files", selectedFiles.length), "Info", JOptionPane.INFORMATION_MESSAGE);
                    try {
                        frame.refreshView();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    frame.setTask(Task.download);
                }
            }

            frame.getFtpChooser().setSelectedFile(new File(""));

        } else if(e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
            frame.collectTrashandExit();
        }
    }

    public boolean download(FTPFile file, String downloadFolder) throws IOException {
        if(!always_approve) {
            int a = UserApproveDownload.ask();

            if(a == JOptionPane.NO_OPTION){
                always_approve = true;
            }

            if(a == JOptionPane.OK_OPTION || a == JOptionPane.NO_OPTION) {
                FileOutputStream download = new FileOutputStream(new File(download_dir + File.separator + file.getName()));
                connector.getClient().retrieveFile(file.getName(), download);

                download.close();
                return true;
            } else {
                return false;
            }
        } else {
            FileOutputStream download = new FileOutputStream(new File(download_dir + File.separator + file.getName()));
            connector.getClient().retrieveFile(file.getName(), download);

            download.close();
            return true;
        }
    }
}
