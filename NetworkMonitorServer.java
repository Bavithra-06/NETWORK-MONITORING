import java.io.*;
import java.net.*;
import java.util.*;

public class NetworkMonitorServer {

    private static Map<String, String> monitoredData = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server started, waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("Received request: " + request);

                    // Example: Respond with monitored data
                    if (request.equalsIgnoreCase("GET_DATA")) {
                        out.println(getMonitoredData());
                    } else {
                        out.println("Unknown request");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getMonitoredData() {
        // Simulate monitoring data
        monitoredData.put("CPU Usage", "25%");
        monitoredData.put("Memory Usage", "40%");
        monitoredData.put("Network Traffic", "15MB/s");

        StringBuilder data = new StringBuilder();
        for (Map.Entry<String, String> entry : monitoredData.entrySet()) {
            data.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return data.toString();
    }
}