package de.dragon.FTClient.async;

public class Event {

    private Thread thread;
    private Runnable runnable;

    public Event(Thread executor, Runnable run) {
        this.thread = executor;
        this.runnable = run;
    }

    public Thread getThread() {
        return thread;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
