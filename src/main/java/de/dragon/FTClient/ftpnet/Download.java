package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.frame.Task;
import de.dragon.FTClient.frame.UserApproveDownload;
import de.dragon.FTClient.frame.progressbar.ProgressBar;
import de.dragon.UsefulThings.misc.DebugPrinter;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

        File exi = new File(System.getProperty("user.home") + File.separator + "FTPDownloads" + File.separator + frame.token);
        if (!exi.exists()) {
            exi.mkdirs();
        }

        download_dir = exi.getAbsolutePath();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION) && parser.getFrame().getTask() == Task.download) {

            File[] selectedFiles = frame.getFtpChooser().getSelectedFiles();
            frame.getFtpChooser().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            int passed = 0;
            int failed = 0;

            ProgressBar progressBar = new ProgressBar(frame.getFrame());

            for (int i = 0; i < selectedFiles.length; i++) {
                try {
                    if (!selectedFiles[i].getName().equals("^^^") && confirmDownload(selectedFiles[i], download_dir)) {
                        DebugPrinter.println("Download successful " + download_dir + File.separator + selectedFiles[i].getName());
                        passed++;
                    } else {
                        DebugPrinter.println("Download failed " + selectedFiles[i].getName());
                        failed++;
                    }
                } catch (IOException ioException) {
                    frame.criticalError(ioException);
                    ioException.printStackTrace();
                }
                progressBar.updatePercent((i + 1) * 1D / selectedFiles.length, selectedFiles[i].getName());
            }

            if(passed > 0) {
                JOptionPane.showMessageDialog(frame.getDropField(), String.format("Successful: %d, Failed: %d. Saved file to %s", passed, failed, download_dir), "Info", JOptionPane.INFORMATION_MESSAGE);
            }

            frame.getFtpChooser().setCursor(null);
            frame.getFtpChooser().setSelectedFile(new File(""));
            progressBar.dispose();

        } else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
            frame.collectTrashandExit();
        }
    }

    public boolean confirmDownload(File f, String downloadPath) throws IOException {
        if (!always_approve) {
            int a = UserApproveDownload.ask();

            if (a == JOptionPane.NO_OPTION) {
                always_approve = true;
            }

            if (a == JOptionPane.OK_OPTION || a == JOptionPane.NO_OPTION) {
                download(parser.getPathToFileOnServer(f.getName()), downloadPath, f.isDirectory(), f.getName());
                return true;
            } else {
                return false;
            }
        } else {
            download(parser.getPathToFileOnServer(f.getName()), downloadPath, f.isDirectory(), f.getName());
            return true;
        }
    }

    private void download(String pathOnServer, String downloadPath, boolean isDirectory, String filename) throws IOException {
        parser.getAsyncParser().interrupt();
        if(isDirectory) {
            new File(downloadPath + File.separator + filename).mkdir();
            for(FTPFile file : connector.getClient().listFiles(pathOnServer)) {
                download(pathOnServer + "/" + file.getName(), downloadPath + File.separator + filename, file.isDirectory(), file.getName());
            }
        } else {
            FileOutputStream download = new FileOutputStream(new File(downloadPath + File.separator + filename));
            connector.getClient().retrieveFile(pathOnServer, download);

            download.close();
        }
        parser.getAsyncParser().interruptComplete();
    }
}
