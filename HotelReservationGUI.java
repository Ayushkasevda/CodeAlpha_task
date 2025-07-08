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
        frame = new JFrame("Hotel Reservation System");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        initRooms();

        // Top Panel
        JPanel topPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Reservation Options"));

        topPanel.add(new JLabel("Customer Name:"));
        nameField = new JTextField();
        topPanel.add(nameField);

        topPanel.add(new JLabel("Room Type:"));
        roomTypeBox = new JComboBox<>(new String[]{"STANDARD", "DELUXE", "SUITE"});
        topPanel.add(roomTypeBox);

        JButton bookBtn = new JButton("Book Room");
        JButton cancelBtn = new JButton("Cancel Booking");
        topPanel.add(bookBtn);
        topPanel.add(cancelBtn);

        topPanel.add(new JLabel("Room No (for cancel):"));
        roomNumField = new JTextField();
        topPanel.add(roomNumField);

        JButton viewBtn = new JButton("View Bookings");
        JButton availableBtn = new JButton("Available Rooms");
        topPanel.add(viewBtn);
        topPanel.add(availableBtn);

        frame.add(topPanel, BorderLayout.NORTH);

        // Output
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Output"));

        frame.add(scrollPane, BorderLayout.CENTER);

        // Button actions
        bookBtn.addActionListener(e -> bookRoom());
        cancelBtn.addActionListener(e -> cancelRoom());
        viewBtn.addActionListener(e -> viewBookings());
        availableBtn.addActionListener(e -> showAvailableRooms());

        frame.setVisible(true);
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
                outputArea.append("Room " + r.roomNumber + " booked for " + name + " (" + type + ")\n");
                return;
            }
        }
        outputArea.append("No " + type + " rooms available.\n");
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
                    outputArea.append("Reservation canceled for Room " + roomNum + " by " + name + "\n");
                    found = true;
                    break;
                }
            }

            if (!found) {
                outputArea.append("Reservation not found.\n");
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
