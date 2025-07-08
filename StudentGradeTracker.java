import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class StudentGradeTracker {
    JFrame frame;
    JTextField nameField, gradeField;
    JTextArea outputArea;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<Double> grades = new ArrayList<>();

    public StudentGradeTracker() {
        frame = new JFrame("Student Grade Tracker");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Input Panel
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Enter Student Details"));

        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Grade (0–100):"));
        gradeField = new JTextField();
        panel.add(gradeField);

        JButton addBtn = new JButton("Add Student");
        JButton showBtn = new JButton("Show Summary");
        panel.add(addBtn);
        panel.add(showBtn);

        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Summary Report"));

        // Add panels to frame
        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Event: Add Student
        addBtn.addActionListener(e -> addStudent());

        // Event: Show Summary
        showBtn.addActionListener(e -> showSummary());

        frame.setVisible(true);
    }

    void addStudent() {
        String name = nameField.getText().trim();
        String gradeText = gradeField.getText().trim();

        if (name.isEmpty() || gradeText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both name and grade.");
            return;
        }

        try {
            double grade = Double.parseDouble(gradeText);
            if (grade < 0 || grade > 100) {
                JOptionPane.showMessageDialog(frame, "Grade must be between 0 and 100.");
                return;
            }

            names.add(name);
            grades.add(grade);
            outputArea.append("Added: " + name + " - " + grade + "\n");

            nameField.setText("");
            gradeField.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid grade. Please enter a numeric value.");
        }
    }

    void showSummary() {
        if (grades.isEmpty()) {
            outputArea.setText("No student data entered.");
            return;
        }

        double total = 0, max = -1, min = 101;
        String top = "", bottom = "";

        for (int i = 0; i < grades.size(); i++) {
            double g = grades.get(i);
            total += g;

            if (g > max) {
                max = g;
                top = names.get(i);
            }

            if (g < min) {
                min = g;
                bottom = names.get(i);
            }
        }

        double average = total / grades.size();

        // Build report
        StringBuilder report = new StringBuilder();
        report.append("===== SUMMARY REPORT =====\n");
        report.append("Total Students: ").append(grades.size()).append("\n");
        report.append(String.format("Average Grade: %.2f\n", average));
        report.append(String.format("Highest Grade: %.2f (%s)\n", max, top));
        report.append(String.format("Lowest Grade: %.2f (%s)\n\n", min, bottom));
        report.append("----- Student List -----\n");

        for (int i = 0; i < names.size(); i++) {
            report.append(names.get(i)).append(" - ").append(grades.get(i)).append("\n");
        }

        outputArea.setText(report.toString());
    }

    public static void main(String[] args) {
        new StudentGradeTracker();
    }
}
