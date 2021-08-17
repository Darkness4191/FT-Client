package de.dragon.FTClient.frame.progressbar;

import de.dragon.FTClient.async.MasterQueue;

import javax.swing.*;

public class CancelPacketMenu extends JPopupMenu {

    public CancelPacketMenu(MasterQueue queue) {
        super();

        this.add(new CancelOption(queue));
    }

}
