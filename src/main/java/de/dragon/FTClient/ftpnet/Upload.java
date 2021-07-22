package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.progressbar.ProgressBar;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Upload {

    private Connector connector;
    private Parser parser;
    private ArrayBlockingQueue<File> q = new ArrayBlockingQueue<>(1000);

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private boolean closed = false;

    public Upload(Parser parser) {
        this.connector = parser.getConnector();
        this.parser = parser;

        executor.submit(this::worker);
    }

    public void upload(File f) {
        q.add(f);
    }

    private void worker() {
        Thread.currentThread().setName("Upload Thread");
        while(true) {
            try {
                File f = q.take();

                ProgressBar bar = new ProgressBar(parser.getFrame());
                bar.init();
                uploadToPath(f, parser.getPathToFileOnServer(f.getName()), bar);

                JOptionPane.showMessageDialog(parser.getFrame().getDropField(), "Upload complete", "Info", JOptionPane.INFORMATION_MESSAGE);
                bar.dispose();
                parser.refreshView(false);
            } catch(Exception e) {
                parser.getFrame().criticalError(e);
            }
        }
    }

    private void uploadToPath(File f, String path, ProgressBar progressBar) throws InterruptedException, IOException {
        if(f.isDirectory()) {
            connector.getClient().makeDirectory(path);
            for(File c : f.listFiles()) {
                uploadToPath(c, path + "/" + c.getName(), progressBar);
            }
        } else {
            progressBar.updateString(f.getName());
            FileInputStream inputStream = new FileInputStream(f);

            parser.getFrame().getMasterQueue().put(() -> {
                try {
                    OutputStream out = connector.getClient().storeFileStream(path);
                    long filesize = f.length();
                    byte[] buffer = new byte[16 * 1024];
                    int am;
                    int rounds = 0;
                    while((am = inputStream.read(buffer)) > 0) {
                        progressBar.updatePercent((rounds * buffer.length * 1D + am) / filesize);
                        out.write(buffer, 0, am);
                        out.flush();
                        rounds++;
                    }
                    out.close();
                    connector.getClient().completePendingCommand();
                    inputStream.close();
                }catch (Exception e) {
                    parser.getFrame().criticalError(e);
                }
            });
        }
    }
}
