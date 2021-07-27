package de.dragon.FTClient.async;

import de.dragon.FTClient.ftpnet.Packet;

public class Event {

    private Thread thread;
    private Packet packet;

    public Event(Thread executor, Packet run) {
        this.thread = executor;
        this.packet = run;
    }

    public Thread getThread() {
        return thread;
    }

    public Packet getRunnable() {
        return packet;
    }
}
