package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class RefreshOption extends FMenuItem {

    private FTPFrame frame;

    public RefreshOption(FTPFrame frame) {
        super();
        this.frame = frame;
        this.setText("Refresh");
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(frame.isInit()) {
            try {
                frame.refreshView();
            } catch (IOException ioException) {
                frame.criticalError(ioException);
                ioException.printStackTrace();
            }
        }
    }
}
