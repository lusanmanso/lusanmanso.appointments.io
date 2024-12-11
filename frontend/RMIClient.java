package frontend;
import java.rmi.Naming;

import backend.BackendServices;

public class RMIClient {
    public static void main(String[] args) {
        try {
            BackendServices service = (BackendServices) Naming.lookup("rmi://127.0.0.1:5000/backendService");

            // Test: Register a user
            service.registerUser("test@example.com", "password123");
            System.out.println("User registered successfully!");

            // Test: List appointments
            System.out.println("Appointments: " + service.listAppointments(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
