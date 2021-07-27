package de.dragon.FTClient;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.UsefulThings.dir.DeleteOnExitReqCall;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            Thread.currentThread().setName("Main");
            new FTPFrame();
        } catch (IOException | ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            DeleteOnExitReqCall.collectTrash();
        }
    }
}
