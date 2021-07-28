package de.dragon.FTClient.listeners;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;
import java.awt.*;

public class PasswordFieldKeyListener extends TextFieldKeyListener {


    public PasswordFieldKeyListener(JPasswordField passField, String s, FTPFrame frame) {
        super(passField, s, frame);
    }

    @Override
    public void changeToNormal() {
        if(!already_changed) {
            already_changed = true;
            textField.setText("");
            textField.setForeground(UIManager.getColor("TextField.foreground"));
            ((JPasswordField) textField).setEchoChar('●');
        }
    }

    @Override
    public void changeToPreWrite() {
        if(textField.getText().length() == 0) {
            already_changed = false;
            textField.setText(s);
            textField.setForeground(Color.GRAY);
            ((JPasswordField) textField).setEchoChar((char)0);
        }
    }
}
