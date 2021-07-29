package de.dragon.FTClient.frame;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.dragon.FTClient.listeners.BasicListenerConfig;
import de.dragon.FTClient.listeners.PasswordFieldKeyListener;
import de.dragon.FTClient.listeners.TextFieldKeyListener;
import de.dragon.UsefulThings.ut;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class LoginBar extends JPanel implements ActionListener, Runnable {

    private JPasswordField passwordField;
    private JTextField textField;
    private JTextField hostField;

    private FTPFrame parent;

    private ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private ArrayBlockingQueue<String> q = new ArrayBlockingQueue<>(2);

    private int statePassword;

    public LoginBar(FTPFrame parent) throws IOException {
        this.parent = parent;
        this.setBackground(Color.WHITE);
        this.setOpaque(true);

        GridLayout layout = new GridLayout();
        layout.setColumns(4);
        layout.setRows(1);
        setLayout(layout);

        passwordField = new JPasswordField();
        textField = new JTextField();
        hostField = new JTextField();

        textField.setFont(textField.getFont().deriveFont(12f));
        hostField.setFont(hostField.getFont().deriveFont(12f));
        passwordField.setFont(passwordField.getFont().deriveFont(12f));

        //adding listeners
        TextFieldKeyListener listenerTextField = new TextFieldKeyListener(textField, "username", parent);
        TextFieldKeyListener listenerHostField = new TextFieldKeyListener(hostField, "host IP", parent);
        PasswordFieldKeyListener listenerPassField= new PasswordFieldKeyListener(passwordField, "password", parent);

        BasicListenerConfig.configAll(textField, listenerTextField);
        BasicListenerConfig.configAll(hostField, listenerHostField);
        BasicListenerConfig.configAll(passwordField, listenerPassField);

        JButton loginButton = new JButton("Log In");
        loginButton.setFont(hostField.getFont().deriveFont(12f));
        loginButton.setBackground(UIManager.getColor("TextField.background"));
        loginButton.setUI(new ButtonUI());
        loginButton.addActionListener(this);
        loginButton.setFocusPainted(false);
        loginButton.setForeground(UIManager.getColor("TextField.foreground"));

        if(ut.getTempFile("FTPClient", "login_details.save").exists()) {
            JsonElement json = new JsonParser().parse(new FileReader(ut.getTempFile("FTPClient", "login_details.save")));

            listenerHostField.changeToNormal();
            listenerTextField.changeToNormal();

            //Get the content of the first map
            String host = json.getAsJsonObject().get("host").getAsString();
            String user = json.getAsJsonObject().get("user").getAsString();

            //Check passwordstate
            if(json.getAsJsonObject().get("state") != null) {
                statePassword = json.getAsJsonObject().get("state").getAsInt();
            } else {
                statePassword = PasswordState.NOT_SAVED;
            }

            if(statePassword == PasswordState.SAVED) {
                listenerPassField.changeToNormal();
                passwordField.setText(json.getAsJsonObject().get("pass").getAsString());
            }

            hostField.setText(host);
            textField.setText(user);
        }

        hostField.setBorder(new GenericBorder());
        textField.setBorder(new GenericBorder());
        passwordField.setBorder(new GenericBorder());
        loginButton.setBorder(new GenericBorder());

        textField.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        hostField.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        passwordField.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        add(hostField);
        add(textField);
        add(passwordField);
        add(loginButton);

        threadPool.execute(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        event();
    }

    public void event() {
        String s = new String(passwordField.getPassword());

        if(!textField.getText().equals("") && !hostField.getText().equals("") && !s.equals("")) {
            try {
                q.put(s);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        } else {
            parent.printToConsoleln("Error: Please specify host, username and password for the ftp Server");
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Login");
        while(true) {
            try {
                String s = q.take();

                parent.printToConsoleln("Login information prepared");
                LoginDetailsContainer container = new LoginDetailsContainer(hostField.getText(), textField.getText(), s, statePassword);
                parent.initFileChooser(container);

                if(statePassword == 0) {
                    int ans = JOptionPane.showOptionDialog(parent, "Save password locally?", "Login", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Yes", "No, don't ask me again", "No"}, "Yes");

                    if(ans == JOptionPane.YES_OPTION) {
                        container.setState(PasswordState.SAVED);
                    } else if(ans == JOptionPane.NO_OPTION) {
                        container.setState(PasswordState.DONT_ASK_AGAIN);
                        container.setPass("");
                    } else {
                        container.setPass("");
                    }
                }

                Gson gson = new Gson();
                String jsons = gson.toJson(container);
                ut.saveInfoToAppdata("FTPClient", "login_details", jsons);
                q.clear();
            } catch (UnsupportedLookAndFeelException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | InterruptedException | BadLocationException unsupportedLookAndFeelException) {
                parent.uninit();
                unsupportedLookAndFeelException.printStackTrace();
            }
        }
    }
}
