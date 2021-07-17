package de.dragon.FTClient.frame;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class GenericBorder implements Border {

    private Border border;

    public GenericBorder() {
        border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
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
