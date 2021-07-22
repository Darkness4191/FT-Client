package de.dragon.FTClient.ftpnet;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import java.io.IOException;

public class Connector {

    private FTPSClient client;

    String user;
    String host;
    String pass;

    public Connector(String host, String user, String pass) throws IOException {
        this.host = host;
        this.user = user;
        this.pass = pass;
        connect(host, user, pass);
    }

    private void connect(String host, String user, String pass) throws IOException {
        client = new FTPSClient();

        client.connect(host);
        client.setFileType(FTPClient.BINARY_FILE_TYPE);
        client.enterLocalPassiveMode();
        client.execPBSZ(0);
        client.execPROT("P");
        client.login(user, pass);
        client.setStrictMultilineParsing(true);

        if(!FTPReply.isPositiveCompletion(client.getReplyCode())) {
            client.disconnect();
            throw new FTPConnectionClosedException(String.format("Server Refused connection (%s)", client.getReplyCode()));
        }

        client.setControlKeepAliveTimeout(300);
        client.setKeepAlive(true);
        client.sendNoOp();

        client.setListHiddenFiles(false);
    }

    public void reconnect() throws IOException {
        if(client != null) {
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

    public FTPSClient getClient() {
        return client;
    }
}
