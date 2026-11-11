package UDP;

import java.io.*;
import java.net.*;
import java.util.*;

public class UDPClient {
    static void main() {
        String serverAddress = "127.0.0.1";
        int serverPort = 5000;
        DatagramSocket socket = null;
        Scanner sc = new Scanner(System.in);

        try {
            socket = new DatagramSocket(); // Binds to any available local port
            InetAddress address = InetAddress.getByName(serverAddress);

            // Input calculator values
            IO.print("Enter operation (ADD/SUB/MULT/DIV): ");
            String operation = sc.next();

            IO.print("Enter first number: ");
            int a = sc.nextInt();

            IO.print("Enter second number: ");
            int b = sc.nextInt();

            // 1. Pack data into byte stream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(operation);
            dos.writeInt(a);
            dos.writeInt(b);
            byte[] sendData = baos.toByteArray();

            // 2. Send datagram packet to server
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, serverPort);
            socket.send(sendPacket);

            // 3. Receive result packet from server
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            // 4. Extract result string
            ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData(), 0, receivePacket.getLength());
            DataInputStream dis = new DataInputStream(bais);
            String result = dis.readUTF();

            IO.println("Result from server: " + result);

        } catch (UnknownHostException uh) {
            IO.println("Host error: " + uh.getMessage());
        } catch (IOException e) {
            IO.println("IO Error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            sc.close();
        }
    }
}
