package de.dragon.FTClient.misc;

import javax.swing.*;
import java.awt.*;

public class PasswordFieldKeyListener extends TextFieldKeyListener {


    public PasswordFieldKeyListener(JPasswordField passField, String s) {
        super(passField, s);
    }

    @Override
    public void changeToNormal() {
        if(!already_changed) {
            already_changed = true;
            textField.setText("");
            textField.setForeground(Color.BLACK);
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
