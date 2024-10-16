package main;

import java.io.*;
import java.net.*;

import java.util.HashMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(10001)) {
            System.out.println("Server started on port 10001...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static TokenManager tokenManager = new TokenManager();
    private static HashMap<String, User> users = new HashMap<>();

    public String handleRequest(HttpRequest request) {
        if (request.getPath().equals("/users") && request.getMethod().equals("POST")) {
            return handleUserRegistration(request);
        } else if (request.getPath().equals("/login")) {
            return handleLogin(request);
        } else if (request.getPath().equals("/protected")) {
            String token = request.getHeader("Authorization");
            if (tokenManager.isValidToken(token)) {
                return "HTTP/1.1 200 OK\n\nAccess granted!";
            } else {
                return "HTTP/1.1 401 Unauthorized\n\nInvalid token!";
            }
        }
        return "HTTP/1.1 404 Not Found\n\n";
    }

    private String handleUserRegistration(HttpRequest request) {
        String body = request.getBody();
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        String username = jsonObject.get("Username").getAsString();
        String password = jsonObject.get("Password").getAsString();

        if (users.containsKey(username)) {
            return "HTTP/1.1 409 Conflict\n\nUser already exists!";
        }

        users.put(username, new User(username, password));
        return "HTTP/1.1 201 Created\n\nUser registered successfully!";
    }

    private String handleRegister(HttpRequest request) {
        String body = request.getBody();
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        String username = jsonObject.get("Username").getAsString();
        String password = jsonObject.get("Password").getAsString();

        if (users.containsKey(username)) {
            return "HTTP/1.1 409 Conflict\n\nUser already exists!";
        }

        users.put(username, new User(username, password));
        return "HTTP/1.1 201 Created\n\nUser registered successfully!";
    }


    private String handleLogin(HttpRequest request) {
        String body = request.getBody();
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        String username = jsonObject.get("Username").getAsString();
        String password = jsonObject.get("Password").getAsString();

        if (!users.containsKey(username) || !users.get(username).checkPassword(password)) {
            return "HTTP/1.1 401 Unauthorized\n\nInvalid credentials!";
        }

        String token = tokenManager.generateToken(username);
        return "HTTP/1.1 200 OK\n\nLogin successful!\nToken: " + token;
    }

}


class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            HttpRequest request = new HttpRequest(in);
            Server server = new Server();
            String response = server.handleRequest(request);
            out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

