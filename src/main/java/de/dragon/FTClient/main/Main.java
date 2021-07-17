package de.dragon.FTClient.main;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        EventQueue.invokeLater(() -> {
            try {
                new FTPFrame();
            } catch (IOException | UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

}
