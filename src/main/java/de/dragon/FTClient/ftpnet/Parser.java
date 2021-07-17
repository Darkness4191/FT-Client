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
import java.util.ArrayList;

public class Parser implements PropertyChangeListener {

    private Connector connector;
    private FTPFrame frame;

    private String currentDir;
    private ArrayList<String> downloaded = new ArrayList<>();

    public Parser(Connector connector, FTPFrame frame) {
        this.connector = connector;
        this.frame = frame;

        currentDir = frame.PATH_TO_TEMP;
    }

    public File parseFile(FTPFile file) throws IOException {
        return parseFile(file.getName(), file.isDirectory());
    }

    private File parseFile(String filename, boolean isDirectory) throws IOException {

        File poss = new File(currentDir.contains(filename) ? currentDir : currentDir + File.separator + filename);

        if(poss.exists()) {
            DebugPrinter.println(String.format("Returning temp " + (isDirectory ? "folder" : "file") + " at %s", currentDir.contains(filename) ? currentDir : currentDir + File.separator + filename));
            return poss;
        } else {
            DebugPrinter.println(String.format("Creating temp " + (isDirectory ? "folder" : "file") + " at %s", currentDir.contains(filename) ? currentDir : currentDir + File.separator + filename));
            return ut.createAbsolutTempFile(currentDir.contains(filename) ? currentDir : currentDir + File.separator + filename, isDirectory);
        }
    }

    public FTPFile parseFTPFileBack(File file)  {
        try {
            for(FTPFile c : connector.getClient().listFiles()) {
                if(c.getName().equals(file.getName())) {
                    return c;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        DebugPrinter.println("Property change: " + evt.getPropertyName());

        if (evt.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
            File f = (File) evt.getNewValue();

            if (f.getAbsolutePath().equals(new File(frame.PATH_TO_TEMP).getParentFile().getAbsolutePath()) || !f.getAbsolutePath().contains(frame.token)) {
                File abs = new File(frame.PATH_TO_TEMP);
                frame.getFtpChooser().setCurrentDirectory(abs);
                currentDir = abs.getAbsolutePath();
            } else {
                try {
                    String s = connector.getClient().printWorkingDirectory();

                    if (conainsName(currentDir, f.getName()) && !f.getName().equals("^^^")) {
                        DebugPrinter.println("Status: Changing to new directory " + f.getName());
                        connector.getClient().changeWorkingDirectory(connector.getClient().printWorkingDirectory() + "/" + f.getName());
                        currentDir += File.separator + f.getName();
                        refreshView();
                        parseFile("^^^", true);
                    } else if(new File(currentDir).getParentFile().getAbsolutePath().contains(frame.token) && f.getName().equals("^^^")){
                        DebugPrinter.println("Status: Changing to parent directory " + f.getName());
                        connector.getClient().changeToParentDirectory();
                        currentDir = new File(currentDir).getParentFile().getAbsolutePath();
                        refreshView();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean conainsName(String folder, String name) {
        for(File c : new File(folder).listFiles()) {
            if(c.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean conainsName(FTPFile[] files, String name) {
        for(FTPFile c : files) {
            if(c.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void refreshView() throws IOException {
        if(currentDir.contains(frame.PATH_TO_TEMP)) {
            File current = new File(currentDir);

            for(File c : current.listFiles()) {
                if(!conainsName(connector.getClient().listFiles(), c.getName())) {
                    ut.deleteFileRec(c);
                }
            }

            for (FTPFile c : connector.getClient().listFiles()) {
                parseFile(c);
            }

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
}
