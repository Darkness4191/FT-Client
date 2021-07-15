package de.dragon.FTClient.ftpnet;

import de.dragon.UsefulThings.ExceptionHandler.ErrorDialog;
import de.dragon.UsefulThings.misc.DebugPrinter;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Upload implements Runnable {

    private Connector connector;
    private Parser parser;

    private ArrayBlockingQueue<File> uploadQueue;
    private Thread uploadThread;

    public Upload(Parser parser) {
        this.connector = parser.getConnector();
        this.parser = parser;

        uploadQueue = new ArrayBlockingQueue<File>(100);
        uploadThread = new Thread(this);

        uploadThread.start();
    }

    public void addToQueue(File f) {
        uploadQueue.add(f);
    }

    @Override
    public void run() {
        while(true) {
            try {
                File f = uploadQueue.take();
                FileInputStream inputStream = new FileInputStream(f);

                connector.getClient().storeFile(f.getName(), inputStream);
                DebugPrinter.println("File stored");
                inputStream.close();

                parser.refreshView();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                new ErrorDialog(e.getCause().getClass().getName() + e.getMessage());
            }
        }
    }
}
