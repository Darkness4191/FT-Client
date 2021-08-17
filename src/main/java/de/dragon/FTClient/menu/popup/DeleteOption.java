package de.dragon.FTClient.menu.popup;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.ftpnet.Delete;
import de.dragon.FTClient.menu.FMenuItem;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class DeleteOption extends FMenuItem {

    FTPFrame frame;

    public DeleteOption(FTPFrame frame) {
        super();
        this.frame = frame;
        this.setText("Delete");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(frame.getFtpChooser().getSelectedFile() != null) {
            int answer = JOptionPane.showConfirmDialog(frame.getDropField(), "Do you really want to delete the selected files?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                Delete delete = new Delete(frame.getParser());
                delete.addFiles(frame.getFtpChooser().getSelectedFiles());
                frame.getMasterQueue().send(delete);
            }
        }
    }
}
