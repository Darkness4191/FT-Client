package de.dragon.FTClient.frame.progressbar;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class ProgressBar extends JProgressBar {

    private int max = 500;

    private FTPFrame frame;
    private boolean isInit = false;

    public ProgressBar(FTPFrame frame) {
        super();
        this.frame = frame;
    }

    public ProgressBar(FTPFrame frame, int max) {
        this(frame);
        this.max = max;
    }

    public void init() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                if(!isInit) {
                    isInit = true;
                    this.setComponentPopupMenu(new CancelPacketMenu(frame.getMasterQueue()));
                    this.setPreferredSize(new Dimension((int) frame.getSize().getWidth(), 19));
                    this.setStringPainted(true);
                    this.setString("");
                    this.setMaximum(max);
                    this.setBorder(BorderFactory.createLineBorder(new Color(177, 177, 177), 2));
                    this.setForeground(new Color(38, 220, 71));
                    this.setBackground(new Color(34, 128, 45));
                    this.setUI(new ProgressBarUI());

                    frame.add(this, BorderLayout.NORTH);
                    frame.revalidate();
                }
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
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
        frame.remove(this);
        frame.revalidate();
    }
}
