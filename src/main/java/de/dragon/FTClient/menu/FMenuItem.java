package de.dragon.FTClient.menu;

import javax.swing.*;
import java.awt.event.ActionListener;

public abstract class FMenuItem extends JMenuItem implements ActionListener {

    public FMenuItem() {
        super();

        this.addActionListener(this);
    }

}
