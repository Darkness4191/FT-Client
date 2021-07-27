package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.frame.progressbar.ProgressBar;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Download extends Packet {

    private Parser parser;
    private String download_dir;
    private FTPFrame frame;
    private Connector connector;

    private static boolean always_approve = false;

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
    public void execute() throws IOException {
        int passed = 0;
        int failed = 0;

        ProgressBar progressBar = new ProgressBar(frame);

        for (int i = 0; i < files.size(); i++) {
            try {
                if (!files.get(i).getName().equals("^^^") && confirmDownload()) {
                    progressBar.init();
                    download(parser.getPathToFileOnServer(files.get(i).getName()), download_dir, files.get(i).isDirectory(), files.get(i).getName(), progressBar);
                    passed++;
                } else {
                    failed++;
                }
            } catch (IOException ioException) {
                frame.criticalError(ioException);
                ioException.printStackTrace();
                break;
            }
        }

        if (passed > 0) {
            JOptionPane.showMessageDialog(frame.getDropField(), String.format("Successful: %d, Failed: %d. Saved file to %s", passed, failed, download_dir), "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        progressBar.dispose();
        parser.refreshView(false);
    }

    public boolean confirmDownload() throws IOException {
        if (!always_approve) {
            int a = JOptionPane.showOptionDialog(null,
                    "Do you want to download that ressource from the server?",
                    "Approve Download",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,     //do not use a custom Icon
                    new Object[]{"Yes", "Yes, don't ask me again", "No"},  //the titles of buttons
                    "Yes");

            if (a == JOptionPane.NO_OPTION) {
                always_approve = true;
            }

            if (a == JOptionPane.OK_OPTION || a == JOptionPane.NO_OPTION) {
                return true;
            } else
                return false;
        } else
            return true;
    }

    private void download(String pathOnServer, String downloadPath, boolean isDirectory, String filename, ProgressBar bar) throws IOException {
        if (isDirectory) {
            new File(downloadPath + File.separator + filename).mkdir();
            for (FTPFile file : connector.getClient().listFiles(pathOnServer)) {
                download(pathOnServer + "/" + file.getName(), downloadPath + File.separator + filename, file.isDirectory(), file.getName(), bar);
            }
        } else {
            bar.setString(filename);
            FileOutputStream download = new FileOutputStream(new File(downloadPath + File.separator + filename));
            InputStream in = connector.getClient().retrieveFileStream(pathOnServer);

            long filesize = Long.parseLong(connector.getClient().getSize(pathOnServer));
            byte[] buffer = new byte[16 * 1024];
            int am;
            int rounds = 0;
            while ((am = in.read(buffer)) > 0) {
                bar.updatePercent((rounds * buffer.length * 1D + am) / filesize);
                download.write(buffer, 0, am);
                download.flush();
                rounds++;
            }
            download.close();
            in.close();
            connector.getClient().completePendingCommand();
        }
    }
}
