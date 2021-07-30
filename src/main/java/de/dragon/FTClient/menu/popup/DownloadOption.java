package de.dragon.FTClient.menu.popup;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.FTClient.ftpnet.Download;
import de.dragon.FTClient.menu.FMenuItem;

import java.awt.event.ActionEvent;

public class DownloadOption extends FMenuItem {

    FTPFrame frame;

    public DownloadOption(FTPFrame frame) {
        super();
        this.frame = frame;
        this.setText("Download");
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(frame.getFtpChooser().getSelectedFile() != null) {
            Download download = new Download(frame.getParser());
            download.addFiles(frame.getParser().getFrame().getFtpChooser().getSelectedFiles());
            frame.getParser().getFrame().getMasterQueue().send(download);
        }
    }
}
