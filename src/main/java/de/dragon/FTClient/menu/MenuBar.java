package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;

public class MenuBar extends JMenuBar {

    public MenuBar(FTPFrame frame) {
        super();

        JMenu menu = new JMenu("File");
        menu.add(new UploadFileMenu(frame));
        menu.add(new DeleteMenu(frame));

        this.add(menu);
    }

}
