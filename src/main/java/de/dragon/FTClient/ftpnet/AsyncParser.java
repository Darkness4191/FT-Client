package de.dragon.FTClient.ftpnet;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class AsyncParser {

    private ArrayBlockingQueue<ParseData> t1_queue = new ArrayBlockingQueue<>(50);
    private ArrayBlockingQueue<ParseData> t2_queue = new ArrayBlockingQueue<>(50);
    private ArrayBlockingQueue<ParseData> master_queue = new ArrayBlockingQueue<>(100);

    private ThreadPoolExecutor executor;

    private Parser parser;

    public AsyncParser(Parser parser) {
        this.parser = parser;

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

        executor.submit(new Worker(t1_queue, this));
        executor.submit(new Worker(t2_queue, this));
        executor.submit(this::master);
    }

    private void master() {
        while (true) {
            try {
                ParseData data = master_queue.take();
                if(t1_queue.size() > t2_queue.size()) {
                    t2_queue.add(data);
                } else {
                    t1_queue.add(data);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void add(ParseData data) {
        master_queue.add(data);
    }

    public Parser getParser() {
        return parser;
    }

}
