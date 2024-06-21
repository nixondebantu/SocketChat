package main;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class AppFrame extends JFrame {
    public AppFrame() {
        initialize();
    }

    private void initialize() {
        this.setTitle("SocketChat");
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/icon.png"));
        this.setIconImage(icon.getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 500);
        this.setResizable(false);
        this.getContentPane().setBackground(new Color(0xF0F3F7));
        this.setLayout(null);
        this.setVisible(true);
    }
}