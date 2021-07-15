package de.dragon.FTClient.info;

import de.dragon.UsefulThings.events.ProgressChangeEvent;
import de.dragon.UsefulThings.events.listeners.ProgressListener;

import javax.swing.*;
import java.awt.*;

public class ProgressView {

    //TODO delete class

    private JProgressBar progressBar;
    private JFrame frame;
    private int steps = 1000;

    public ProgressView() {
        this(true);
    }

    public ProgressView(boolean visible) {
        JPanel panel = new JPanel();
        panel.setSize(400, 200);
        progressBar = new JProgressBar(0, steps);
        progressBar.setSize(400, 200);
        progressBar.setValue(1);
        progressBar.setStringPainted(false);
        panel.add(progressBar);

        frame = new JFrame();
        frame.setTitle("Progress");
        frame.setVisible(visible);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.add(panel, BorderLayout.CENTER);

        frame.validate();
    }

    public void setValue(double percent) {
        progressBar.setValue((int) (percent * steps));
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JFrame getFrame() {
        return frame;
    }
}
