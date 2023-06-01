package Client;

import Shared.Request;


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.*;

public class ClientMain {

    public static void main(String[] args) {

        String hostname = "127.0.0.1";
        int port = 1234;

        try {
            Socket socket = new Socket(hostname, port);

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            Scanner scan = new Scanner(System.in);
            String response ;

            while ((response = reader.readLine()) != null) {
                if (!response.equals("null")) {
                    String request = Request.createRequest(new JSONObject(response), scan);
                    JSONObject jRequest = new JSONObject(request);
                    writer.println(request);
                    if (jRequest.getString("type").equals("download")) {
                        //receiveFile(socket,jRequest.getString("id"));
                    }
                }
            }

        } catch (UnknownHostException ex) {
            System. out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
//    public static void receiveFile
}
