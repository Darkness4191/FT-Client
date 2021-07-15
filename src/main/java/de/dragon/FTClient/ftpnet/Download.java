package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.ApproveDownload;
import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.info.ProgressView;
import de.dragon.UsefulThings.events.ProgressChangeEvent;
import de.dragon.UsefulThings.events.listeners.ProgressListener;
import de.dragon.UsefulThings.misc.DebugPrinter;
import de.dragon.UsefulThings.net.Pipe;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Download implements ActionListener {

    private Parser parser;
    private String download_dir;
    private FTPFrame frame;
    private Connector connector;

    private boolean always_approve = false;

    public Download(Parser parser) {
        this.parser = parser;
        this.frame = parser.getFrame();
        this.connector = parser.getConnector();

        File exi = new File(System.getProperty("user.home") + File.separator + "FTPDownloads");
        if(!exi.exists()) {
            exi.mkdirs();
        }

        download_dir = exi.getAbsolutePath();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DebugPrinter.println("Action performed: " + e.getActionCommand());

        if(e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {

            File[] selectedFiles = frame.getFtpChooser().getSelectedFiles();
            frame.getFtpChooser().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            for(int i = 0; i < selectedFiles.length; i++) {
                try {
                    if(download(parser.parseFTPFileBack(selectedFiles[i]), download_dir)) {
                        DebugPrinter.println("Download successful " + download_dir + File.separator + selectedFiles[i].getName());
                    } else {
                        DebugPrinter.println("Download failed " + selectedFiles[i].getName());
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

            frame.getFtpChooser().setCursor(null);

        } else if(e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
            frame.collectTrashandExit();
        }
    }

    public boolean download(FTPFile file, String downloadFolder) throws IOException {
        if(!always_approve) {
            int a = ApproveDownload.ask();

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
