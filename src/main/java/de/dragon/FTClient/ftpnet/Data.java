package de.dragon.FTClient.ftpnet;

public class Data {

    private boolean preload;
    private String path;

    public Data(String path, boolean preload) {
        this.path = path;
        this.preload = preload;
    }

    public boolean preload() {
        return preload;
    }

    public String getPath() {
        return path;
    }
}
