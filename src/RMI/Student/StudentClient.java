package RMI.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class StudentClient extends JFrame {

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> searchCategoryBox;
    private JTextField searchField;
    private JButton searchButton;
    private JButton fetchAllButton;

    public StudentClient() {
        setTitle("Remote Student Database Client");
        setSize(850, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top Search Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Search Student Records"));

        searchCategoryBox = new JComboBox<>(new String[]{"Name", "Branch", "Student ID", "Email"});
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.setForeground(Color.BLACK);

        topPanel.add(new JLabel("Search By:"));
        topPanel.add(searchCategoryBox);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        // Center Table View
        String[] columns = {"ID", "Name", "Branch", "Percentage", "Email"};
        tableModel = new DefaultTableModel(columns, 0);
        studentTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Student Details"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Panel
        fetchAllButton = new JButton("Fetch All Students");
        fetchAllButton.setForeground(Color.BLACK);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(fetchAllButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        searchButton.addActionListener(e -> performSearch());
        fetchAllButton.addActionListener(e -> fetchAllStudents());
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
            StudentInterface stub = (StudentInterface) registry.lookup("StudentService");

            List<String> students = stub.searchStudents(category, keyword);
            populateTable(students);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void fetchAllStudents() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            StudentInterface stub = (StudentInterface) registry.lookup("StudentService");

            List<String> students = stub.getAllStudents();
            populateTable(students);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void populateTable(List<String> students) {
        tableModel.setRowCount(0);

        for (String record : students) {
            if (record.startsWith("Database Error")) {
                JOptionPane.showMessageDialog(this, record.replace("Database Error: ", ""), "Message", JOptionPane.INFORMATION_MESSAGE);
                continue;
            }

            String[] parts = record.split(" \\| ");
            if (parts.length == 5) {
                String id = parts[0].replace("ID: ", "").trim();
                String name = parts[1].replace("Name: ", "").trim();
                String branch = parts[2].replace("Branch: ", "").trim();
                String percentage = parts[3].replace("Percentage: ", "").trim();
                String email = parts[4].replace("Email: ", "").trim();
                tableModel.addRow(new Object[]{id, name, branch, percentage, email});
            }
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Failed to connect to RMI Server.\nEnsure StudentServer is running.\nError: " + ex.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
    }

    static void main() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            StudentClient gui = new StudentClient();
            gui.setVisible(true);
        });
    }
}
