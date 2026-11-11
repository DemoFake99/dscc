package RMI.Student;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class StudentServer {
    static void main() {
        try {
            StudentInterface studentService = new StudentImpl();

            // Create RMI registry on default port 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // Bind the remote object stub under the name "StudentService"
            registry.rebind("StudentService", studentService);

            IO.println("Student RMI Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
