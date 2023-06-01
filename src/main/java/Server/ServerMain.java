package Server;

import Shared.Response;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;

public class ServerMain {

    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    public static void main(String args[]) throws IOException, SQLException {

        int portNumber = 1234;
        ServerMain server = new ServerMain(portNumber);
        server.start();
    }

    public ServerMain(int portNumber) throws IOException {
        this.serverSocket = new ServerSocket(portNumber);
    }

    public void start() throws SQLException {
        System.out.println("Server started.");
        Connection connection = sql();
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(socket, connection);
                clients.add(handler);
                handler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private Connection connection;

        public ClientHandler(Socket socket, Connection connection) throws IOException {
            this.socket = socket;
            this.connection = connection;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        }

        public void run() {
            Statement statement = null;
            try {
                statement = connection.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            this.out.println(Response.initialMenu());
            String request;
            try {
                while ((request = in.readLine()) != null) {
                    if (!request.equals("null")) {
                        JSONObject jsonRequest = new JSONObject(request);
                        if (jsonRequest.getString("type").equals("exit")){
                            socket.close();
                            clients.remove(this);
                        }
                        else if (jsonRequest.getString("type").equals("download")) {
                            //SendFiles(new JSONObject(request).getString("id"), socket);
                        }
                        String response = Response.responseCreator(jsonRequest, statement);
                        this.out.println(response);
                    }
                }

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    clients.remove(this);
                    statement.close();
                    System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public Connection sql() throws SQLException {

        String url = "jdbc:postgresql://localhost:5432/Steam";
        String user = "postgres";
        String pass = "12345";
        Connection connection = DriverManager.getConnection(url, user, pass);
        System.out.println("Connected to the PostgreSQL database!");

        return connection;
    }
    //public static void SendFiles

}
