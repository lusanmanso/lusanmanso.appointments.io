import javax.jws.WebService;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import default_package.AppointmentServiceSOAP;

import java.util.Scanner;

public class ClientSOAP {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

        // Use the URL defined in the SOAP address portion of the WSDL
        factory.setAddress("http://localhost:8080/SOAPServer/services/SOAPServPort");

        // Utilize the class which was auto-generated by Apache CXF wsdl2java
        factory.setServiceClass(AppointmentServiceSOAP.class);

        Object client = factory.create();

        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- SOAP Client Menu ---");
            System.out.println("1. Register User");
            System.out.println("2. Book Appointment");
            System.out.println("3. List Appointments");
            System.out.println("4. Cancel Appointment");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // consumir el salto de línea

            try {
                switch (option) {
                    case 1:
                        System.out.print("Enter email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();
                        String registerResponse = ((AppointmentServiceSOAP)client).registerUser(email, password);
                        System.out.println(registerResponse);
                        break;
                    case 2:
                        System.out.print("Enter user email: ");
                        String userEmail = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String userPasswordForBooking = scanner.nextLine();
                        System.out.print("Enter clinic ID: ");
                        String clinicId = scanner.nextLine();
                        System.out.print("Enter specialty name: ");
                        String specialtyName = scanner.nextLine();
                        System.out.print("Enter date (YYYY-MM-DD): ");
                        String date = scanner.nextLine();

                        // Call getAvailableSlots before user selects the slot
                        String availableSlots = ((AppointmentServiceSOAP)client).getAvailableSlots(specialtyName, clinicId, date);
                        System.out.println("Available slots: " + availableSlots);

                        System.out.print("Enter time slot (1-12): ");
                        int slot = scanner.nextInt();
                        scanner.nextLine();

                        String bookResponse = ((AppointmentServiceSOAP)client).bookAppointment(userEmail, userPasswordForBooking, clinicId, specialtyName, date, slot);
                        System.out.println(bookResponse);
                        break;
                    case 3:
                        System.out.print("Enter user email: ");
                        String listUserEmail = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String userPasswordForList = scanner.nextLine();
                        String listResponse = ((AppointmentServiceSOAP)client).listAppointments(listUserEmail, userPasswordForList);
                        System.out.println(listResponse);
                        break;
                    case 4:
                        System.out.print("Enter user email: ");
                        String cancelUserEmail = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String userPasswordForCancel = scanner.nextLine();
                        System.out.print("Enter appointment ID: ");
                        String appointmentId = scanner.nextLine();
                        String cancelResponse = ((AppointmentServiceSOAP)client).cancelAppointment(cancelUserEmail, userPasswordForCancel, appointmentId);
                        System.out.println(cancelResponse);
                        break;
                    case 5:
                        exit = true;
                        System.out.println("Exiting SOAP client...");
                        break;

                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        scanner.close();
    }
}