package de.dragon.FTClient.ftpnet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Packet {

    protected ArrayList<File> files = new ArrayList<>();

    public abstract void execute() throws IOException, InterruptedException;

    public void addFile(File f) {
        files.add(f);
    }

    public void addFiles(File... filelist) {
        files.addAll(Arrays.asList(filelist));
    }

}
