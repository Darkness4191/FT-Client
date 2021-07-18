package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.frame.Task;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteOption extends JCheckBoxMenuItem implements ActionListener {

    private FTPFrame frame;
    private boolean toggeled = false;

    public DeleteOption(FTPFrame frame) {
        super();
        this.frame = frame;
        this.setText("Delete");
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(!toggeled) {
            toggeled = true;
            frame.setTask(Task.delete);
        } else {
            toggeled = false;
            frame.setTask(Task.download);
        }
    }
}
