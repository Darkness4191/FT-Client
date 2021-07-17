package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogoutMenu extends JMenuItem implements ActionListener {

    private FTPFrame frame;
    private boolean state = false;

    public LogoutMenu(FTPFrame frame) {
        super();
        this.frame = frame;
        this.setText("Logout");
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(frame.isInit()) {
            frame.uninit();
        }
    }
}
