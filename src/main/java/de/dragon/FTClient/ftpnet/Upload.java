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
    private int count = 0;
    private int total = 0;

    public Upload(Parser parser) {
        this.connector = parser.getConnector();
        this.parser = parser;
    }

    @Override
    public void execute() {
        failed.clear();
        for (File f : files) {
            total += getFileCount(f);
        }

        for (File f : files) {
            ProgressBar bar = new ProgressBar(parser.getFrame());
            bar.init();

            try {
                uploadToPath(f, parser.getPathToFileOnServer(f.getName()), bar);
                parser.refreshView(false);
            } catch (Exception e) {
                try {
                    System.out.println(parser.getConnector().getClient().getReply());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                String pre = String.format("Upload complete: Successful: %d, Failed: %d", count - failed.size(), failed.size() + total - count);

                if(failed.size() > 20) {
                    failed.removeAll(failed.subList(20, failed.size() - 1));
                }

                JOptionPane.showMessageDialog(parser.getFrame().getDropField(),
                        pre + (failed.size() > 0 ? "\nFailed: " : "") + merge(failed, "\nFailed: ") + (failed.size() > 20 ? "\n..." : ""),
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                bar.dispose();
            }
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

    private void uploadToPath(File f, String pathOnServer, ProgressBar progressBar) throws IOException {
        if(!canceled) {
            if (f.isDirectory()) {
                connector.getClient().makeDirectory(pathOnServer);
                for (File c : f.listFiles()) {
                    uploadToPath(c, pathOnServer + "/" + c.getName(), progressBar);
                }
            } else {
                try {
                    progressBar.updateString(f.getName() + String.format(" (%d/%d)", count + 1, total));
                    FileInputStream inputStream = new FileInputStream(f);
                    OutputStream out = connector.getClient().storeFileStream(pathOnServer);
                    long filesize = f.length();
                    pipe(inputStream, out, progressBar, filesize);

                    out.close();
                    connector.getClient().completePendingCommand();
                    inputStream.close();
                } catch (Exception e) {
                    failed.add(f);
                } finally {
                    count++;
                }
            }
        }
    }

    private String merge(List<? extends File> list, String mergeChar) {
        StringBuilder builder = new StringBuilder();

        for(File s : list) {
            builder.append(s.getAbsolutePath()).append(mergeChar);
        }

        return builder.toString().substring(0, builder.length() != 0 ? builder.length() - mergeChar.length() : 0);
    }
}
