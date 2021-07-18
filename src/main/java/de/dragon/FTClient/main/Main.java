package de.dragon.FTClient.main;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.UsefulThings.dir.DeleteOnExitReqCall;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        try {
            new FTPFrame();
        } catch (IOException | UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            DeleteOnExitReqCall.collectTrash();
        }
    }

}
