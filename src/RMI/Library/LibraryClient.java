package RMI.Library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class LibraryClient extends JFrame {

    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> searchCategoryBox;
    private JTextField searchField;
    private JButton searchButton;
    private JButton fetchAllButton;

    public LibraryClient() {
        setTitle("Remote Library Database Client");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- TOP PANEL: Search Bar ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Search Books"));

        searchCategoryBox = new JComboBox<>(new String[]{"Book Name", "Author", "Book ID"});
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.setForeground(Color.BLACK);

        topPanel.add(new JLabel("Search By:"));
        topPanel.add(searchCategoryBox);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL: Table ---
        String[] columns = {"Book ID", "Book Name", "Author"};
        tableModel = new DefaultTableModel(columns, 0);
        bookTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Search Results"));
        add(scrollPane, BorderLayout.CENTER);

        // --- BOTTOM PANEL: Fetch All Button ---
        fetchAllButton = new JButton("Fetch All Books");
        fetchAllButton.setForeground(Color.BLACK);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(fetchAllButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- ACTION LISTENERS ---
        searchButton.addActionListener(e -> performSearch());
        fetchAllButton.addActionListener(e -> fetchAllBooks());
    }

    private void performSearch() {
        String category = (String) searchCategoryBox.getSelectedItem();
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search keyword.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            LibraryInterface libraryStub = (LibraryInterface) registry.lookup("LibraryService");

            // Call the new RMI search method
            List<String> books = libraryStub.searchBooks(category, keyword);
            populateTable(books);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void fetchAllBooks() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            LibraryInterface libraryStub = (LibraryInterface) registry.lookup("LibraryService");

            List<String> books = libraryStub.getBooks();
            populateTable(books);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void populateTable(List<String> books) {
        tableModel.setRowCount(0);

        for (String book : books) {
            if (book.startsWith("Database Error")) {
                JOptionPane.showMessageDialog(this, book.replace("Database Error: ", ""), "Message", JOptionPane.INFORMATION_MESSAGE);
                continue;
            }

            String[] parts = book.split(" \\| ");
            if (parts.length == 3) {
                String id = parts[0].replace("ID: ", "").trim();
                String name = parts[1].replace("Name: ", "").trim();
                String author = parts[2].replace("Author: ", "").trim();
                tableModel.addRow(new Object[]{id, name, author});
            }
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Failed to connect to RMI Server.\nEnsure LibraryServer is running.\nError: " + ex.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            LibraryClient gui = new LibraryClient();
            gui.setVisible(true);
        });
    }
}