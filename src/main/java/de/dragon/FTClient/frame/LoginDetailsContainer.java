package de.dragon.FTClient.frame;

public class LoginDetailsContainer {

    private String host;
    private String user;
    private String pass;
    private int state;

    public LoginDetailsContainer(String host, String user, String pass, int state) {
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.state = state;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
