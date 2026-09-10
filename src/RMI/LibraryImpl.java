package RMI;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryImpl extends UnicastRemoteObject implements LibraryInterface {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";

    public LibraryImpl() throws RemoteException {
        super();
    }

    @Override
    public List<String> getBooks() throws RemoteException {
        List<String> bookList = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Book")) {

            while (rs.next()) {
                String bookRecord = "ID: " + rs.getInt("b_id") +
                        " | Name: " + rs.getString("b_name") +
                        " | Author: " + rs.getString("b_author");
                bookList.add(bookRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            bookList.add("Database Error: " + e.getMessage());
        }

        return bookList;
    }

    @Override
    public List<String> searchBooks(String category, String keyword) throws RemoteException {
        List<String> bookList = new ArrayList<>();
        String sqlQuery = "";

        if (category.equals("Book ID")) {
            sqlQuery = "SELECT * FROM book WHERE b_id = ?";
        } else if (category.equals("Book Name")) {
            sqlQuery = "SELECT * FROM book WHERE b_name LIKE ?";
        } else if (category.equals("Author")) {
            sqlQuery = "SELECT * FROM book WHERE b_author LIKE ?";
        }

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = con.prepareStatement(sqlQuery)) {

            if (category.equals("Book ID")) {
                try {
                    pstmt.setInt(1, Integer.parseInt(keyword));
                } catch (NumberFormatException e) {
                    bookList.add("Database Error: ID must be a number");
                    return bookList;
                }
            } else {
                // Use % for partial text matching (e.g., searching "Rob" finds "Robert")
                pstmt.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String bookRecord = "ID: " + rs.getInt("b_id") +
                        " | Name: " + rs.getString("b_name") +
                        " | Author: " + rs.getString("b_author");
                bookList.add(bookRecord);
            }

            if (bookList.isEmpty()) {
                bookList.add("Database Error: No books found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            bookList.add("Database Error: " + e.getMessage());
        }

        return bookList;
    }
}
