package de.dragon.FTClient.ftpnet;

import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import java.io.IOException;

public class Connector {

    private FTPSClient client;

    public Connector(String host, String user, String pass) throws IOException {
        client = new FTPSClient();

        client.connect(host);
        client.enterLocalPassiveMode();
        client.execPBSZ(0);
        client.execPROT("P");
        client.login(user, pass);

        client.setControlKeepAliveTimeout(300);
        client.setKeepAlive(true);
        client.sendNoOp();

        client.setListHiddenFiles(false);

        if(!FTPReply.isPositiveCompletion(client.getReplyCode())) {
            client.disconnect();
            throw new FTPConnectionClosedException(String.format("FTP server connection refused. (%s)", client.getReplyCode()));
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
