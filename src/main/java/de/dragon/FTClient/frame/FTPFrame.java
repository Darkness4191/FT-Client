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
import de.dragon.UsefulThings.net.Token;
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
    public String token;

    private final String TITLE = "File Transfer Client";

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
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);

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
        try {
            //init temp direc
            printToConsoleln("Initializing components...");
            if (ut.getTempFile("FTPClient", token).exists()) {
                ut.deleteFileRec(ut.getTempFile("FTPClient", token));
            }

            token = c.getHost() + "#" + c.getUser();
            String realToken = new Token(20).encode();

            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                PATH_TO_TEMP = ut.createAbsolutTempFile(ut.merge(new String[]{System.getProperty("user.home"), "AppData", "Local", "Temp", realToken, token}, File.separator), true).getAbsolutePath();
            } else {
                PATH_TO_TEMP = ut.createAbsolutTempFile(ut.merge(new String[]{System.getProperty("user.home"), "Temp", realToken, token}, File.separator), true).getAbsolutePath();
            }

            //TODO optimize parent file deletion
            DeleteOnExitReqCall.add(new File(PATH_TO_TEMP).getParentFile());

            DebugPrinter.println(ut.merge(new String[]{System.getProperty("user.home"), "Temp", realToken, token}, File.separator));

            //needs updated ftp file converted to file
            ftpChooser = new JFileChooser(PATH_TO_TEMP);
            ftpChooser.setOpaque(false);
            filelister = (JComponent) ftpChooser.getComponent(2);

            //config ftpchooser
            new ConfigJFileChooser(ftpChooser);

            //login
            printToConsoleln("Connecting to FTP server...");

            try {
                connector = new Connector(c.getHost(), c.getUser(), c.getPass());
                printToConsoleln("Connection attempt successful");
            } catch (IOException e) {
                criticalError(e);
                return;
            }

            printToConsoleln("Building parser");
            parser = new Parser(connector, this);
            approveActions = new ApproveActions(parser);
            upload = new Upload(parser);

            //droplistener
            dropTarget = new DropListener(upload);
            ftpChooser.addPropertyChangeListener(parser);
            ftpChooser.addActionListener(approveActions);
            filelister.setDropTarget(dropTarget);
            DebugPrinter.println(filelister.getClass().getName());

            //update fileview
            printToConsoleln("Connection fully established");
            printToConsoleln("Refreshing...");
            parser.refreshView();

            //JFrame setup
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menu, filelister);
            splitPane.setDividerLocation(23);
            splitPane.setDividerSize(0);

            isInit = true;

            buildFrame(splitPane);
            setTask(Task.download);
        } catch (Exception e) {
            criticalError(e);
            e.printStackTrace();
        }
    }

    private void buildFrame(JComponent c) {
        if (frame == null) {
            frame = new JFrame(TITLE);
            frame.setSize(800, 450);
            frame.setLocationRelativeTo(null);
            frame.setBackground(Console.DefaultBackground);

            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    collectTrashandExit();
                }
            });

            //add Menubar
            frame.setJMenuBar(new MenuBar(this));
        } else {
            frame.remove(lastPainted);
        }

        frame.add(c, BorderLayout.CENTER);
        lastPainted = c;

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

    public void uninit() {
        if(connector != null) {
            try {
                connector.getClient().disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        isInit = false;
        connector = null;
        parser = null;
        upload = null;
        approveActions = null;
        ftpChooser = null;
        filelister = null;
        PATH_TO_TEMP = null;
        token = null;

        con.flushConsole();

        System.gc();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menu, con.getPane());
        splitPane.setDividerLocation(23);
        splitPane.setDividerSize(0);
        splitPane.setBackground(Console.DefaultBackground);

        buildFrame(splitPane);
    }

    public void printToConsoleln(String s) throws BadLocationException {
        con.getPane().getDocument().insertString(con.getPane().getDocument().getLength(), s + "\n", null);
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        switch (task) {
            case download -> {
                ftpChooser.setApproveButtonText("Download");
                frame.setTitle(TITLE + " (download-mode)");
            }
            case delete -> {
                ftpChooser.setApproveButtonText("Delete");
                frame.setTitle(TITLE + " (delete-mode)");
            }
        }

        this.task = task;
    }

    public void criticalError(Exception e) {
        JOptionPane.showMessageDialog(null, "Critical Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        uninit();
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
