package de.dragon.FTClient.menu.popup;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.menu.RefreshOption;

import javax.swing.*;

public class FPopupMenu extends JPopupMenu {

    FTPFrame frame;

    public FPopupMenu(FTPFrame frame) {
        super();
        this.frame = frame;

        this.add(new RefreshOption(frame));
        this.addSeparator();
        this.add(new DeleteOption(frame));
        this.add(new DownloadOption(frame));
    }

}
