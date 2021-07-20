package de.dragon.FTClient.frame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class GenericBorder implements Border {

    private Border border;

    public GenericBorder() {
        border = BorderFactory.createLineBorder(new Color(177, 177, 177), 1);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        border.paintBorder(c, g, x, y, width, height);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return border.getBorderInsets(c);
    }

    @Override
    public boolean isBorderOpaque() {
        return border.isBorderOpaque();
    }
}
