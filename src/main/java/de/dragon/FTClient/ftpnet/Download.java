package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.progressbar.ProgressBar;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Download extends Packet implements PipeStream {

    private Parser parser;
    private String download_dir;

    public static boolean always_approve = false;

    private ArrayList<String> failed = new ArrayList<>();
    private int passed = 0;

    public Download(Parser parser) {
        this.parser = parser;

        File exi = new File(System.getProperty("user.home") + File.separator + "Downloads");
        if (!exi.exists()) {
            exi.mkdirs();
        }

        download_dir = exi.getAbsolutePath();
    }

    @Override
    public void execute() throws IOException {
        ProgressBar progressBar = new ProgressBar(parser.getFrame());
        failed.clear();
        passed = 0;

        if (confirmDownload()) {
            for (int i = 0; i < files.size(); i++) {
                if (!files.get(i).getName().equals("^^^")) {
                    progressBar.init();
                    download(parser.getPathToFileOnServer(files.get(i).getName()), download_dir, files.get(i).isDirectory(), files.get(i).getName(), progressBar);
                }
            }
        }

        String pre = String.format("Successful: %d, Failed: %d. Saved file to %s" + merge(failed, "%nFailed: "), passed, failed.size(), download_dir);
        if(failed.size() > 20) {
            failed.removeAll(failed.subList(20, failed.size() - 1));
        }

        JOptionPane.showMessageDialog(parser.getFrame().getDropField(),
                pre + (failed.size() > 0 ? "\nFailed: " : "") + merge(failed, "\nFailed: ") + (failed.size() > 20 ? "\n..." : ""),
                "Info", JOptionPane.INFORMATION_MESSAGE);

        progressBar.dispose();

        parser.refreshView(false);
    }

    private void download(String pathOnServer, String downloadPath, boolean isDirectory, String filename, ProgressBar bar) throws IOException {
        if(!canceled) {
            if (isDirectory) {
                new File(downloadPath + File.separator + filename).mkdir();
                for (FTPFile file : parser.getConnector().getClient().listFiles(pathOnServer)) {
                    download(pathOnServer + "/" + file.getName(), downloadPath + File.separator + filename, file.isDirectory(), file.getName(), bar);
                }
            } else {
                try {
                    bar.setString(filename);
                    FileOutputStream download = new FileOutputStream(new File(downloadPath + File.separator + filename));
                    long filesize = Long.parseLong(parser.getConnector().getClient().getSize(pathOnServer));

                    InputStream in = parser.getConnector().getClient().retrieveFileStream(pathOnServer);
                    pipe(in, download, bar, filesize);

                    bar.updatePercent(1);
                    download.close();
                    in.close();

                    parser.getConnector().getClient().completePendingCommand();
                    passed++;
                } catch (Exception e) {
                    failed.add(filename);
                    parser.getConnector().getClient().completePendingCommand();
                }
            }
        }
    }

    private boolean confirmDownload() throws IOException {
        if (!always_approve) {
            int a = JOptionPane.showOptionDialog(null, "Do you want to download the selected file(s) from the server?", "Approve Download", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Yes", "Yes, don't ask me again", "No"}, "Yes");
            if (a == JOptionPane.NO_OPTION) {
                always_approve = true;
            }

            return a == JOptionPane.OK_OPTION || a == JOptionPane.NO_OPTION;
        } else {
            return true;
        }
    }

    private String merge(List<? extends String> list, String mergeChar) {
        StringBuilder builder = new StringBuilder();

        for(String s : list) {
            builder.append(s).append(mergeChar);
        }

        return builder.toString().substring(0, builder.length() != 0 ? builder.length() - mergeChar.length() : 0);
    }
}
