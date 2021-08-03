package de.dragon.UsefulThings.dir;

import de.dragon.UsefulThings.ut;

import java.io.File;
import java.util.ArrayList;

/**
 * Part of UsefulThings project
 *
 * @author Dragon777/Darkness4191
 **/

public class DeleteOnExitReqCall {

    private static ArrayList<File> files = new ArrayList<File>();

    public static void add(File f) {
        if(!contains(f)) {
            files.add(f);
        }
    }

    private static boolean contains(File f) {
        for(File c : files) {
            if(c.getAbsolutePath().equals(f)) {
                return true;
            }
        }

        return false;
    }

    public static void collectTrash() {
        for(File f : new ArrayList<File>(files)) {
            ut.deleteFileRec(f);
            files.remove(f);
        }
    }

}
