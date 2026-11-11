package Host;

import java.net.*; // Importing networking classes

public class IP {
    static void main() {
        try {
            // Attempting to get the InetAddress object for the specified hostname
            InetAddress ip = InetAddress.getByName("www.instagram.com");

            // Printing the hostname
            IO.println("Host Name: " + ip.getHostName());

            // Printing the IP address in string format
            IO.println("Host Address: " + ip.getHostAddress());
        } catch (UnknownHostException uh) {
            // Handling the exception if the hostname is not found
            IO.println(uh);
        }
    }
}
