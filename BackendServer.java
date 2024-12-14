
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class BackendServer {
	public static void main(String[] args) {
		try {
			System.out.println("Starting RMI registry...");
			LocateRegistry.createRegistry(5000); // Create a RMI registry on port 5000
			System.out.println("RMI registry started...");

			System.out.println("Starting BackendServerImpl instance...");
			BackendServices service = new BackendServiceImpl();
			System.out.println("BackendServerImpl instance started...");

			System.out.println("Binding BackendServerImpl instance to RMI registry...");
			Naming.rebind("rmi://192.168.1.184:5000/backendService", service);
			System.out.println("Backend RMI server is running...");

		} catch (RemoteException e) {
			System.out.println("RemoteException occurred");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Exception occurred");
			e.printStackTrace();
		}
	}
}