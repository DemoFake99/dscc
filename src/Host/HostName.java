package Host;

import java.net.*;
import java.util.*;

public class HostName {
    static void main() {
        Scanner sc = new Scanner(System.in);

        try {
            // Taking hostname from the user
            IO.println("Enter Host Name: ");
            String hostname = sc.nextLine();

            // Getting InetAddress object for the specified hostname
            InetAddress ip = InetAddress.getByName(hostname);

            // Printing the hostname
            IO.println("Host Name: " + ip.getHostName());

            // Printing the IP address
            IO.println("Host Address: " + ip.getHostAddress());

        } catch (UnknownHostException u) {
            IO.println("Host not found: " + u);
        }

        sc.close();
    }
}
