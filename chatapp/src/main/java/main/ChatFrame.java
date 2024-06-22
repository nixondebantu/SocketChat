package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatFrame extends AppFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private JPanel userListPanel;
    private JScrollPane userListScrollPane;

    ImageIcon userIcon = new ImageIcon(getClass().getResource("/icons/userIcon.png"));

    public ChatFrame() {
        super();
        setupUI();
        connectToServer();
    }

    private void setupUI() {
        this.setLayout(new BorderLayout());

        // User list panel
        userListPanel = new JPanel();
        userListPanel.setBackground(Color.WHITE);
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));

        userListScrollPane = new JScrollPane(userListPanel);
        userListScrollPane.setPreferredSize(new Dimension(245, 450));
        userListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        userListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.add(userListScrollPane, BorderLayout.WEST);

        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        this.add(chatScrollPane, BorderLayout.CENTER);

        // Message input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        inputPanel.add(sendButton, BorderLayout.EAST);

        this.add(inputPanel, BorderLayout.SOUTH);

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome " + UserInfo.Username);
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        this.add(welcomeLabel, BorderLayout.NORTH);

        // Fetch initial user list
        updateUserList(fetchUserListFromServer(UserInfo.UID));
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 8080);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send initial connection message
            out.println("connect");
            out.println(UserInfo.UID);
            out.println(UserInfo.Username);

            // Start a thread to receive messages
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to server: " + e.getMessage());
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            out.println("message");
            out.println(message);
            messageField.setText("");
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equals("userlist")) {
                    // Receive updated user list
                    List<String> users = new ArrayList<>();
                    while (!(message = in.readLine()).equals("end")) {
                        users.add(message);
                    }
                    SwingUtilities.invokeLater(() -> updateUserList(users));
                } else {
                    // Regular chat message
                    final String displayMessage = message;
                    SwingUtilities.invokeLater(() -> chatArea.append(displayMessage + "\n"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> fetchUserListFromServer(int uid) {
        List<String> users = new ArrayList<>();
        try {
            Socket socket = new Socket("localhost", 8080);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("fetchUserList");
            out.println(uid);
            String userData;
            while ((userData = in.readLine()) != null) {
                users.add(userData);
            }

            socket.close();
        } catch (Exception e) {
            System.out.println("Error fetching user list from server: " + e.getMessage());
        }
        return users;
    }

    private void updateUserList(List<String> users) {
        userListPanel.removeAll();
        for (String user : users) {
            JButton userButton = createUserButton(user);
            userListPanel.add(userButton);
            userListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        userListPanel.revalidate();
        userListPanel.repaint();
    }

    private JButton createUserButton(String name) {
        JButton button = new JButton(name);
        button.setIcon(userIcon);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Clicked on user: " + name);
        });
        return button;
    }
}