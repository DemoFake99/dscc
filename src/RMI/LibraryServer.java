package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LibraryServer {
    public static void main(String[] args) {
        try {
            LibraryInterface libraryService = new LibraryImpl();

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("LibraryService", libraryService);

            System.out.println("Library RMI Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
