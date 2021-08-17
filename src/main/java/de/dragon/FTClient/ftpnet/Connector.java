package de.dragon.FTClient.ftpnet;

import org.apache.commons.net.ftp.*;

import javax.net.ssl.SSLException;
import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Connector {

    private FTPClient client;

    private Thread noop;

    String user;
    String host;
    String pass;

    public Connector(String host, String user, String pass) throws IOException {
        this.host = host;
        this.user = user;
        connect(host, user, pass);

        noop = new Thread(() -> {
            while(client.isConnected()) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    client.sendNoOp();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                throw new FTPConnectionClosedException("Planned exception due to user input");
            }
            client.connect(host);
        }

        client.setFileType(FTP.BINARY_FILE_TYPE);
        client.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE);
        client.setTcpNoDelay(true);
        client.enterLocalPassiveMode();
        client.login(user, pass);

        if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
            client.disconnect();
            throw new FTPConnectionClosedException(String.format("Server Refused connection (%s)", client.getReplyCode()));
        }

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
