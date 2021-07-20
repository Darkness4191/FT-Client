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
        uploadToPath(f, parser.getCurrentDirOnServer());
    }

    private void uploadToPath(File f, String path) throws InterruptedException, IOException {
        if(f.isDirectory()) {
            String nextpath = path + (path.endsWith("/") ?  f.getName() : "/" + f.getName());
            connector.getClient().makeDirectory(nextpath);
            for(File c : f.listFiles()) {
                uploadToPath(c, nextpath);
            }
        } else {
            parser.getAsyncParser().waitForRelease();
            FileInputStream inputStream = new FileInputStream(f);

            String s = path + (path.endsWith("/") ?  f.getName() : "/" + f.getName());

            OutputStream out = connector.getClient().storeFileStream(path + (path.endsWith("/") ?  f.getName() : "/" + f.getName()));
            byte[] buffer = new byte[16 * 1024];
            int am;
            while((am = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, am);
                out.flush();
            }
            out.close();
            connector.getClient().completePendingCommand();
            inputStream.close();
            parser.getAsyncParser().interuptComplete();
        }
    }
}
