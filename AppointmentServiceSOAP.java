import javax.jws.WebMethod;
import javax.jws.WebService;
import java.rmi.Naming;
import java.util.List;
import java.util.Map;

@WebService(targetNamespace = "http://default_package/", portName = "SOAPServPort", serviceName = "SOAPServService")
public class AppointmentServiceSOAP {
	
    BackendServices backend;

    public AppointmentServiceSOAP() {
        try {
            backend = (BackendServices) Naming.lookup("rmi://192.168.1.184:5000/backendService");
            System.out.println("Connection with backend RMI successful.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error connecting with the backend RMI: " + e.getMessage());
        }
    }

    @WebMethod
    public String registerUser(String email, String password) {
        try {
            backend.registerUser(email, password);
            return "User registered successfully: " + email;
        } catch (Exception e) {
            return "Error registering user: " + e.getMessage();
        }
    }
    
    @WebMethod
    public String getAvailableSlots(String specialtyName, String clinicId, String date) {
        try {
            Map<Integer, String> slots = backend.getAvailableSlots(specialtyName, clinicId, date);
            if (slots.isEmpty()) {
                return "No available slots.";
            }

            StringBuilder sb = new StringBuilder("Available Slots:\n");
            for (Map.Entry<Integer, String> entry : slots.entrySet()) {
                sb.append("Slot ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            return sb.toString();

        } catch (Exception e) {
            return "Error getting available slots: " + e.getMessage();
        }
    }


    @WebMethod
    public String bookAppointment(String userEmail, String password, String clinicId, String specialtyName, String date, int slot) {
        try {
            backend.bookAppointment(userEmail, password, clinicId, specialtyName, date, slot);
            return "Appointment successfully booked for user " + userEmail;
        } catch (Exception e) {
            return "Error booking appointment: " + e.getMessage();
        }
    }

    @WebMethod
    public String cancelAppointment(String userEmail, String password, String appointmentId) {
        try {
            backend.cancelAppointment(userEmail, password, appointmentId);
            return "Appointment cancelled successfully.";
        } catch (Exception e) {
            return "Error cancelling appointment: " + e.getMessage();
        }
    }

    @WebMethod
    public String listAppointments(String userEmail, String password) {
        try {
            List<String> appointments = backend.listAppointments(userEmail, password);
            if (appointments.isEmpty()) {
                return "No appointments found for the user.";
            }
            StringBuilder response = new StringBuilder("User appointments:\n");
            for (String appointment : appointments) {
                response.append(appointment).append("\n");
            }
            return response.toString();
        } catch (Exception e) {
            return "Error listing appointments: " + e.getMessage();
        }
    }
}