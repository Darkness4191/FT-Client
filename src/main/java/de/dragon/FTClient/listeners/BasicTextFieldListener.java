package de.dragon.FTClient.listeners;

import de.dragon.FTClient.frame.FTPFrame;

import java.awt.*;
import java.awt.event.*;

public class BasicTextFieldListener implements KeyListener, FocusListener, MouseListener {

    private FTPFrame frame;

    public BasicTextFieldListener(FTPFrame frame) {
        this.frame = frame;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        frame.getDropField().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        frame.getDropField().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
