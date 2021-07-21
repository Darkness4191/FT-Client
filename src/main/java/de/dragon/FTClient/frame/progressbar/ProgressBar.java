package de.dragon.FTClient.frame.progressbar;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;
import java.awt.*;

public class ProgressBar extends JProgressBar {

    public final double STEPS = 10000D;
    private int max = (int) STEPS;

    private FTPFrame parent;

    public ProgressBar(FTPFrame frame) {
        super();
        this.parent = frame;
        this.setPreferredSize(new Dimension((int) parent.getFrame().getSize().getWidth(), 20));
        this.setStringPainted(true);
        this.setString("");
        this.setBorder(BorderFactory.createLineBorder(new Color(177, 177, 177), 2));
        this.setForeground(new Color(38, 220, 71));
        this.setBackground(new Color(34, 128, 45));
        this.setUI(new ProgressBarUI());

        parent.getFrame().add(this, BorderLayout.NORTH);
        parent.getFrame().revalidate();
    }

    public ProgressBar(FTPFrame frame, int max) {
        this(frame);
        this.max = max;
    }

    public void updatePercent(double percent) {
        update((int) (STEPS * percent));
    }

    public void updatePercent(double percent, String string) {
        this.setString(string);
        update((int) (STEPS * percent));
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
