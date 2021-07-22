package de.dragon.FTClient.ftpnet;

import de.dragon.UsefulThings.ut;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class AsyncParser {

    private ArrayBlockingQueue<ParseData> lowPrio_q = new ArrayBlockingQueue<>(100);
    private ArrayBlockingQueue<ParseData> highPrio_q = new ArrayBlockingQueue<>(100);

    private LinkedList<String> already_build = new LinkedList<>();

    private ThreadPoolExecutor executor;

    private Parser parser;
    private int currentlyInterrupting = 0;
    private int currentlyWaiting = 0;

    private ArrayBlockingQueue<Integer> release = new ArrayBlockingQueue<>(10);
    private ArrayBlockingQueue<Integer> interrupt = new ArrayBlockingQueue<>(10);

    public AsyncParser(Parser parser) {
        this.parser = parser;

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        executor.submit(this::worker);
    }

    public void addToLowPrio(ParseData data) {
        lowPrio_q.add(data);
    }

    public void addToHighPrio(ParseData data) {
        if (lowPrio_q.isEmpty()) {
            lowPrio_q.add(data);
        } else {
            highPrio_q.add(data);
        }
    }

    private void worker() {
        while (true) {
            try {
                ParseData data = null;
                if (!highPrio_q.isEmpty()) {
                    data = highPrio_q.take();
                } else {
                    data = lowPrio_q.take();
                }

                if (currentlyInterrupting > 0) {
                    if(currentlyWaiting > 0) {
                        release.add(1);
                    }
                    interrupt.take();
                }

                String fromServer = data.getPath().replace(parser.getFrame().PATH_TO_TEMP, "").replace("\\", "/");
                if (fromServer.equals("")) {
                    fromServer = "/";
                }

                if (!already_build.contains(data.getPath())) {
                    parser.parseFile("^^^", data.getPath(), true);
                    already_build.add(data.getPath());
                }

                FTPFile[] files = parser.getConnector().getClient().listFiles(fromServer);
                for (FTPFile c : files) {
                    if (c.isDirectory() && data.preload()) {
                        lowPrio_q.add(new ParseData(data.getPath() + File.separator + c.getName(), false));
                    }
                    parser.parseFile(c, data.getPath());
                }

                for (File c : new File(data.getPath()).listFiles()) {
                    if (!conainsName(files, c.getName()) && !c.getName().equals("^^^")) {
                        ut.deleteFileRec(c);
                    }
                }

                if (parser.getFrame().getFtpChooser().getCurrentDirectory().getAbsolutePath().equals(data.getPath())) {
                    parser.getFrame().getFtpChooser().rescanCurrentDirectory();
                }
            } catch (Exception e) {
                e.printStackTrace();
                parser.getFrame().criticalError(e);
            }
        }
    }

    private boolean conainsName(FTPFile[] files, String name) {
        for (FTPFile c : files) {
            if (c.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Parser getParser() {
        return parser;
    }

    public void interrupt() {
        currentlyInterrupting++;
        try {
            if(!lowPrio_q.isEmpty() && !highPrio_q.isEmpty() || currentlyInterrupting > 1) {
                currentlyWaiting++;
                release.take();
                currentlyWaiting--;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        if(currentlyWaiting > 0) {
            release.add(1);
        }
        interrupt.add(1);
        currentlyInterrupting--;
    }

    public LinkedList<String> getAlready_build() {
        return already_build;
    }

}
