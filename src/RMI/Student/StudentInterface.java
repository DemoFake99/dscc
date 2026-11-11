package RMI.Student;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface StudentInterface extends Remote {
    List<String> getAllStudents() throws RemoteException;
    List<String> searchStudents(String category, String keyword) throws RemoteException;
}
