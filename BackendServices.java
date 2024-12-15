
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BackendServices extends Remote {
	void registerUser(String email, String password) throws RemoteException;
    void bookAppointment(String userEmail, String clinicId, String specialtyName, String date, int slot) throws RemoteException;
	void cancelAppointment(String appointmentId) throws RemoteException;
	List<String> listAppointments(String userEmail) throws RemoteException;
}