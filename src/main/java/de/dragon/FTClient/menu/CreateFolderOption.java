package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.ftpnet.Packet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class CreateFolderOption extends FMenuItem {

    private FTPFrame frame;

    public CreateFolderOption(FTPFrame frame) {
        super();
        this.frame = frame;
        this.setText("Folder");
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(frame.isInit()) {
            String ans = JOptionPane.showInputDialog(frame, "Folder name: ", "Create Folder", JOptionPane.PLAIN_MESSAGE);

            if(ans != null && !ans.equals("")) {
                frame.getMasterQueue().send(new Packet() {
                    @Override
                    public void execute() throws IOException, InterruptedException {
                        String dirOnServer = frame.getParser().getCurrentDirOnServer();
                        frame.getParser().getConnector().getClient().makeDirectory(dirOnServer + (dirOnServer.endsWith("/") ? ans : "/" + ans));
                        frame.getParser().refreshView(false);
                    }
                });
            }
        }
    }
}
