import java.rmi.Naming;
import java.rmi.RemoteException; // RemoteExceptions for RMI 
import java.rmi.server.UnicastRemoteObject; // RMI Classes implementation

import java.sql.Connection; // Handle JDBC connections
import java.sql.PreparedStatement; // Handle prepared SQL statements
import java.sql.ResultSet; // Handle SQL results
import java.sql.SQLException; // Handle SQL errors

import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.HashMap;

// Password ciphering
import java.nio.charset.StandardCharsets; // To work with UTF-8 encoding
import java.security.MessageDigest; // Calculate (SHA-256) hashes
import java.security.NoSuchAlgorithmException; // Exception if the hashing algorithm is not found

public class BackendServiceImpl extends UnicastRemoteObject implements BackendServices {

    public BackendServiceImpl() throws RemoteException {
        super();
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

    public Map<Integer, String> listAvailableSlots(int specialtyId, int clinicId, String date) throws RemoteException {
        
        Map<Integer, String> slots = new HashMap<Integer, String>();
        return slots;
    }

    private int findAvailableDoctor(Connection conn, int clinicId, int specialtyId, String date, int slot) throws SQLException, RemoteException {
        // Find first available doctor for the clinic
        String sql = "SELECT d.id FROM Doctors d " + 
                     "WHERE d.specialty_id = ? AND d.clinic_id = ? " +
                     "AND d.id NOT IN (" +
                     "  SELECT doctor_id FROM Appointments " +
                     "  WHERE appointment_date = ? AND appointment_slot = ?" +
                     ") LIMIT 1";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, specialtyId);
        stmt.setInt(2, clinicId);
        stmt.setString(3, date);
        stmt.setInt(4, slot);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        } else {
            throw new RemoteException("No doctors available for this specialty and clinic at the selected slot.")
        }
    }

    @Override
    public void bookAppointment(int userId, int clinicId, int specialtyId, String date, int slot) throws RemoteException {
        try (Connection conn = MySQLConnection.getConnection()) {
            // Validate slot number
            if (slot < 1 || slot > 12 ) {
                throw new RemoteException("Invalid slot number. Slots range from 1 to 12.");
            }

            // TODO Verify the clinic exists

            // Verify the specialty belongs to the clinic
            String checkSQL = "SELECT COUNT(*) FROM Specialties WHERE id = ? AND clinic_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setInt(1, specialtyId);
            checkStmt.setInt(2, clinicId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new RemoteException("The specialty does not belong to the clinic.");
            }

            // Find first available doctor for the first specialty
            int doctorId = findAvailableDoctor(conn, clinicId, specialtyId, date, slot);

            // Insert appointment
            String sql = "INSERT INTO Appointments (user_id, specialty_id, doctor_id, appointmentd_date, appointment_slot) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, specialtyId);
            stmt.setInt(3, doctorId);
            stmt.setString(4, date);
            stmt.setInt(5, slot);
            stmt.executeUpdate();

            System.out.println("Appointment successfully booked for user " + userId + " with doctor " + doctorId + " at clinic " + clinicId + ".");        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error while booking the appointment.");
        }
    }

    public void cancelAppointment(int appointmentId) throws RemoteException {
        try (Connection conn = MySQLConnection.getConnection()) {
            // Delete appointment
            String sql = "DELETE FROM Appointments WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, appointmentId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RemoteException("The appointment does not exist.");
            }

            System.out.println("Appointment cancelled successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error cancelling appointment.");
        }
    }

    public List<String> listAppointments(int userId) throws RemoteException {
        List<String> appointments = new ArrayList<String>();
        return appointments;
    }

}