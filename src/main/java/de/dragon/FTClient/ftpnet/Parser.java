package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.FTPFrame;
import de.dragon.UsefulThings.misc.DebugPrinter;
import de.dragon.UsefulThings.ut;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

public class Parser implements PropertyChangeListener {

    private Connector connector;
    private FTPFrame frame;

    private String currentDir;
    private AsyncParser asyncParser;

    public Parser(Connector connector, FTPFrame frame) {
        this.connector = connector;
        this.frame = frame;

        asyncParser = new AsyncParser(this);
        currentDir = frame.PATH_TO_TEMP;
        asyncParser.getAlready_build().add(currentDir);
    }

    public File parseFile(FTPFile file, String path) throws IOException {
        return parseFile(file.getName(), path, file.isDirectory());
    }

    protected File parseFile(String filename, String path, boolean isDirectory) throws IOException {

        String pathtoFile = path.contains(filename) ? path : path + File.separator + filename;
        File poss = new File(pathtoFile);

        if (poss.exists()) {
            return poss;
        } else {
            return ut.createAbsolutTempFile(pathtoFile, isDirectory);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        DebugPrinter.println("Property change: " + evt.getPropertyName());

        if (evt.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
            File f = (File) evt.getNewValue();

            if (f.getAbsolutePath().equals(new File(frame.PATH_TO_TEMP).getParentFile().getAbsolutePath()) || !f.getAbsolutePath().contains(frame.PATH_TO_TEMP)) {
                File abs = new File(frame.PATH_TO_TEMP);
                frame.getFtpChooser().setCurrentDirectory(abs);
                currentDir = abs.getAbsolutePath();
            } else if (f.getAbsolutePath().contains(frame.PATH_TO_TEMP)) {
                try {

                    if (conainsName(currentDir, f.getName()) && !f.getName().equals("^^^")) {
                        DebugPrinter.println("Status: Changing to new directory " + f.getName());
                        currentDir += File.separator + f.getName();
                        refreshView();
                    } else if (new File(currentDir).getParentFile().getAbsolutePath().contains(frame.token) && f.getName().equals("^^^")) {
                        DebugPrinter.println("Status: Changing to parent directory " + f.getName());
                        currentDir = new File(currentDir).getParentFile().getAbsolutePath();
                        refreshView(false);
                    }

                } catch (IOException e) {
                    frame.criticalError(e);
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean conainsName(String folder, String name) {
        for (File c : new File(folder).listFiles()) {
            if (c.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void refreshView() throws IOException {
        refreshView(true);
    }

    public void refreshView(boolean preload) throws IOException {
        if (currentDir.contains(frame.PATH_TO_TEMP)) {
            asyncParser.addToHighPrio(new Data(currentDir, preload));

            frame.getFtpChooser().setCurrentDirectory(new File(currentDir));
            frame.getFtpChooser().rescanCurrentDirectory();
        }
    }

    public Connector getConnector() {
        return connector;
    }

    public FTPFrame getFrame() {
        return frame;
    }

    public String getCurrentDirOnServer() {
        String fromServer = currentDir.replace(frame.PATH_TO_TEMP, "").replace(File.separator, "/");
        if(fromServer.equals("")) {
            fromServer = "/";
        }
        return fromServer;
    }

    public String getPathToFileOnServer(String path) {
        String pathonServer = currentDir.replace(frame.PATH_TO_TEMP, "").replace(File.separator, "/");
        if(pathonServer.equals("")) {
            pathonServer = "/";
        }
        pathonServer += pathonServer.endsWith("/") ? path : "/" + path;
        return pathonServer;
    }

    public String convertToFileOnServer(String path) {
        String pathonServer = path.replace(frame.PATH_TO_TEMP, "").replace(File.separator, "/");
        if(pathonServer.equals("")) {
            pathonServer = "/";
        }
        return pathonServer;
    }
}
