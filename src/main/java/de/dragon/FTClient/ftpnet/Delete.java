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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Delete implements ActionListener {

    private Parser parser;
    private FTPFrame frame;
    private Connector connector;

    private ArrayBlockingQueue<File[]> q = new ArrayBlockingQueue<>(1000);

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private int currentlyDeleted = 0;

    public Delete(Parser parser) {
        this.parser = parser;
        this.frame = parser.getFrame();
        this.connector = parser.getConnector();

        executor.submit(this::worker);
    }

    private void worker() {
        Thread.currentThread().setName("Delete Thread");
        while(true) {
            try {
                File[] selectedFiles = q.take();
                currentlyDeleted = 0;

                ProgressBar bar = new ProgressBar(frame);
                for (int i = 0; i < selectedFiles.length; i++) {
                    bar.updatePercent(i * 1D / selectedFiles.length, selectedFiles[i].getName());
                    int finalI = i;
                    frame.getMasterQueue().put(() -> {
                        try {
                            currentlyDeleted += delete(parser.getPathToFileOnServer(selectedFiles[finalI].getName()), selectedFiles[finalI].getName(), selectedFiles[finalI].isDirectory());
                        } catch (IOException e) {
                            frame.criticalError(e);
                        }
                    });
                }

                bar.dispose();
                JOptionPane.showMessageDialog(frame.getDropField(), String.format("Deleted %d files", currentlyDeleted), "Info", JOptionPane.INFORMATION_MESSAGE);
                frame.refreshView(false);
            } catch (IOException | InterruptedException ioException) {
                frame.criticalError(ioException);
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION) && parser.getFrame().getTask() == Task.delete) {
            File[] selectedFiles = frame.getFtpChooser().getSelectedFiles();

            int answer = JOptionPane.showConfirmDialog(frame.getDropField(), "Do you really want to delete the selected files?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                q.add(selectedFiles);
            }
            frame.getFilenameField().setText("");

        }
    }

    public int delete(String pathOnServer, String filename, boolean isDirectory) throws IOException {
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
    }
}
