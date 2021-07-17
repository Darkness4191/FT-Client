package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.frame.Task;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteMenu extends JMenuItem implements ActionListener {

    private FTPFrame frame;

    public DeleteMenu(FTPFrame frame) {
        super();
        this.frame = frame;
        this.setText("Delete");
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.setTask(Task.delete);
    }
}
