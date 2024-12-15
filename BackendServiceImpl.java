// import java.rmi.Naming;
// import java.rmi.Remote;
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

    /* 1. REGISTER USER */
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
            throw new RuntimeException("Error hashing password.", e);
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$"); 
    }

    @Override
    public void registerUser(String email, String password) throws RemoteException {
        try (Connection conn = MySQLConnection.getConnection()) {
            if (!isValidEmail(email)) {
                throw new RemoteException(("Invalid email format."));
            }

            // Cipher passwords for further security
            String hashedPassword = hashPassword(password);

            // SQL consult
            String sql = "INSERT INTO Users (email, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();

            System.out.println("User successfully registered: " + email);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Error code for duplicated keys
                throw new RemoteException("The email is already registered.");
            }
            e.printStackTrace();
            throw new RemoteException("Error registering user.");
        }
    }

    /* 2. BOOK APPOINTMENT */
    private void validateClinicExists(Connection conn, String clinicId) throws SQLException, RemoteException {
        String sql = "SELECT COUNT(*) FROM Clinics WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, clinicId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next() && rs.getInt(1) == 0) {
            throw new RemoteException("Clinic does not exist.");
        }
    }
    
    private void validateSpecialtyInClinic(Connection conn, String clinicId, String specialtyName) throws SQLException, RemoteException {
        System.out.println("Validating if specialty '" + specialtyName + "' belongs to clinic '" + clinicId + "'");
    
        String sql = "SELECT COUNT(*) FROM Specialties WHERE name = ? AND clinic_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, specialtyName);
        stmt.setString(2, clinicId);
    
        ResultSet rs = stmt.executeQuery();
        if (rs.next() && rs.getInt(1) == 0) {
            throw new RemoteException("Specialty '" + specialtyName + "' does not belong to clinic '" + clinicId + "'");
        }
    }

    // Auxiliary method to convert slots to timetable
    private String slotToTime(int slot) throws RemoteException {
        String[] times = {
            "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM",
            "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM",
            "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM"
        };

        if (slot < 1 || slot > 12) {
            throw new RemoteException("Invalid slot number. Slots range from 1 to 12.");
        }
        return times[slot - 1];
    }

    private Map<Integer, String> listAvailableSlots(String specialtyName, String clinicId, String date) throws RemoteException {
        Map<Integer, String> availableSlots = new HashMap<>();
        try (Connection conn = MySQLConnection.getConnection()) {
            validateClinicExists(conn, clinicId);
            validateSpecialtyInClinic(conn, clinicId, specialtyName);

            for (int i = 1; i <= 12; i++) {
                availableSlots.put(i, slotToTime(i));
            }

            String sql = "SELECT appointment_slot FROM Appointments a " +
                         "JOIN Doctors d ON a.doctor_id = d.id " +
                         "WHERE d.specialty_name = ? AND d.clinic_id = ? AND a.appointment_date = ? ";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, specialtyName);
            stmt.setString(2, clinicId);
            stmt.setString(3, date);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int bookedSlot = rs.getInt("appointment_slot");
                availableSlots.remove(bookedSlot); // Delete occupied slots
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error validating clinic and specialty.");
        } 
    
        return availableSlots;
    }

    private String findAvailableDoctor(Connection conn, String clinicId, String specialtyName, String date, int slot) throws SQLException, RemoteException {
        // Find first available doctor for the clinic
        String sql = "SELECT d.id FROM Doctors d " + 
                     "WHERE d.specialty_name = ? AND d.clinic_id = ? " +
                     "AND d.id NOT IN (" +
                     "  SELECT doctor_id FROM Appointments " +
                     "  WHERE appointment_date = ? AND appointment_slot = ?" +
                     ") LIMIT 1";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, specialtyName);
        stmt.setString(2, clinicId);
        stmt.setString(3, date);
        stmt.setInt(4, slot);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("id");
        } else {
            throw new RemoteException("No doctors available for this specialty and clinic at the selected slot.");
        }
    }

    @Override
    public void bookAppointment(String userEmail, String clinicId, String specialtyName, String date, int slot) throws RemoteException {
        try (Connection conn = MySQLConnection.getConnection()) {
            Map<Integer, String> availableSlotsBefore = listAvailableSlots(specialtyName, clinicId, date);
            System.out.println("Available slots before booking: " + availableSlotsBefore);

            if (slot < 1 || slot > 12) {
                throw new RemoteException("Invalid slot number. Slots range from 1 to 12.");
            }

            validateClinicExists(conn, clinicId);
            validateSpecialtyInClinic(conn, clinicId, specialtyName);

            String doctorId = findAvailableDoctor(conn, clinicId, specialtyName, date, slot);

            String sql = "INSERT INTO Appointments (id, user_email, specialty_name, doctor_id, appointment_date, appointment_slot) " +
                         "VALUES (UUID(), ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userEmail);
            stmt.setString(2, specialtyName);
            stmt.setString(3, doctorId);
            stmt.setString(4, date);
            stmt.setInt(5, slot);
            stmt.executeUpdate();

            System.out.println("Appointment successfully booked for user " + userEmail + " with doctor " + doctorId + " at clinic " + clinicId + ".");

            Map<Integer, String> availableSlotsAfter = listAvailableSlots(specialtyName, clinicId, date);
            System.out.println("Available slots after booking: " + availableSlotsAfter);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error while booking the appointment.");
        }
    }

    /* 3. CANCEL APPOINTMENT */
    @Override
    public void cancelAppointment(String appointmentId) throws RemoteException {
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "DELETE FROM Appointments WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, appointmentId);

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

    /* 4. LIST APPOINTMENTS */
    @Override
    public List<String> listAppointments(String userEmail) throws RemoteException {
        List<String> appointments = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT a.appointment_date, a.appointment_slot, d.name AS doctor_name, s.name AS specialty_name " +
                        "FROM Appointments a " +
                        "JOIN Doctors d ON a.doctor_id = d.id " +
                        "JOIN Specialties s ON a.specialty_name = s.name " +
                        "WHERE a.user_email = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userEmail);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String appointment = "Date: " + rs.getString("appointment_date") +
                                     " Slot: " + rs.getString("appointment_slot") +
                                     " Doctor: " + rs.getString("doctor_name") +
                                     " Specialty: " + rs.getString("specialty_name");
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error listing appointments.");
        }
        return appointments;
    }
}