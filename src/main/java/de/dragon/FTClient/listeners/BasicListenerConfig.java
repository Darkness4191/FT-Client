package de.dragon.FTClient.listeners;

import javax.swing.*;
import java.awt.*;

public class BasicListenerConfig {

    public static void configAll(Component c, BasicTextFieldListener listener) {
        c.addMouseListener(listener);
        c.addKeyListener(listener);
        c.addFocusListener(listener);
    }

    public static void configAll(JComponent c, BasicTextFieldListener listener) {
        c.addMouseListener(listener);
        c.addKeyListener(listener);
        c.addFocusListener(listener);
    }

}
