package de.dragon.FTClient.ftpnet;

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

    public AsyncParser(Parser parser) {
        this.parser = parser;

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        executor.submit(new Worker(lowPrio_q, highPrio_q, this, "/"));
    }

    public void addToLowPrio(ParseData data) {
        lowPrio_q.add(data);
    }

    public void addToHighPrio(ParseData data) {
        if(lowPrio_q.isEmpty()) {
            lowPrio_q.add(data);
        } else {
            highPrio_q.add(data);
        }
    }

    public Parser getParser() {
        return parser;
    }

    public LinkedList<String> getAlready_build() {
        return already_build;
    }

}
