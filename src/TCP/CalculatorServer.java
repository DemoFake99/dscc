package TCP;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class CalculatorServer {
    static void main() {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            IO.println("Server is running and waiting for client on port 5000...");

            Socket socket = serverSocket.accept();
            IO.println("Client connected!");

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String input = dis.readUTF();
                if (input.equalsIgnoreCase("bye")) {
                    IO.println("Client disconnected.");
                    break;
                }

                IO.println("Equation received: " + input);
                double result = 0;

                try {
                    StringTokenizer st = new StringTokenizer(input);
                    double oprnd1 = Double.parseDouble(st.nextToken());
                    String operation = st.nextToken();
                    double oprnd2 = Double.parseDouble(st.nextToken());

                    switch (operation) {
                        case "+": result = oprnd1 + oprnd2; break;
                        case "-": result = oprnd1 - oprnd2; break;
                        case "*": result = oprnd1 * oprnd2; break;
                        case "/":
                            if(oprnd2 == 0) throw new ArithmeticException("Division by zero");
                            result = oprnd1 / oprnd2;
                            break;
                        default:
                            dos.writeUTF("Error: Invalid Operator");
                            continue;
                    }
                    dos.writeUTF(String.valueOf(result));
                } catch (Exception e) {
                    dos.writeUTF("Error: Invalid format. Use 'Number Operator Number' (e.g., 5 + 3)");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
