package de.dragon.FTClient.frame.progressbar;

import de.dragon.FTClient.async.MasterQueue;
import de.dragon.FTClient.menu.FMenuItem;

import java.awt.event.ActionEvent;

public class CancelOption extends FMenuItem {

    private MasterQueue queue;

    public CancelOption(MasterQueue queue) {
        super();
        this.queue = queue;
        this.setText("Cancel Operation");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        queue.cancelCurrentPacket();
    }
}
