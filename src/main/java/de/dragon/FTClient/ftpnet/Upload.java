package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.progressbar.ProgressBar;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Upload extends Packet implements PipeStream {

    private Connector connector;
    private Parser parser;

    private ArrayList<File> failed = new ArrayList<>();

    public Upload(Parser parser) {
        this.connector = parser.getConnector();
        this.parser = parser;
    }

    @Override
    public void execute() throws IOException, InterruptedException {
        failed.clear();

        int total = 0;
        for (File f : files) {
            total += getFileCount(f);
        }

        for (File f : files) {
            ProgressBar bar = new ProgressBar(parser.getFrame());
            bar.init();

            uploadToPath(f, parser.getPathToFileOnServer(f.getName()), bar);

            JOptionPane.showMessageDialog(parser.getFrame().getDropField(),
                    String.format("Upload complete: Successful: %d, Failed: %d%nFailed: " + merge(failed, "%nFailed: "), total - failed.size(), failed.size()),
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            bar.dispose();
            parser.refreshView(false);
        }
    }

    private int getFileCount(File dir) {
        if (dir.isDirectory()) {
            int r = 0;
            for (File f : dir.listFiles()) {
                r += getFileCount(f);
            }
            return r;
        } else {
            return 1;
        }
    }

    private void uploadToPath(File f, String pathOnServer, ProgressBar progressBar) throws InterruptedException, IOException {
        if (f.isDirectory()) {
            connector.getClient().makeDirectory(pathOnServer);
            for (File c : f.listFiles()) {
                uploadToPath(c, pathOnServer + "/" + c.getName(), progressBar);
            }
        } else {
            try {
                progressBar.updateString(f.getName());
                FileInputStream inputStream = new FileInputStream(f);
                OutputStream out = connector.getClient().storeFileStream(pathOnServer);
                long filesize = f.length();
                pipe(inputStream, out, progressBar, filesize);

                out.close();
                connector.getClient().completePendingCommand();
                inputStream.close();
            } catch (Exception e) {
                failed.add(f);
            }
        }
    }

    private String merge(List<? extends File> list, String mergeChar) {
        StringBuilder builder = new StringBuilder();

        for(File s : list) {
            builder.append(s.getAbsolutePath()).append(mergeChar);
        }

        return builder.toString().substring(0, builder.length() - 2);
    }
}
