package de.dragon.FTClient.frame;

import de.dragon.FTClient.async.MasterQueue;
import de.dragon.FTClient.ftpnet.Connector;
import de.dragon.FTClient.ftpnet.Parser;
import de.dragon.FTClient.listeners.BasicTextFieldListener;
import de.dragon.FTClient.listeners.DropListener;
import de.dragon.FTClient.listeners.FileDisplay;
import de.dragon.FTClient.listeners.MainListener;
import de.dragon.FTClient.menu.MenuBar;
import de.dragon.FTClient.menu.popup.FPopupMenu;
import de.dragon.FTClient.menu.popup.SwingUtils;
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

public class FTPFrame extends JFrame {

    public String PATH_TO_TEMP;
    public String token;

    private final String TITLE = "File Transfer Client";

    private MasterQueue masterQueue;

    private JFileChooser ftpChooser;
    private JFileChooser homeChooser;
    private JComponent filelister;
    private LoginBar menu;
    private Parser parser;
    private Connector connector;
    private DropField dropField;
    private JTextField filenameField;

    private Console con;

    private JComponent lastPainted;
    private Task task;
    private boolean isInit = false;

    public FTPFrame() throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);

        masterQueue = new MasterQueue(this);

        con = new Console(false);
        con.setEditable(false);
        con.getPane().setBackground(Console.DefaultBackground);
        con.getPane().setOpaque(true);

        //Init Menubar
        menu = new LoginBar(this);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menu, con.getPane());
        splitPane.setDividerLocation(23);
        splitPane.setDividerSize(0);
        splitPane.setBackground(Color.WHITE);
        splitPane.setOpaque(false);

        buildFrame(splitPane);
    }

    public void initFileChooser(LoginDetailsContainer c) throws UnsupportedLookAndFeelException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, BadLocationException {
        try {
            //Init temp direc
            printToConsoleln("Initializing components...");
            if (ut.getTempFile("FTPClient", token).exists()) {
                ut.deleteFileRec(ut.getTempFile("FTPClient", token));
            }

            token = c.getHost() + "#" + c.getUser();
            String random = "FTC" + new Token(20).encode();

            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                PATH_TO_TEMP = ut.createAbsolutTempFile(ut.merge(new String[]{System.getProperty("user.home"), "AppData", "Local", "Temp", random, token}, File.separator), true).getAbsolutePath();
            } else {
                PATH_TO_TEMP = ut.createAbsolutTempFile(ut.merge(new String[]{System.getProperty("user.home"), "Temp", random, token}, File.separator), true).getAbsolutePath();
            }

            //TODO optimize parent file deletion
            DeleteOnExitReqCall.add(new File(PATH_TO_TEMP).getParentFile());

            DebugPrinter.println(PATH_TO_TEMP);

            //JFileChooser setup
            ftpChooser = new JFileChooser(PATH_TO_TEMP);
            if(System.getProperty("os.name").toLowerCase().contains("windows")) {
                filelister = (JComponent) ftpChooser.getComponent(2);
                JComponent toDisable = (JComponent) getComponent(getComponent(filelister, 2), 2);
                ((JLabel) getComponent(getComponent(toDisable.getParent(), 0), 1)).setText("Selected Files");
                ((JLabel) getComponent(getComponent(toDisable.getParent(), 0), 1)).updateUI();

                filenameField = new JTextField();
                filenameField.addMouseListener(new BasicTextFieldListener(this));
                filenameField.setSize(toDisable.getComponent(1).getSize());
                filenameField.setFont(toDisable.getComponent(1).getFont());
                Component filler1 = toDisable.getComponent(0);
                Component filler2 = toDisable.getComponent(2);
                Component dropdown = toDisable.getComponent(3);
                toDisable.removeAll();
                toDisable.add(filler1);
                toDisable.add(filenameField);
                toDisable.add(filler2);
                toDisable.add(dropdown);

                JList list = SwingUtils.getDescendantOfType(JList.class, ftpChooser, "Enabled", true);
                JPopupMenu popup = list.getComponentPopupMenu();
                list.setComponentPopupMenu(new FPopupMenu(this));

                filelister.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
                FileDisplay fileNameDisplay = new FileDisplay(ftpChooser, filenameField);

            } else if(System.getProperty("os.name").toLowerCase().contains("linux")){
                ftpChooser.getComponent(0).setVisible(false);
                getComponent(ftpChooser.getComponent(1), 0).setVisible(false);
                getComponent(ftpChooser.getComponent(1), 3).setVisible(false);

                filelister = ftpChooser;
            }

            //Config ftpchooser
            ftpChooser.setMultiSelectionEnabled(true);
            ftpChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            ftpChooser.setOpaque(false);

            //Login
            printToConsoleln("Connecting to server...");
            try {
                connector = new Connector(c.getHost(), c.getUser(), c.getPass());
                printToConsoleln("Connection attempt successful");
            } catch (IOException e) {
                printToConsoleln("Error: " + e.getMessage());
                e.printStackTrace();
                con.flushConsole();
                isInit = true;
                criticalError(e);
            }

            printToConsoleln("Building parser");
            parser = new Parser(connector, this);


            //Droplistener setup
            DropListener dropTarget = new DropListener(this);
            ftpChooser.addPropertyChangeListener(parser);
            ftpChooser.addActionListener(new MainListener(parser));
            filelister.setDropTarget(dropTarget);
            DebugPrinter.println(filelister.getClass().getName());

            printToConsoleln("Connection fully established");
            printToConsoleln("Refreshing...");

            //JFrame setup
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menu, filelister);
            splitPane.setDividerLocation(23);
            splitPane.setDividerSize(0);

            isInit = true;

            //Build JFrame
            buildFrame(splitPane);
            setTask(Task.download);

            refreshView(true);
        } catch (Exception e) {
            criticalError(e);
            e.printStackTrace();
        }
    }

    private void buildFrame(JComponent c) {
        if (!isInit) {
            this.setTitle(TITLE);
            this.setSize(800, 450);
            this.setLocationRelativeTo(null);
            this.setBackground(Color.WHITE);

            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    collectTrashandExit();
                }
            });

            //add Menubar
            this.setJMenuBar(new MenuBar(this));

            //droplistener
            dropField = new DropField();
            dropField.setSize(this.getSize());
            dropField.setDropTarget(new DropListener(this));
            this.getRootPane().getLayeredPane().add(dropField, JLayeredPane.PALETTE_LAYER);
        } else {
            this.remove(lastPainted);
        }

        this.add(c, BorderLayout.CENTER);
        lastPainted = c;

        this.setVisible(true);
        this.revalidate();
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
        if (connector != null) {
            try {
                connector.getClient().disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        masterQueue.clearList();

        connector = null;
        parser = null;
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
        isInit = false;
    }

    public void printToConsoleln(String s) {
        try {
            con.getPane().getDocument().insertString(con.getPane().getDocument().getLength(), s + "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        switch (task) {
            case download -> {
                ftpChooser.setApproveButtonText("Download");
                this.setTitle(TITLE + " (download-mode)");
            }
            case delete -> {
                ftpChooser.setApproveButtonText("Delete");
                this.setTitle(TITLE + " (delete-mode)");
            }
        }

        this.task = task;
    }

    public void criticalError(Exception e) {
        e.printStackTrace();
        if(connector != null) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage() + "(" + connector.getClient().getReplyCode() + ")", "Error", JOptionPane.ERROR_MESSAGE);
            try {
                connector.reconnect();
            } catch (IOException ioException) {
                uninit();
                ioException.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            uninit();
        }
    }

    public FTPSClient getClient() {
        return connector.getClient();
    }

    public JFileChooser getFtpChooser() {
        return ftpChooser;
    }

    public JComponent getFilelister() {
        return filelister;
    }

    public boolean isInit() {
        return isInit;
    }

    public void refreshView(boolean preload) throws IOException {
        if (isInit) {
            parser.refreshView(preload);
        }
    }

    public void addActionListener(ActionListener listener) {
        ftpChooser.addActionListener(listener);
    }

    public DropField getDropField() {
        return dropField;
    }

    private Component getComponent(Component component, int i) {
        return ((JComponent) component).getComponent(i);
    }

    public LoginBar getMenu() {
        return menu;
    }

    public MasterQueue getMasterQueue() {
        return masterQueue;
    }

    public JTextField getFilenameField() {
        return filenameField;
    }

    public Parser getParser() {
        return parser;
    }

}
