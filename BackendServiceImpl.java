import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class BackendServiceImpl extends UnicastRemoteObject implements BackendServices {

    public BackendServiceImpl() throws RemoteException {
        super();
    }

    public void registerUser(String email, String password) throws RemoteException {
        System.out.println("Registering user " + email);
    }

    public void bookAppointment(int userId, int clinicId, int specialtyId, String dateTime) throws RemoteException {
        System.out.println("Booking appointment for user " + userId + " at clinic " + clinicId + " with specialty " + specialtyId + " on " + dateTime);
    }

    public void cancelAppointment(int appointmentId) throws RemoteException {
        System.out.println("Cancelling appointment " + appointmentId);
    }

    public List<String> listAppointments(int userId) throws RemoteException {
        List<String> appointments = new ArrayList<String>();
        appointments.add("Lucy");
        appointments.add("John");
        appointments.add("Peter");
        return appointments;
    }

}