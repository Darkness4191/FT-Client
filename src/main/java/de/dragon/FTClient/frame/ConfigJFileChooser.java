package de.dragon.FTClient.frame;

import javax.swing.*;
import java.awt.*;

public class ConfigJFileChooser {

    private JFileChooser chooser;

    public ConfigJFileChooser(JFileChooser chooser) {
        this.chooser = chooser;
        chooser.setMultiSelectionEnabled(true);
    }

    private void disableButtons(Container con) {
        for (Component c : con.getComponents()) {

            if (c instanceof JButton) {
                JButton b = (JButton) c;

                if (b.getText() != null && b.getText().equals("Open")) {
                    b.setText("Download");
                }
            } else if (c instanceof Container) {
                disableButtons((Container) c);
            }
        }
    }

}
