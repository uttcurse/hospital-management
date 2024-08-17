package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalMgmt {
    public static final String url="jdbc:mysql://localhost:3306/hospital";
    private static final String username="root";
    private static final String password="mysql";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Scanner scanner = new Scanner(System.in);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            while(true)
            {
                System.out.println("\nHOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointments");
                System.out.println("5. Exit");
                System.out.println("Enter your choice: ");
                int ch = scanner.nextInt();
                switch(ch)
                {
                    case 1:
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        patient.viewPatient();
                        System.out.println();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println("Appointment booked successfully!");
                        break;
                    case 5:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner)
    {
        System.out.print("Enter patient ID: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter doctor ID: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date(YYYY-MM-DD): ");
        String date = scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId))
        {
            if(checkDoctorAvailability(doctorId, date, connection))
            {
                String appointmentQuery = "INSERT INTO appointments(pid, did, appointment_date) VALUES(?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, date);
                    int affectedRows = preparedStatement.executeUpdate();
                    if(affectedRows > 0)
                    {
                        System.out.println("Appointment booked successfully!");
                    }
                    else
                    {
                        System.out.println("Failed to book appointment!");
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                System.out.println("Doctor not available on this date!");
            }
        }
        else
        {
            System.out.println("Patient or doctor not found!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String date, Connection connection) {
        String query = "SELECT * FROM appointments WHERE did = ? AND appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
            {
                int count = resultSet.getInt(1);
                if(count==0)
                {
                    return true;
                }
                else {
                    return false;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
