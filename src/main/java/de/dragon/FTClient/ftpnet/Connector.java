package de.dragon.FTClient.ftpnet;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import javax.net.ssl.SSLException;
import javax.swing.*;
import java.io.IOException;

public class Connector {

    private FTPClient client;

    String user;
    String host;
    String pass;

    public Connector(String host, String user, String pass) throws IOException {
        this.host = host;
        this.user = user;
        connect(host, user, pass);
    }

    private void connect(String host, String user, String pass) throws IOException {
        try {
            FTPSClient SSLClient = new FTPSClient();

            SSLClient.connect(host);
            SSLClient.execPBSZ(0);
            SSLClient.execPROT("P");

            client = SSLClient;
        } catch (SSLException e) {
            client = new FTPClient();
            int reply = JOptionPane.showConfirmDialog(null, "The FTP server which you are connecting to likely uses no encryption. Do you want to connect anyways?", "Login", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (reply != JOptionPane.YES_OPTION) {
                throw new FTPConnectionClosedException("Planned Exception");
            }
            client.connect(host);
        }

        client.setFileType(FTPClient.BINARY_FILE_TYPE);
        client.enterLocalPassiveMode();
        client.login(user, pass);
        client.setStrictMultilineParsing(true);

        if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
            client.disconnect();
            throw new FTPConnectionClosedException(String.format("Server Refused connection (%s)", client.getReplyCode()));
        }

        client.setControlKeepAliveTimeout(300);
        client.setKeepAlive(true);
        client.sendNoOp();
        client.setListHiddenFiles(false);
    }

    public void reconnect() throws IOException {
        if (client != null) {
            connect(host, user, pass);
        }
    }

    public void logout() {
        try {
            client.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FTPClient getClient() {
        return client;
    }
}
