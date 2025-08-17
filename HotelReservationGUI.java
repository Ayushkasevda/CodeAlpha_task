import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class HotelReservationGUI {
    JFrame frame;
    JComboBox<String> roomTypeBox;
    JTextArea outputArea;
    JTextField nameField, roomNumField;
    ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<Reservation> reservations = new ArrayList<>();
    final String FILE_NAME = "reservations.txt";

    public static void main(String[] args) {
        new HotelReservationGUI().start();
    }

    void start() {
        frame = new JFrame("🏨 Hotel Reservation System");
        frame.setSize(700, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(15, 15));
        frame.getContentPane().setBackground(new Color(245, 245, 250));

        initRooms();

        // 🔹 Header
        JLabel title = new JLabel("Welcome to Hotel Reservation System", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(new Color(30, 60, 130));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(title, BorderLayout.NORTH);

        // 🔹 Top Panel (Inputs)
        JPanel topPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Reservation Options"));
        topPanel.setBackground(new Color(235, 240, 255));

        JLabel nameLbl = new JLabel("Customer Name:");
        nameLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField = new JTextField();
        topPanel.add(nameLbl);
        topPanel.add(nameField);

        JLabel roomLbl = new JLabel("Room Type:");
        roomLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        roomTypeBox = new JComboBox<>(new String[]{"STANDARD", "DELUXE", "SUITE"});
        topPanel.add(roomLbl);
        topPanel.add(roomTypeBox);

        JButton bookBtn = styledButton("Book Room", new Color(46, 204, 113));
        JButton cancelBtn = styledButton("Cancel Booking", new Color(231, 76, 60));
        topPanel.add(bookBtn);
        topPanel.add(cancelBtn);

        topPanel.add(new JLabel("Room No (for cancel):"));
        roomNumField = new JTextField();
        topPanel.add(roomNumField);

        JButton viewBtn = styledButton("View Bookings", new Color(52, 152, 219));
        JButton availableBtn = styledButton("Available Rooms", new Color(241, 196, 15));
        topPanel.add(viewBtn);
        topPanel.add(availableBtn);

        frame.add(topPanel, BorderLayout.WEST);

        // 🔹 Output Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(30, 30, 30));
        outputArea.setForeground(new Color(230, 230, 230));
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Output Console"));
        frame.add(scrollPane, BorderLayout.CENTER);

        // Button actions
        bookBtn.addActionListener(e -> bookRoom());
        cancelBtn.addActionListener(e -> cancelRoom());
        viewBtn.addActionListener(e -> viewBookings());
        availableBtn.addActionListener(e -> showAvailableRooms());

        frame.setVisible(true);
    }

    JButton styledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }

    void initRooms() {
        for (int i = 101; i <= 103; i++) rooms.add(new Room(i, "STANDARD"));
        for (int i = 201; i <= 202; i++) rooms.add(new Room(i, "DELUXE"));
        for (int i = 301; i <= 302; i++) rooms.add(new Room(i, "SUITE"));
    }

    void bookRoom() {
        String name = nameField.getText().trim();
        String type = roomTypeBox.getSelectedItem().toString();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Enter customer name.");
            return;
        }

        for (Room r : rooms) {
            if (r.type.equals(type) && r.available) {
                r.available = false;
                Reservation res = new Reservation(name, r.roomNumber, type);
                reservations.add(res);
                saveToFile(res);
                outputArea.append("✅ Room " + r.roomNumber + " booked for " + name + " (" + type + ")\n");
                return;
            }
        }
        outputArea.append("❌ No " + type + " rooms available.\n");
    }

    void cancelRoom() {
        String name = nameField.getText().trim();
        String roomStr = roomNumField.getText().trim();
        if (name.isEmpty() || roomStr.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Enter both name and room number.");
            return;
        }

        try {
            int roomNum = Integer.parseInt(roomStr);
            boolean found = false;

            Iterator<Reservation> it = reservations.iterator();
            while (it.hasNext()) {
                Reservation r = it.next();
                if (r.customerName.equals(name) && r.roomNumber == roomNum) {
                    it.remove();
                    for (Room room : rooms) {
                        if (room.roomNumber == roomNum) {
                            room.available = true;
                            break;
                        }
                    }
                    outputArea.append("🛑 Reservation canceled for Room " + roomNum + " by " + name + "\n");
                    found = true;
                    break;
                }
            }

            if (!found) {
                outputArea.append("⚠ Reservation not found.\n");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid room number.");
        }
    }

    void viewBookings() {
        outputArea.setText("===== All Bookings =====\n");
        for (Reservation r : reservations) {
            outputArea.append(r + "\n");
        }
        loadFromFile();
    }

    void showAvailableRooms() {
        outputArea.setText("===== Available Rooms =====\n");
        for (Room r : rooms) {
            if (r.available) outputArea.append("Room " + r.roomNumber + " - " + r.type + "\n");
        }
    }

    void saveToFile(Reservation res) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            bw.write(res.customerName + "," + res.roomNumber + "," + res.type);
            bw.newLine();
        } catch (IOException e) {
            outputArea.append("Error writing to file.\n");
        }
    }

    void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                outputArea.append("Booking: " + parts[0] + " - Room " + parts[1] + " (" + parts[2] + ")\n");
            }
        } catch (IOException e) {
            outputArea.append("No previous bookings.\n");
        }
    }

    // Room class
    class Room {
        int roomNumber;
        String type;
        boolean available;

        Room(int num, String type) {
            this.roomNumber = num;
            this.type = type;
            this.available = true;
        }
    }

    // Reservation class
    class Reservation {
        String customerName;
        int roomNumber;
        String type;

        Reservation(String name, int roomNum, String type) {
            this.customerName = name;
            this.roomNumber = roomNum;
            this.type = type;
        }

        public String toString() {
            return customerName + " - Room " + roomNumber + " (" + type + ")";
        }
    }
}


