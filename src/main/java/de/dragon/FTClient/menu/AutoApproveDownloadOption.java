package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.ftpnet.Download;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class AutoApproveDownloadOption extends JCheckBoxMenuItem implements ActionListener {

    public AutoApproveDownloadOption() {
        super();
        this.setText("Auto approve downloads");
        this.setState(Download.always_approve);
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Download.always_approve = !Download.always_approve;
        this.setState(Download.always_approve);
    }
}
