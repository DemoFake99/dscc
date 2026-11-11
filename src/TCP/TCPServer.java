package TCP;

import java.net.*;
import java.io.*;

public class TCPServer {

    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    public TCPServer(int port) {

        try {
            // Create server socket
            serverSocket = new ServerSocket(port);

            IO.println("Server Started");
            IO.println("Waiting for a client...");

            // Accept client connection
            socket = serverSocket.accept();

            IO.println("Client Accepted");

            // Input from client
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            // Output to client
            out = new DataOutputStream(socket.getOutputStream());

            // Receive operation
            String operation = in.readUTF();

            // Receive numbers
            int a = in.readInt();
            int b = in.readInt();

            IO.println("Operation: " + operation);
            IO.println("Numbers: " + a + " and " + b);

            int result;

            // Calculator logic
            switch (operation.toUpperCase()) {

                case "ADD":
                    result = a + b;
                    break;

                case "SUB":
                    result = a - b;
                    break;

                case "MULT":
                    result = a * b;
                    break;

                case "DIV":
                    if (b == 0) {
                        out.writeUTF("Cannot divide by zero");
                        socket.close();
                        return;
                    }

                    result = a / b;
                    break;

                default:
                    out.writeUTF("Invalid operation");
                    socket.close();
                    return;
            }

            // Send result to client
            out.writeUTF(String.valueOf(result));

            IO.println("Result sent: " + result);

            // Close connection
            socket.close();
            in.close();
            out.close();
            serverSocket.close();

        } catch (IOException i) {
            IO.println(i);
        }
    }

    static void main() {
        TCPServer s = new TCPServer(5000);
    }
}
