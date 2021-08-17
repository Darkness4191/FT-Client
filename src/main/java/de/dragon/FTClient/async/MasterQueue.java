package de.dragon.FTClient.async;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.ftpnet.Packet;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

public class MasterQueue {

    private ArrayBlockingQueue<Event> events = new ArrayBlockingQueue<Event>(5000);
    public Thread master;
    private FTPFrame frame;
    private Packet current;

    public MasterQueue(FTPFrame frame) {
        this.frame = frame;

        master = new Thread(this::run);
        master.start();
    }

    private void run() {
        Thread.currentThread().setName("Master");
        while (true) {
            try {
                Event event = events.take();
                current = event.getRunnable();
                current.execute();
                synchronized (event.getThread()) {
                    event.getThread().notify();
                }
            } catch (Exception e) {
                frame.criticalError(e);
            }
        }
    }

    public void sendAndWait(Packet run) {
        try {
            if (!Thread.currentThread().equals(master)) {
                events.add(new Event(Thread.currentThread(), run));
                synchronized (Thread.currentThread()) {
                    Thread.currentThread().wait();
                }
            } else {
                run.execute();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            frame.criticalError(e);
        }
    }

    public void send(Packet run) {
        events.add(new Event(Thread.currentThread(), run));
    }

    public void clearList() {
        events.clear();
    }

    public void cancelCurrentPacket() {
        current.setCanceled(true);
    }

}
