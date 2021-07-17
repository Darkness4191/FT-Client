package de.dragon.FTClient.frame;

public class LoginContainer {

    private String host;
    private String user;
    private String pass;

    public LoginContainer(String host, String user, String pass) {
        this.host = host;
        this.user = user;
        this.pass = pass;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
