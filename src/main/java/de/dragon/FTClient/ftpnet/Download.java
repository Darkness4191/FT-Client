package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.frame.Task;
import de.dragon.FTClient.frame.UserApproveDownload;
import de.dragon.FTClient.frame.progressbar.ProgressBar;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Download implements ActionListener {

    private Parser parser;
    private String download_dir;
    private FTPFrame frame;
    private Connector connector;
    private ArrayBlockingQueue<File[]> q = new ArrayBlockingQueue<>(1000);

    private boolean always_approve = false;

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public Download(Parser parser) {
        this.parser = parser;
        this.frame = parser.getFrame();
        this.connector = parser.getConnector();

        File exi = new File(System.getProperty("user.home") + File.separator + "FTPDownloads" + File.separator + frame.token);
        if (!exi.exists()) {
            exi.mkdirs();
        }

        download_dir = exi.getAbsolutePath();
        executor.submit(this::take);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION) && parser.getFrame().getTask() == Task.download) {
            q.add(frame.getFtpChooser().getSelectedFiles());
        } else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
            frame.collectTrashandExit();
        }
    }

    private void take() {
        while(true) {
            try {
                File[] files = q.take();
                int passed = 0;
                int failed = 0;

                ProgressBar progressBar = new ProgressBar(frame);

                for (int i = 0; i < files.length; i++) {
                    try {
                        if (!files[i].getName().equals("^^^") && confirmDownload()) {
                            progressBar.init();
                            download(parser.getPathToFileOnServer(files[i].getName()), download_dir, files[i].isDirectory(), files[i].getName(), progressBar);
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

                if(passed > 0) {
                    JOptionPane.showMessageDialog(frame.getDropField(), String.format("Successful: %d, Failed: %d. Saved file to %s", passed, failed, download_dir), "Info", JOptionPane.INFORMATION_MESSAGE);
                }
                frame.getFtpChooser().setSelectedFile(new File(""));
                progressBar.dispose();
                parser.refreshView(false);
            } catch(Exception e) {
                parser.getFrame().criticalError(e);
            }
        }
    }

    public boolean confirmDownload() throws IOException {
        if (!always_approve) {
            int a = UserApproveDownload.ask();

            if (a == JOptionPane.NO_OPTION) {
                always_approve = true;
            }

            if (a == JOptionPane.OK_OPTION || a == JOptionPane.NO_OPTION) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void download(String pathOnServer, String downloadPath, boolean isDirectory, String filename, ProgressBar bar) throws IOException {
        if(isDirectory) {
            new File(downloadPath + File.separator + filename).mkdir();
            parser.getFrame().getMasterQueue().put(() -> {
                try {
                    for(FTPFile file : connector.getClient().listFiles(pathOnServer)) {
                        download(pathOnServer + "/" + file.getName(), downloadPath + File.separator + filename, file.isDirectory(), file.getName(), bar);
                    }
                } catch (Exception e) {
                    parser.getFrame().criticalError(e);
                }
            });
        } else {
            bar.setString(filename);
            FileOutputStream download = new FileOutputStream(new File(downloadPath + File.separator + filename));
            parser.getFrame().getMasterQueue().put(() -> {
                try {
                    InputStream in = connector.getClient().retrieveFileStream(pathOnServer);

                    long filesize = Long.parseLong(connector.getClient().getSize(pathOnServer));
                    byte[] buffer = new byte[16 * 1024];
                    int am;
                    int rounds = 0;
                    while((am = in.read(buffer)) > 0) {
                        bar.updatePercent((rounds * buffer.length * 1D + am) / filesize);
                        download.write(buffer, 0, am);
                        download.flush();
                        rounds++;
                    }
                    download.close();
                    in.close();
                    connector.getClient().completePendingCommand();
                }catch (Exception e) {
                    parser.getFrame().criticalError(e);
                }
            });
        }
    }
}
