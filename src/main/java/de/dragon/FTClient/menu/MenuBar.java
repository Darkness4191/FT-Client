package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;

public class MenuBar extends JMenuBar {

    public MenuBar(FTPFrame frame) {
        super();

        JMenu file = new JMenu("File");
        file.add(new UploadFileMenu(frame));
        file.add(new DeleteMenu(frame));
        file.add(new LogoutMenu(frame));

        JMenu view = new JMenu("View");
        view.add(new EnableHiddenFiles(frame));

        this.add(file);
        this.add(view);
    }

}
