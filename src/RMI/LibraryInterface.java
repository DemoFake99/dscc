package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LibraryInterface extends Remote {
    List<String> getBooks() throws RemoteException;
    List<String> searchBooks(String category, String keyword) throws RemoteException;
}
