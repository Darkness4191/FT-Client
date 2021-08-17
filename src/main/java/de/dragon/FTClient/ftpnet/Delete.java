package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.frame.progressbar.ProgressBar;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import java.io.IOException;

public class Delete extends Packet {

    private Parser parser;
    private FTPFrame frame;
    private Connector connector;

    public Delete(Parser parser) {
        this.parser = parser;
        this.frame = parser.getFrame();
        this.connector = parser.getConnector();
    }

    @Override
    public void execute() throws IOException {
        int deleted = 0;

        ProgressBar bar = new ProgressBar(frame);
        for (int i = 0; i < files.size(); i++) {
            bar.updatePercent(i * 1D / files.size(), files.get(i).getName());
            try {
                parser.getConnector().getClient().setListHiddenFiles(true);
                deleted += delete(parser.getPathToFileOnServer(files.get(i).getName()), files.get(i).getName(), files.get(i).isDirectory());
            } catch (IOException e) {
                frame.criticalError(e);
            } finally {
                parser.getConnector().getClient().setListHiddenFiles(false);
            }
        }

        bar.dispose();
        JOptionPane.showMessageDialog(frame.getDropField(), String.format("Deleted %d files", deleted), "Info", JOptionPane.INFORMATION_MESSAGE);
        frame.refreshView(false);
    }

    private int delete(String pathOnServer, String filename, boolean isDirectory) throws IOException {
        if(!filename.equals("..") && !filename.equals(".")) {
            if (isDirectory) {
                int sum = 0;
                for (FTPFile file : parser.getConnector().getClient().listFiles(pathOnServer)) {
                    sum += delete(pathOnServer + "/" + file.getName(), file.getName(), file.isDirectory());
                }
                parser.getConnector().getClient().removeDirectory(pathOnServer);
                return sum + 1;
            } else {
                parser.getConnector().getClient().deleteFile(pathOnServer);
                return 1;
            }
        } else {
            return 0;
        }
    }
}
