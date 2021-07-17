package de.dragon.FTClient.frame;

import javax.swing.*;

public class UserApproveDownload {

    public static int ask() {
        Object[] options = {"Yes", "Yes, don't ask me again", "No"};

        return JOptionPane.showOptionDialog(null,
                "Do you want to download that ressource from the server?",
                "Approve Download",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[0]);

    }

}
