package de.dragon.FTClient.frame;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.dragon.FTClient.misc.PasswordFieldKeyListener;
import de.dragon.FTClient.misc.TextFieldKeyListener;
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

    public LoginBar(FTPFrame parent) throws IOException {
        this.parent = parent;

        GridLayout layout = new GridLayout();
        layout.setColumns(4);
        layout.setRows(1);
        setLayout(layout);

        passwordField = new JPasswordField();
        textField = new JTextField();
        hostField = new JTextField();

        textField.setFont(textField.getFont().deriveFont(11f));
        hostField.setFont(hostField.getFont().deriveFont(11f));
        passwordField.setFont(passwordField.getFont().deriveFont(11f));

        //adding listeners
        TextFieldKeyListener listenerTextField = new TextFieldKeyListener(textField, "username", parent);
        TextFieldKeyListener listenerHostField = new TextFieldKeyListener(hostField, "host IP", parent);
        PasswordFieldKeyListener listenerPassField= new PasswordFieldKeyListener(passwordField, "password", parent);

        textField.addKeyListener(listenerTextField);
        textField.addFocusListener(listenerTextField);
        textField.addMouseListener(listenerTextField);
        hostField.addKeyListener(listenerHostField);
        hostField.addFocusListener(listenerHostField);
        hostField.addMouseListener(listenerHostField);
        passwordField.addKeyListener(listenerPassField);
        passwordField.addFocusListener(listenerPassField);
        passwordField.addMouseListener(listenerPassField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        loginButton.setFocusPainted(false);

        if(ut.getTempFile("FTPClient", "login_details.save").exists()) {
            JsonElement json = new JsonParser().parse(new FileReader(ut.getTempFile("FTPClient", "login_details.save")));

            listenerHostField.changeToNormal();
            listenerTextField.changeToNormal();

            //Get the content of the first map
            String host = json.getAsJsonObject().get("host").getAsString();
            String user = json.getAsJsonObject().get("user").getAsString();

            hostField.setText(host);
            textField.setText(user);
        }

        hostField.setBorder(new GenericBorder());
        textField.setBorder(new GenericBorder());
        passwordField.setBorder(new GenericBorder());

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
        String s = new String(passwordField.getPassword());

        if(!textField.getText().equals("") && !hostField.getText().equals("") && !s.equals("")) {
            try {
                q.put(s);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        } else {
            parent.getConsole().printColoredTextln("Error: Please specify host, username and password for the ftp Server", Color.RED);
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                String s = q.take();

                parent.getConsole().getPane().getDocument().insertString(parent.getConsole().getPane().getDocument().getLength(), "Login information prepared", null);
                LoginDetailsContainer container = new LoginDetailsContainer(hostField.getText(), textField.getText(), s);
                parent.initFileChooser(container);
                container.setPass("");

                Gson gson = new Gson();
                String jsons = gson.toJson(container);
                ut.saveInfoToAppdata("FTPClient", "login_details", jsons);
            } catch (UnsupportedLookAndFeelException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | InterruptedException | BadLocationException unsupportedLookAndFeelException) {
                parent.uninit();
                unsupportedLookAndFeelException.printStackTrace();
            }
        }
    }
}
