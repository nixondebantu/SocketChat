package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends AppFrame implements ActionListener {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton submitButton;

    public LoginFrame() {
        super();
        setupUI();
    }

    private void setupUI() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/icon.png"));
        JLabel welcomeLabel = createWelcomeLabel(icon);
        this.add(welcomeLabel);

        JLabel loginLabel = createLoginLabel();
        this.add(loginLabel);

        this.add(createFieldLabel("Password:", 270));
        this.add(createFieldLabel("Email:", 210));

        emailField = createEmailField();
        this.add(emailField);

        passwordField = createPasswordField();
        this.add(passwordField);

        submitButton = createSubmitButton();
        submitButton.addActionListener(this);
        this.add(submitButton);

        JButton registerButton = createRegisterButton();
        this.add(registerButton);
    }

    private JLabel createLoginLabel() {
        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Times New Roman", Font.ITALIC, 40));
        loginLabel.setVerticalAlignment(JLabel.CENTER);
        loginLabel.setHorizontalAlignment(JLabel.CENTER);
        loginLabel.setBounds(450, 0, 450, 200);
        return loginLabel;
    }

    private JLabel createFieldLabel(String title, int y) {
        JLabel label = new JLabel(title);
        label.setBounds(475, y, 400, 10);
        return label;
    }

    private JLabel createWelcomeLabel(ImageIcon icon) {
        JLabel welcomeLabel = new JLabel("SocketChat");
        welcomeLabel.setIcon(icon);
        welcomeLabel.setHorizontalTextPosition(JLabel.CENTER);
        welcomeLabel.setVerticalTextPosition(JLabel.BOTTOM);
        welcomeLabel.setVerticalAlignment(JLabel.CENTER);
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 32));
        welcomeLabel.setBounds(0, 0, 450, 400);
        return welcomeLabel;
    }

    private JTextField createEmailField() {
        JTextField emailField = new JTextField();
        emailField.setBounds(475, 225, 300, 35);
        emailField.setFont(new Font("Arial", Font.PLAIN, 18));
        emailField.setForeground(Color.BLACK);
        emailField.setBackground(Color.WHITE);
        return emailField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(475, 285, 300, 35);
        passwordField.setForeground(Color.BLACK);
        passwordField.setBackground(Color.WHITE);
        return passwordField;
    }

    private JButton createSubmitButton() {
        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(500, 330, 150, 30);
        submitButton.setFont(new Font("Arial", Font.BOLD, 18));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(0x3797D4));
        return submitButton;
    }

    private JButton createRegisterButton() {
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(680, 330, 150, 30);
        registerButton.setFont(new Font("Arial", Font.BOLD, 18));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(Color.GRAY);
        return registerButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            try (Socket socket = new Socket("localhost", 8080);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Send email and password to the server
                out.println(email);
                out.println(password);

                // Read the response from the server
                String response = in.readLine();
                if (!"null".equals(response)) {
                    String[] userData = response.split(",");
                    String uid = userData[0];
                    String username = userData[1];
                    System.out.println("Login successful. UID: " + uid + ", Username: " + username);
                    UserInfo.UID = Integer.parseInt(uid);
                    UserInfo.Username = username;
                    JOptionPane.showMessageDialog(this,
                            "Login successful. Welcome " + username + " !");

                    // Dispose the LoginFrame and open ChatFrame
                    this.dispose();
                    new ChatFrame();
                } else {
                    System.out.println("Login failed");
                    JOptionPane.showMessageDialog(this, "Login failed. Please check your credentials.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while connecting to the server.");
            }
        }
    }
}
