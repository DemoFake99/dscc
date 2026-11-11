package RMI.Student;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentImpl extends UnicastRemoteObject implements StudentInterface {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/student";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";

    public StudentImpl() throws RemoteException {
        super();
    }

    private String formatRecord(ResultSet rs) throws SQLException {
        return "ID: " + rs.getInt("ID") +
                " | Name: " + rs.getString("NAME") +
                " | Branch: " + rs.getString("BRANCH") +
                " | Percentage: " + rs.getDouble("PERCENTAGE") +
                " | Email: " + rs.getString("EMAIL");
    }

    @Override
    public List<String> getAllStudents() throws RemoteException {
        List<String> studentList = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM student_data")) {

            while (rs.next()) {
                studentList.add(formatRecord(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            studentList.add("Database Error: " + e.getMessage());
        }

        return studentList;
    }

    @Override
    public List<String> searchStudents(String category, String keyword) throws RemoteException {
        List<String> studentList = new ArrayList<>();
        String sqlQuery = "";

        switch (category) {
            case "Student ID":
                sqlQuery = "SELECT * FROM student_data WHERE ID = ?";
                break;
            case "Name":
                sqlQuery = "SELECT * FROM student_data WHERE NAME LIKE ?";
                break;
            case "Branch":
                sqlQuery = "SELECT * FROM student_data WHERE BRANCH LIKE ?";
                break;
            case "Email":
                sqlQuery = "SELECT * FROM student_data WHERE EMAIL LIKE ?";
                break;
            default:
                studentList.add("Database Error: Invalid search category");
                return studentList;
        }

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = con.prepareStatement(sqlQuery)) {

            if (category.equals("Student ID")) {
                try {
                    pstmt.setInt(1, Integer.parseInt(keyword));
                } catch (NumberFormatException e) {
                    studentList.add("Database Error: ID must be a number");
                    return studentList;
                }
            } else {
                pstmt.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                studentList.add(formatRecord(rs));
            }

            if (studentList.isEmpty()) {
                studentList.add("Database Error: No students found matching criteria.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            studentList.add("Database Error: " + e.getMessage());
        }

        return studentList;
    }
}