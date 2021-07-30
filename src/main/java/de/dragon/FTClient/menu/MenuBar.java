package de.dragon.FTClient.menu;

import de.dragon.FTClient.frame.FTPFrame;

import javax.swing.*;

public class MenuBar extends JMenuBar {

    public MenuBar(FTPFrame frame) {
        super();

        JMenu file = new JMenu("File");
        file.add(new UploadFileOption(frame));
        file.add(new DeleteModeOption(frame));
        file.add(new LogoutOption(frame));

        JMenu news = new JMenu("New");
        news.add(new CreateFolderOption(frame));

        JMenu options = new JMenu("Options");
        options.add(new RefreshOption(frame));

        JMenu view = new JMenu("View");
        view.add(new EnableHiddenFilesOption(frame));

        this.add(file);
        this.add(options);
        this.add(news);
        this.add(view);
    }

}
