package de.dragon.FTClient.misc;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TextFieldKeyListener implements KeyListener, FocusListener, MouseListener {

    protected JTextField textField;

    protected boolean already_changed = false;
    protected String s;
    protected FTPFrame frame;

    public TextFieldKeyListener(JTextField textField, String s, FTPFrame frame) {
        this.textField = textField;
        this.s = s;
        this.frame = frame;

        changeToPreWrite();
    }

    public void changeToNormal() {
        if(!already_changed) {
            already_changed = true;
            textField.setText("");
            textField.setForeground(Color.BLACK);
        }
    }

    public void changeToPreWrite() {
        if(textField.getText().length() == 0) {
            already_changed = false;
            textField.setText(s);
            textField.setForeground(Color.GRAY);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        changeToNormal();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void focusGained(FocusEvent e) {
        if(!already_changed) {
            textField.setCaretPosition(0);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        changeToPreWrite();
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
}
