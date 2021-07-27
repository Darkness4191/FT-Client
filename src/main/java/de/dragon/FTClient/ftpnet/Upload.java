package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.progressbar.ProgressBar;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Upload extends Packet {

    private Connector connector;
    private Parser parser;

    private boolean closed = false;

    public Upload(Parser parser) {
        this.connector = parser.getConnector();
        this.parser = parser;
    }

    @Override
    public void execute() throws IOException, InterruptedException {
        for (File f : files) {
            ProgressBar bar = new ProgressBar(parser.getFrame());
            bar.init();
            uploadToPath(f, parser.getPathToFileOnServer(f.getName()), bar);

            JOptionPane.showMessageDialog(parser.getFrame().getDropField(), "Upload complete", "Info", JOptionPane.INFORMATION_MESSAGE);
            bar.dispose();
            parser.refreshView(false);
        }
    }

    private void uploadToPath(File f, String path, ProgressBar progressBar) throws InterruptedException, IOException {
        if (f.isDirectory()) {
            connector.getClient().makeDirectory(path);
            for (File c : f.listFiles()) {
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
            while ((am = inputStream.read(buffer)) > 0) {
                progressBar.updatePercent((rounds * buffer.length * 1D + am) / filesize);
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
