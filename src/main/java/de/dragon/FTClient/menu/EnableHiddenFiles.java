package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class EnableHiddenFiles extends JCheckBoxMenuItem implements ActionListener {

    private FTPFrame frame;
    private boolean state = false;

    public EnableHiddenFiles(FTPFrame frame) {
        super();
        this.frame = frame;
        this.setText("Hidden files");
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(frame.isInit()) {
            try {
                state = !state;
                frame.getClient().setListHiddenFiles(state);
                frame.refreshView();
            } catch (IOException ioException) {
                state = false;
                frame.criticalError(ioException);
                ioException.printStackTrace();
            }
        }
    }
}
