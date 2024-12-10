import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class BackendServiceImpl extends UnicastRemoteObject implements BackendServices {

    public BackendServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void registerUser(String email, String password) throws RemoteException {
        try (Connection conn = MySQLConnection.getConnection()) {
            // Cipher passwords for further security
            String hashedPassword = hashPassword(password);

            // SQL consult
            String sql = "INSERT INTO Users (email, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();

            System.out.println("Usuario registrado con éxito: " + email);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Error code for duplicated keys
                throw new RemoteException("El email ya está registrado.");
            }
            e.printStackTrace();
            throw new RemoteException("Error al registrar el usuario.");
        }
    }

    // First version of method to cipher passwords
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al cifrar la contraseña.", e);
        }
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