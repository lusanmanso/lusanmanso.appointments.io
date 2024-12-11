import java.rmi.Naming;
import java.rmi.RemoteException; // RemoteExceptions for RMI 
import java.rmi.server.UnicastRemoteObject; // RMI Classes implementation
import java.sql.Connection; // Handle JDBC connections
import java.sql.PreparedStatement; // Handle prepared SQL statements
import java.sql.ResultSet; // PHandle SQL results
import java.sql.SQLException; // Handle SQL errors
import java.util.ArrayList;
import java.util.List;

// Password ciphering
import java.nio.charset.StandardCharsets; // To work with UTF-8 encoding
import java.security.MessageDigest; // Calculate (SHA-256) hashes
import java.security.NoSuchAlgorithmException; // Exception if the hashing algorithm is not found

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

            System.out.println("User succesfully registered: " + email);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Error code for duplicated keys
                throw new RemoteException("The email is already registered.");
            }
            e.printStackTrace();
            throw new RemoteException("Error registering user.");
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
            throw new RuntimeException("Error ciphering password.", e);
        }
    }

    public void bookAppointment(int userId, int clinicId, int specialtyId, String dateTime) throws RemoteException {
        try (Connection conn = MySQLConnection.getConnection()) {
            // Verify the specialty belongs to the clinic
            String checkSQL = "SELECT COUNT(*) FROM Specialties WHERE id = ? AND clinic_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setInt(1, specialtyId);
            checkStmt.setInt(2, clinicId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new RemoteException("The specialty does not belong to the clinic.");
            }

            // Insert appointment
            String sql = "INSERT INTO Appointments (user_id, specialty_id, date_time) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, specialtyId);
            stmt.setString(3, dateTime);
            stmt.executeUpdate();

            System.out.println("Appointment booked successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error booking appointment.");
        }
    }

    public void cancelAppointment(int appointmentId) throws RemoteException {
    }

    public List<String> listAppointments(int userId) throws RemoteException {
        List<String> appointments = new ArrayList<String>();
        appointments.add("Lucy");
        appointments.add("John");
        appointments.add("Peter");
        return appointments;
    }

}