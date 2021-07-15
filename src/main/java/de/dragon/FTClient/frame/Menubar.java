package de.dragon.FTClient.frame;

import de.dragon.UsefulThings.misc.DebugPrinter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ArrayBlockingQueue;

public class Menubar extends JPanel implements ActionListener {

    private ArrayBlockingQueue<LoginContainer> q = new ArrayBlockingQueue<>(1);

    public Menubar() {
        GridLayout layout = new GridLayout();
        layout.setVgap(2);
        layout.setHgap(2);
        layout.setColumns(4);
        layout.setRows(1);
        setLayout(layout);

        JPasswordField passwordField = new JPasswordField();
        JTextField textField = new JTextField();
        JTextField hostField = new JTextField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this);

        add(hostField);
        add(textField);
        add(passwordField);
        add(loginButton);
    }

    public LoginContainer getContainer() throws InterruptedException {
        return q.take();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
    }
}
