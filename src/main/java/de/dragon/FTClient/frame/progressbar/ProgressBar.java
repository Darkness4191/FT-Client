package de.dragon.FTClient.frame.progressbar;

import javax.swing.*;
import java.awt.*;

public class ProgressBar extends JProgressBar {

    public final double STEPS = 5000D;
    private int max = (int) STEPS;

    private JFrame parent;

    public ProgressBar(JFrame frame) {
        super();
        this.parent = frame;
        this.setPreferredSize(new Dimension((int) frame.getSize().getWidth(), 20));
        this.setStringPainted(true);
        this.setString("");
        this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        parent.add(this, BorderLayout.SOUTH);
        parent.revalidate();
    }

    public ProgressBar(JFrame frame, int max) {
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

    public void dispose() {
        parent.remove(this);
        parent.revalidate();
    }

}
