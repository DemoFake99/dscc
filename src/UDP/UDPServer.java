package UDP;

import java.net.*;
import java.io.*;

public class UDPServer {
    static void main() {
        int port = 5000;
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket(port);
            IO.println("UDP Server Started on port " + port);
            IO.println("Waiting for client data...");

            byte[] receiveBuffer = new byte[1024];

            // 1. Receive packet from client
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            // 2. Read structured data from packet payload
            ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData(), 0, receivePacket.getLength());
            DataInputStream dis = new DataInputStream(bais);

            String operation = dis.readUTF();
            int a = dis.readInt();
            int b = dis.readInt();

            IO.println("Operation: " + operation);
            IO.println("Numbers: " + a + " and " + b);

            String responseMessage;

            // 3. Calculator logic
            switch (operation.toUpperCase()) {
                case "ADD":
                    responseMessage = String.valueOf(a + b);
                    break;
                case "SUB":
                    responseMessage = String.valueOf(a - b);
                    break;
                case "MULT":
                    responseMessage = String.valueOf(a * b);
                    break;
                case "DIV":
                    if (b == 0) {
                        responseMessage = "Cannot divide by zero";
                    } else {
                        responseMessage = String.valueOf(a / b);
                    }
                    break;
                default:
                    responseMessage = "Invalid operation";
                    break;
            }

            // 4. Send response back to the client's address and port
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(responseMessage);
            byte[] sendBuffer = baos.toByteArray();

            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
            socket.send(sendPacket);

            IO.println("Result sent: " + responseMessage);

        } catch (IOException e) {
            IO.println("Server Error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
