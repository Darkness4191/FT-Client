package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class EnableHiddenFilesOption extends JCheckBoxMenuItem implements ActionListener {

    private FTPFrame frame;
    private boolean state = false;

    public EnableHiddenFilesOption(FTPFrame frame) {
        super();
        this.frame = frame;
        this.setText("Hidden Files");
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(frame.isInit()) {
            try {
                state = !state;
                frame.getClient().setListHiddenFiles(state);
                frame.refreshView(false);
            } catch (IOException ioException) {
                state = false;
                frame.criticalError(ioException);
                ioException.printStackTrace();
            }
        }
    }
}
