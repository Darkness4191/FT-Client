package de.dragon.FTClient.misc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TextFieldKeyListener implements KeyListener, FocusListener {

    protected JTextField textField;

    protected boolean already_changed = false;
    protected String s;

    public TextFieldKeyListener(JTextField textField, String s) {
        this.textField = textField;
        this.s = s;

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
}
