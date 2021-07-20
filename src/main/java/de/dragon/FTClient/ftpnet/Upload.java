package de.dragon.FTClient.ftpnet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Upload {

    private Connector connector;
    private Parser parser;


    private boolean closed = false;

    public Upload(Parser parser) {
        this.connector = parser.getConnector();
        this.parser = parser;

    }

    public void upload(File f) throws InterruptedException, IOException {
        parser.getAsyncParser().interrupt();
        uploadToPath(f, parser.getPathToFileOnServer(f.getName()));
        parser.getAsyncParser().interruptComplete();
    }

    private void uploadToPath(File f, String path) throws InterruptedException, IOException {
        if(f.isDirectory()) {
            connector.getClient().makeDirectory(path);
            for(File c : f.listFiles()) {
                uploadToPath(c, path + "/" + c.getName());
            }
        } else {
            FileInputStream inputStream = new FileInputStream(f);

            OutputStream out = connector.getClient().storeFileStream(path);
            byte[] buffer = new byte[16 * 1024];
            int am;
            while((am = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, am);
                out.flush();
            }
            out.close();
            connector.getClient().completePendingCommand();
            inputStream.close();
        }
    }
}
