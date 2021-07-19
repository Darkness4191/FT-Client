package de.dragon.FTClient.ftpnet;

import de.dragon.UsefulThings.ut;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

public class Worker implements Runnable {

    private ArrayBlockingQueue<ParseData> q;
    private ArrayBlockingQueue<ParseData> prio_q;
    private AsyncParser parent;

    private String seperator;

    public Worker(ArrayBlockingQueue<ParseData> queue, ArrayBlockingQueue<ParseData> prioQ, AsyncParser parent, String seperator) {
        this.seperator = seperator;
        this.parent = parent;
        this.prio_q = prioQ;
        q = queue;
    }

    @Override
    public void run() {
        while(true) {
            try {
                ParseData data = null;
                if(!prio_q.isEmpty()) {
                    data = prio_q.take();
                } else {
                    data = q.take();
                }

                String fromServer = data.getPath().replace(parent.getParser().getFrame().PATH_TO_TEMP, "").replace("\\", seperator);
                if(fromServer.equals("")) {
                    fromServer = seperator;
                }

                System.out.println(fromServer);

                if(!parent.getAlready_build().contains(data.getPath())) {
                    parent.getParser().parseFile("^^^", data.getPath(), true);
                    parent.getAlready_build().add(data.getPath());
                }

                FTPFile[] files = parent.getParser().getConnector().getClient().listFiles(fromServer);
                for(FTPFile c : files) {
                    if(c.isDirectory() && data.preload()) {
                        parent.addToLowPrio(new ParseData(data.getPath() + File.separator + c.getName(), false));
                    }
                    parent.getParser().parseFile(c, data.getPath());
                }

                for (File c : new File(data.getPath()).listFiles()) {
                    if (!conainsName(files, c.getName()) && !c.getName().equals("^^^")) {
                        ut.deleteFileRec(c);
                    }
                }

                if(parent.getParser().getFrame().getFtpChooser().getCurrentDirectory().getAbsolutePath().equals(data.getPath())) {
                    parent.getParser().getFrame().getFtpChooser().rescanCurrentDirectory();
                }

            } catch (Exception e) {
                e.printStackTrace();
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
}
