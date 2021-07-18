package de.dragon.FTClient.frame;

import de.dragon.FTClient.ftpnet.ApproveActions;
import de.dragon.FTClient.ftpnet.Connector;
import de.dragon.FTClient.ftpnet.Parser;
import de.dragon.FTClient.ftpnet.Upload;
import de.dragon.FTClient.menu.MenuBar;
import de.dragon.FTClient.misc.DropListener;
import de.dragon.UsefulThings.console.Console;
import de.dragon.UsefulThings.dir.DeleteOnExitReqCall;
import de.dragon.UsefulThings.misc.DebugPrinter;
import de.dragon.UsefulThings.ut;
import org.apache.commons.net.ftp.FTPSClient;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
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
    private LoginBar menu;
    private Parser parser;
    private Connector connector;
    private ApproveActions approveActions;
    private Upload upload;
    private DropListener dropTarget;

    private JFrame frame;
    private Console con;

    private JComponent lastPainted;
    private Task task;
    private boolean isInit = false;

    public FTPFrame() throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        con = new Console(false);
        con.setEditable(false);

        //menubar
        menu = new LoginBar(this);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menu, con.getPane());
        splitPane.setDividerLocation(23);
        splitPane.setDividerSize(0);
        splitPane.setBackground(Console.DefaultBackground);

        buildFrame(splitPane);
    }

    public void initFileChooser(LoginDetailsContainer c) throws UnsupportedLookAndFeelException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, BadLocationException {
        //init temp direc
        if (ut.getTempFile("FTPClient", token).exists()) {
            ut.deleteFileRec(ut.getTempFile("FTPClient", token));
        }

        token = c.getHost() + "#" + c.getUser();
        PATH_TO_TEMP = ut.createTempFile("FTPClient", token, true).getAbsolutePath();
        RELATIVE_PATH_TO_TEMP = "FTPClient" + File.separator + token;

        //needs updated ftp file converted to file
        ftpChooser = new JFileChooser(PATH_TO_TEMP);
        ftpChooser.setOpaque(false);
        filelister = (JComponent) ftpChooser.getComponent(2);

        //config ftpchooser
        new ConfigJFileChooser(ftpChooser);

        //login
        con.getPane().getDocument().insertString(con.getPane().getDocument().getLength(), "Connecting...\n", null);

        try {
            connector = new Connector(c.getHost(), c.getUser(), c.getPass());
            con.getPane().getDocument().insertString(con.getPane().getDocument().getLength(), "Connected\n", null);
        } catch (IOException e) {
            printToConsole("Error: Connection failed:", Color.RED);
            if (isInit) {
                printToConsole(connector.getClient().getStatus(), Color.WHITE);
            }
        }

        parser = new Parser(connector, this);
        approveActions = new ApproveActions(parser);
        upload = new Upload(parser);

        //droplistener
        dropTarget = new DropListener(upload);
        ftpChooser.addPropertyChangeListener(parser);
        ftpChooser.addActionListener(approveActions);
        setTask(Task.download);
        filelister.setDropTarget(dropTarget);
        DebugPrinter.println(filelister.getClass().getName());

        //update fileview
        con.getPane().getDocument().insertString(con.getPane().getDocument().getLength(), "Connection fully established\n", null);
        parser.refreshView();

        //JFrame setup
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menu, filelister);
        splitPane.setDividerLocation(23);
        splitPane.setDividerSize(0);

        isInit = true;
        buildFrame(splitPane);
    }

    public void uninit() {
        if (isInit) {
            try {
                connector.getClient().logout();
            } catch (IOException e) {
                e.printStackTrace();
            }

            isInit = false;
            connector = null;
            parser = null;
            upload = null;
            approveActions = null;
            ftpChooser = null;
            filelister = null;
            PATH_TO_TEMP = null;
            RELATIVE_PATH_TO_TEMP = null;
            token = null;
            con.flushConsole();

            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menu, con.getPane());
            splitPane.setDividerLocation(23);
            splitPane.setDividerSize(0);
            splitPane.setBackground(Console.DefaultBackground);

            buildFrame(splitPane);
        }
    }

    private void buildFrame(JComponent c) {
        if (frame == null) {
            frame = new JFrame("SimpleFTP Client");
            frame.setSize(800, 450);
            frame.setLocationRelativeTo(null);
            frame.setBackground(Console.DefaultBackground);
        } else {
            frame.remove(lastPainted);
        }

        frame.add(c, BorderLayout.CENTER);

        lastPainted = c;

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                collectTrashandExit();
            }
        });

        //add Menubar
        frame.setJMenuBar(new MenuBar(this));

        frame.setVisible(true);
        frame.revalidate();
    }

    public void collectTrashandExit() {
        try {
            DeleteOnExitReqCall.collectTrash();
            if (isInit) {
                new File(PATH_TO_TEMP).delete();
                connector.logout();
            }

            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void printToConsole(String s, Color c) {
        if (isInit) {
            con.printColoredTextln(s, c);
        }
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        switch (task) {
            case download -> ftpChooser.setApproveButtonText("Download");
            case delete -> ftpChooser.setApproveButtonText("Delete");
        }

        this.task = task;
    }

    public void criticalError(Exception e) {
        uninit();
        JOptionPane.showMessageDialog(null, "Server connection timeout: Reinitializing components", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public FTPSClient getClient() {
        return connector.getClient();
    }

    public JFileChooser getFtpChooser() {
        return ftpChooser;
    }

    public boolean isInit() {
        return isInit;
    }

    public void refreshView() throws IOException {
        if (isInit) {
            parser.refreshView();
        }
    }

    public void addActionListener(ActionListener listener) {
        ftpChooser.addActionListener(listener);
    }

    public Console getConsole() {
        return con;
    }

    public Upload getUpload() {
        return upload;
    }

}
