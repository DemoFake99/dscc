package TCP;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class CalculatorClient {
    static void main() {
        try (Socket socket = new Socket("localhost", 5000);
             Scanner scanner = new Scanner(System.in)) {

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            System.out.println("Connected to Calculator Server.");
            System.out.println("Enter equation (e.g., 5 + 3) or type 'bye' to exit:");

            while (true) {
                System.out.print("Enter equation: ");
                String input = scanner.nextLine();

                dos.writeUTF(input);
                if (input.equalsIgnoreCase("bye")) {
                    break;
                }

                String result = dis.readUTF();
                System.out.println("Server Response: " + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
