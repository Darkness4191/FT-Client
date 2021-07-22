package de.dragon.FTClient.async;

import java.util.concurrent.ArrayBlockingQueue;

public class MasterQueue {

    private ArrayBlockingQueue<Event> events = new ArrayBlockingQueue<Event>(5000);
    public Thread master;

    public MasterQueue() {
        master = new Thread(this::run);
        master.start();
    }

    private void run() {
        Thread.currentThread().setName("Master");
        while(true) {
            try {
                Event event = events.take();
                event.getRunnable().run();
                synchronized (event.getThread()) {
                    event.getThread().notify();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void put(Runnable run) {
        if(!Thread.currentThread().equals(master)) {
            events.add(new Event(Thread.currentThread(), run));
            try {
                synchronized (Thread.currentThread()) {
                    Thread.currentThread().wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            run.run();
        }
    }

}
