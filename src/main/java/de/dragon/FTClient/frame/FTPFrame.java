package de.dragon.FTClient.frame;

import de.dragon.FTClient.ftpnet.Connector;
import de.dragon.FTClient.ftpnet.Download;
import de.dragon.FTClient.ftpnet.Parser;
import de.dragon.FTClient.ftpnet.Upload;
import de.dragon.FTClient.misc.DropListener;
import de.dragon.UsefulThings.console.Console;
import de.dragon.UsefulThings.dir.DeleteOnExitReqCall;
import de.dragon.UsefulThings.misc.DebugPrinter;
import de.dragon.UsefulThings.ut;

import javax.swing.*;
import javax.swing.plaf.basic.BasicFileChooserUI;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class FTPFrame {

    public String PATH_TO_TEMP;
    public String RELATIVE_PATH_TO_TEMP;
    public String token;

    private JFileChooser ftpChooser;
    private JFileChooser homeChooser;
    private JComponent filelister;
    private Menubar menu;
    private Parser parser;
    private Connector connector;
    private Download download;
    private Upload upload;
    private DropListener dropTarget;

    private JFrame frame;

    public FTPFrame(String host, String user, String pass) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        DebugPrinter.setPrint(true);

        //init temp direc
        if(ut.getTempFile("FTPClient", token).exists()) {
            ut.deleteFileRec(ut.getTempFile("FTPClient", token));
        }

        token = host + "#" + user;
        PATH_TO_TEMP = ut.createTempFile("FTPClient", token, true).getAbsolutePath();
        RELATIVE_PATH_TO_TEMP = "FTPClient" + File.separator + token;

        //needs updated ftp file converted to file
        ftpChooser = new JFileChooser(PATH_TO_TEMP);
        ftpChooser.setOpaque(false);

        //menubar
        menu = new Menubar();

        //config ftpchooser
        new ConfigJFileChooser(ftpChooser);

        //login
        connector = new Connector(host, user, pass);
        parser = new Parser(connector, this);
        download = new Download(parser);

        //droplistener
        upload = new Upload(parser);
        filelister = (JComponent) ftpChooser.getComponent(2);
        dropTarget = new DropListener(upload);
        ftpChooser.addPropertyChangeListener(parser);
        ftpChooser.addActionListener(download);
        filelister.setDropTarget(dropTarget);
        DebugPrinter.println(filelister.getClass().getName());

        //update fileview
        parser.refreshView();

        //JFrame setup
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menu, filelister);
        splitPane.setDividerLocation(0.50);
        splitPane.setDividerSize(0);
        frame = new JFrame("FTClient");
        frame.setSize(800, 450);
        frame.setLocationRelativeTo(null);
        frame.setBackground(Console.DefaultBackground);
        frame.add(splitPane, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                collectTrashandExit();
            }
        });

        frame.setVisible(true);
        frame.revalidate();
    }

    public void collectTrashandExit() {
        DeleteOnExitReqCall.collectTrash();
        new File(PATH_TO_TEMP).delete();
        connector.logout();

        System.exit(0);
    }

    public JFileChooser getFtpChooser() {
        return ftpChooser;
    }

    public void addActionListener(ActionListener listener) {
        ftpChooser.addActionListener(listener);
    }

}
