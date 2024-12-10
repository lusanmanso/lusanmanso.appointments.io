import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BackendServices extends Remote {
	void registerUser(String email, String password) throws RemoteException;
	void bookAppointment(int userId, int clinicId, int specialtyId, String dateTime) throws RemoteException;
	void cancelAppointment(int appointmentId) throws RemoteException;
	List<String> listAppointments(int userId) throws RemoteException;
}