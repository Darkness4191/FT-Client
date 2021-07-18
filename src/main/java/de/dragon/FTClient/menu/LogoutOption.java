package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;

import java.awt.event.ActionEvent;

public class LogoutOption extends FMenuItem {

    private FTPFrame frame;
    private boolean state = false;

    public LogoutOption(FTPFrame frame) {
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
