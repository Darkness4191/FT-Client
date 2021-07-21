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
    private ArrayBlockingQueue<File> q = new ArrayBlockingQueue<>(100);

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    private boolean closed = false;

    public Upload(Parser parser) {
        this.connector = parser.getConnector();
        this.parser = parser;

        executor.submit(this::take);
    }

    public void upload(File f) {
        q.add(f);
    }

    private void take() {
        while(true) {
            try {
                File f = q.take();
                parser.getAsyncParser().interrupt();
                ProgressBar bar = new ProgressBar(parser.getFrame());
                uploadToPath(f, parser.getPathToFileOnServer(f.getName()), bar);
                JOptionPane.showMessageDialog(parser.getFrame().getDropField(), "Upload complete", "Info", JOptionPane.INFORMATION_MESSAGE);
                bar.dispose();
                parser.getAsyncParser().release();
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

            OutputStream out = connector.getClient().storeFileStream(path);
            long filesize = f.length();
            byte[] buffer = new byte[16 * 1024];
            int am;
            int rounds = 0;
            while((am = inputStream.read(buffer)) > 0) {
                progressBar.updatePercent(rounds * am * 1D / filesize);
                out.write(buffer, 0, am);
                out.flush();
                rounds++;
            }
            out.close();
            connector.getClient().completePendingCommand();
            inputStream.close();
        }
    }
}
