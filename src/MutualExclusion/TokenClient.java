package MutualExclusion;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.*;

public class TokenClient {
    static void main() {
        Scanner sc = new Scanner(System.in);

        IO.print("Enter Client ID: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty() || id.contains(":")) {
            IO.println("Invalid Client ID.");
            return;
        }

        BlockingQueue<String> messages = new LinkedBlockingQueue<>();

        try (Socket socket = new Socket("127.0.0.1", 5000);
             PrintWriter out = new PrintWriter(
                     socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()))) {

            out.println("CONNECT:" + id);
            IO.println("Connected as " + id);
            IO.println("Waiting for your turn...");

            Thread listener = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        messages.put(msg);
                    }
                } catch (Exception ignored) {
                } finally {
                    messages.offer("SERVER_DISCONNECTED");
                }
            });

            listener.setDaemon(true);
            listener.start();

            while (true) {
                String msg = messages.take();

                if (msg.equals("SERVER_DISCONNECTED")) {
                    IO.println("Server disconnected.");
                    break;
                }

                if (!msg.equals("YOUR_TURN")) continue;

                IO.println("\n--- " + id + " MENU ---");
                IO.println("1. Accept");
                IO.println("2. Reject");
                IO.println("3. Exit");
                IO.print("Enter choice: ");

                String choice = sc.nextLine().trim();

                if (choice.equals("1")) {
                    out.println("ACCEPT:" + id);

                    IO.println(
                            "\n>>> INSIDE CRITICAL SECTION <<<");
                    IO.println(
                            "Press ENTER to exit the Critical Section.");
                    sc.nextLine();

                    out.println("RELEASE");
                    IO.println(
                            "Critical Section released.");
                } else if (choice.equals("2")) {
                    out.println("REJECT");
                    IO.println(
                            "Rejected. Waiting for your next turn.");
                } else if (choice.equals("3")) {
                    out.println("EXIT");
                    IO.println("Client exiting.");
                    break;
                } else {
                    IO.println(
                            "Invalid choice. Your turn is skipped.");
                    out.println("REJECT");
                }

                IO.println("Waiting for your next turn...");
            }

        } catch (Exception e) {
            IO.println("Error: " + e.getMessage());
        }
    }
}