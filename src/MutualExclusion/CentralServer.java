package MutualExclusion;

import java.io.*;
import java.net.*;
import java.util.*;

public class CentralServer {
    private static final int PORT = 5000;
    private static final List<ClientHandler> clients = new ArrayList<>();
    private static ClientHandler activeClient;

    static void main() {
        IO.println("[SERVER] Started on port " + PORT);

        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = server.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            IO.println("[SERVER] " + e.getMessage());
        }
    }

    private static synchronized void connect(ClientHandler c) {
        clients.add(c);
        IO.println("[SERVER] Connected: " + c.id);

        if (activeClient == null) {
            activate(c);
        } else {
            IO.println("[SERVER] " + c.id + " is waiting.");
        }
    }

    private static void activate(ClientHandler c) {
        activeClient = c;
        c.send("YOUR_TURN");
        IO.println("[SERVER] Turn assigned to " + c.id);
    }

    private static synchronized void finishTurn(ClientHandler c) {
        if (activeClient != c) return;

        IO.println("[SERVER] Turn finished: " + c.id);
        activeClient = null;

        int index = clients.indexOf(c);
        if (index >= 0 && !clients.isEmpty()) {
            activate(clients.get((index + 1) % clients.size()));
        }
    }

    private static synchronized void disconnect(ClientHandler c) {
        int index = clients.indexOf(c);
        if (index < 0) return;

        boolean wasActive = (activeClient == c);
        clients.remove(c);

        IO.println("[SERVER] Disconnected: " + c.id);

        if (wasActive) {
            activeClient = null;

            if (!clients.isEmpty()) {
                activate(clients.get(index % clients.size()));
            }
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private String id = "Client";

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        synchronized void send(String message) {
            if (out != null) out.println(message);
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                String first = in.readLine();
                if (first == null || !first.startsWith("CONNECT:")) {
                    return;
                }

                id = first.substring("CONNECT:".length());
                connect(this);

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equals("REJECT") ||
                            message.equals("RELEASE")) {
                        finishTurn(this);
                    } else if (message.equals("EXIT")) {
                        break;
                    }
                }
            } catch (IOException e) {
                IO.println("[SERVER] Connection error: " + id);
            } finally {
                disconnect(this);
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
