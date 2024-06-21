package main;

import javax.swing.JLabel;

public class ChatFrame extends AppFrame {
    public ChatFrame() {
        super();
        setupUI();
    }

    private void setupUI() {
        JLabel label = new JLabel("Welcome " + UserInfo.Username);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setBounds(0, 0, 900, 500);
        this.add(label);
    }
}
