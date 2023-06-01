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
                        receiveFile(socket,jRequest.getString("id"));
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

    public static void receiveFile(Socket socket,String id) throws IOException {

        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        File folder = new File("C:\\Users\\astan\\Eighth-Assignment-Steam\\src\\main\\java\\Client\\Downloads\\");
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();

        for (File file:listOfFiles){
            if (file.getName().endsWith(".png")) {
                fileNames.add(file.getName().substring(0,file.getName().length() - 4));
            }
        }

        int i = 1;
        String id1 = id;
        while (fileNames.contains(id)){
            id = id1 + " (" + i + ")";
            i++;
        }

        String filePath = "C:\\Users\\astan\\Eighth-Assignment-Steam\\src\\main\\java\\Client\\Downloads\\" + id + ".png";
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        long size = dataInputStream.readLong();
        byte[] buffer = new byte[1024];
        int bytes = 0;
        long totalBytesRead = 0;
        while (totalBytesRead<size && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size-totalBytesRead))) != -1) {

            bufferedOutputStream.write(buffer, 0, bytes);
            totalBytesRead += bytes;
        }

        System.out.println("Download complete!");
        fileOutputStream.close();
    }
}
