package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Patient {
    private Connection connection;
    private Scanner scanner;

    public Patient(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void addPatient() {
        scanner = new Scanner(System.in);
        System.out.println("Enter patient name: ");
        String name = scanner.nextLine();
        System.out.println("Enter patient age: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid age.");
            scanner.next(); // consume the invalid input
        }
        int age = scanner.nextInt();
        scanner.nextLine(); // consume the newline character

        System.out.println("Enter patient gender: ");
        String gender = scanner.nextLine().trim();
        if (gender.isEmpty()) {
            System.out.println("Patient gender cannot be empty.");
            return;
        }

        try {
            String query = "INSERT INTO patients(name, age, gender) VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Patient added successfully.");
            } else {
                System.out.println("Failed to add patient!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewPatient() {
        String query = "SELECT * FROM patients";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Patients:");
            System.out.println("+------------+--------------------------------+---------+---------------+");
            System.out.println("| Patient ID | Patient Name                   | Age     | Gender        |");
            System.out.println("+------------+--------------------------------+---------+---------------+");
            while(resultSet.next()) {
                System.out.printf("| %-10s | %-30s | %-7s | %-13s |\n",
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getString("gender"));
                System.out.println("+------------+--------------------------------+---------+---------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getPatientById(int id) {
        String query = "SELECT * FROM patients WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}