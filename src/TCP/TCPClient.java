package TCP;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient {

    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    // Establish connection
    public TCPClient(String address, int port) {

        try {
            // Connect to server
            socket = new Socket(address, port);

            IO.println("Connected to Server");

            // Input from server
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            // Output to server
            out = new DataOutputStream(socket.getOutputStream());

            Scanner sc = new Scanner(System.in);

            // Take calculator input
            IO.print("Enter operation (ADD/SUB/MULT/DIV): ");
            String operation = sc.next();

            IO.print("Enter first number: ");
            int a = sc.nextInt();

            IO.print("Enter second number: ");
            int b = sc.nextInt();

            // Send data to server
            out.writeUTF(operation);
            out.writeInt(a);
            out.writeInt(b);

            // Receive result
            String result = in.readUTF();

            IO.println("Result from server: " + result);

            // Close connection
            socket.close();
            in.close();
            out.close();

        } catch (UnknownHostException uh) {
            IO.println(uh);

        } catch (IOException i) {
            IO.println(i);
        }
    }

    static void main() {

        // Same system
        TCPClient client = new TCPClient("127.0.0.1", 5000);

        // For different system, replace with
        // server's IP address:
        // new Client("192.168.1.105", 5000);
    }
}
