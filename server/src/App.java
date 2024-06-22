import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class App {
    static Connection connection = null;

    public static void main(String[] args) {
        try {
            // Establish database connection
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/chatapp", "postgres", "root");
            if (connection != null) {
                System.out.println("Connected to the database successfully");
            } else {
                System.out.println("Database connection failed");
                return;
            }

            // Start the server
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server is listening on port 8080");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                // Handle client in a separate thread
                new ClientHandler(clientSocket).start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Read the type of request
                String requestType = in.readLine();

                if ("login".equals(requestType)) {
                    // Read email and password from the client
                    String email = in.readLine();
                    String password = in.readLine();

                    // Validate credentials
                    String userData = validateLogin(email, password);

                    // Send the result back to the client
                    out.println(userData != null ? userData : "null");
                } else if ("fetchUserList".equals(requestType)) {
                    // Read UID from the client
                    String uidStr = in.readLine();
                    int uid = Integer.parseInt(uidStr);

                    // Fetch the user list
                    List<String> userList = fetchUserList(uid);

                    // Send the user list back to the client
                    for (String user : userList) {
                        out.println(user);
                    }
                }

                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            }
        }

        private String validateLogin(String email, String password) {
            try {
                String query = "SELECT uid, username FROM users WHERE usermail = ? AND userpass = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, email);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int uid = resultSet.getInt("uid");
                    String username = resultSet.getString("username");
                    return uid + "," + username;
                }
            } catch (Exception e) {
                System.out.println("Error validating login: " + e.getMessage());
            }
            return null;
        }

        private List<String> fetchUserList(int uid) {
            List<String> userList = new ArrayList<>();
            try {
                String query = "SELECT username FROM users WHERE uid <> ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, uid);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    userList.add(username);
                }
            } catch (Exception e) {
                System.out.println("Error fetching user list: " + e.getMessage());
            }
            return userList;
        }

    }
}
