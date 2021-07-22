package de.dragon.FTClient.frame.progressbar;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;
import java.awt.*;

public class ProgressBar extends JProgressBar {

    private int max = 500;

    private FTPFrame parent;
    private boolean isInit = false;

    public ProgressBar(FTPFrame frame) {
        super();
        this.parent = frame;
    }

    public ProgressBar(FTPFrame frame, int max) {
        this(frame);
        this.max = max;
    }

    public void init() {
        if(!isInit) {
            isInit = true;
            this.setPreferredSize(new Dimension((int) parent.getFrame().getSize().getWidth(), 20));
            this.setStringPainted(true);
            this.setString("");
            this.setMaximum(max);
            this.setBorder(BorderFactory.createLineBorder(new Color(177, 177, 177), 2));
            this.setForeground(new Color(38, 220, 71));
            this.setBackground(new Color(34, 128, 45));
            this.setUI(new ProgressBarUI());

            parent.getFrame().add(this, BorderLayout.NORTH);
            parent.getFrame().revalidate();
        }
    }

    public void updatePercent(double percent) {
        update((int) (max * percent));
    }

    public void updatePercent(double percent, String string) {
        this.setString(string);
        update((int) (max * percent));
    }

    public void update(int num) {
        this.setValue(num);
    }

    public void updateString(String s) {
        this.setString(s);
    }

    public void dispose() {
        parent.getFrame().remove(this);
        parent.getFilelister().setEnabled(true);
        parent.getFrame().revalidate();
    }
}
