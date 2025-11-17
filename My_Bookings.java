import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class My_Bookings {
    private Scanner scanner = new Scanner(System.in);

    // CSV file path - CHANGED to match BookingTickets.java
    private static final String BOOKINGS_FILE = "movie_bookings.csv";

    // Inner class to represent a booking
    public static class Booking {
        private int bookingId;
        private String movieName;
        private String customerName;
        private String customerEmail;
        private int numberOfTickets;
        private double totalPrice;
        private String bookingDate;
        private String bookingTime;
        private String showtime;
        private String seats; // Seat coordinates (e.g., "A1;B5;J10")
        private String status; // "Confirmed", "Cancelled"

        // Constructor for loading from CSV (BookingTickets format)
        // Format: BookingID,MovieName,CustomerName,CustomerEmail,Tickets,TotalPrice,BookingDate,BookingTime,Showtime,Seats
        public Booking(int bookingId, String movieName, String customerName, String customerEmail,
                       int numberOfTickets, double totalPrice, String bookingDate, 
                       String bookingTime, String showtime, String seats) {
            this.bookingId = bookingId;
            this.movieName = movieName;
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.numberOfTickets = numberOfTickets;
            this.totalPrice = totalPrice;
            this.bookingDate = bookingDate;
            this.bookingTime = bookingTime;
            this.showtime = showtime;
            this.seats = seats != null ? seats : "";
            this.status = "Confirmed"; // Default status
        }

        // Constructor with status (for cancelled bookings)
        public Booking(int bookingId, String movieName, String customerName, String customerEmail,
                       int numberOfTickets, double totalPrice, String bookingDate, 
                       String bookingTime, String showtime, String seats, String status) {
            this.bookingId = bookingId;
            this.movieName = movieName;
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.numberOfTickets = numberOfTickets;
            this.totalPrice = totalPrice;
            this.bookingDate = bookingDate;
            this.bookingTime = bookingTime;
            this.showtime = showtime;
            this.seats = seats != null ? seats : "";
            this.status = status;
        }

        // Getters
        public int getBookingId() { return bookingId; }
        public String getMovieName() { return movieName; }
        public int getNumberOfTickets() { return numberOfTickets; }
        public double getTicketPrice() { 
            if (numberOfTickets > 0) {
                return totalPrice / numberOfTickets; 
            }
            return 0.0;
        }
        public double getTotalPrice() { return totalPrice; }
        public String getCustomerName() { return customerName; }
        public String getCustomerEmail() { return customerEmail; }
        public String getStatus() { return status; }
        public String getBookingDate() { return bookingDate + " " + bookingTime; }
        public String getShowtime() { return showtime; }
        public String getSeats() { return seats != null ? seats : ""; }

        // Setters
        public void setStatus(String status) { this.status = status; }
        public void setNumberOfTickets(int numberOfTickets) { 
            double pricePerTicket = this.totalPrice / this.numberOfTickets;
            this.numberOfTickets = numberOfTickets;
            this.totalPrice = numberOfTickets * pricePerTicket;
        }

        // Convert booking to CSV line (BookingTickets format)
        public String toCSV() {
            String seatsStr = seats != null ? seats : "";
            if (status != null && !status.isEmpty()) {
                // Include status if present
                return bookingId + "," +
                       movieName + "," +
                       customerName + "," +
                       customerEmail + "," +
                       numberOfTickets + "," +
                       String.format("%.2f", totalPrice) + "," +
                       bookingDate + "," +
                       bookingTime + "," +
                       showtime + "," +
                       seatsStr + "," +
                       status;
            } else {
                // No status field
                return bookingId + "," +
                       movieName + "," +
                       customerName + "," +
                       customerEmail + "," +
                       numberOfTickets + "," +
                       String.format("%.2f", totalPrice) + "," +
                       bookingDate + "," +
                       bookingTime + "," +
                       showtime + "," +
                       seatsStr;
            }
        }
    }

    // Method to read all bookings from CSV file
    public static ArrayList<Booking> loadAllBookings() {
        ArrayList<Booking> bookings = new ArrayList<>();
        File file = new File(BOOKINGS_FILE);

        if (!file.exists()) {
            return bookings; // Return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    
                    // Handle formats:
                    // Format 1 (new BookingTickets with seats): BookingID,MovieName,CustomerName,CustomerEmail,Tickets,TotalPrice,Date,Time,Showtime,Seats
                    // Format 2 (with status): BookingID,MovieName,CustomerName,CustomerEmail,Tickets,TotalPrice,Date,Time,Showtime,Seats,Status
                    // Format 3 (old format without seats): BookingID,MovieName,CustomerName,CustomerEmail,Tickets,TotalPrice,Date,Time,Showtime
                    
                    if (parts.length >= 9) {
                        int bookingId = Integer.parseInt(parts[0].trim());
                        String movieName = parts[1].trim();
                        String customerName = parts[2].trim();
                        String customerEmail = parts[3].trim();
                        int numberOfTickets = Integer.parseInt(parts[4].trim());
                        double totalPrice = Double.parseDouble(parts[5].trim());
                        String bookingDate = parts[6].trim();
                        String bookingTime = parts[7].trim();
                        String showtime = parts[8].trim();
                        String seats = "";
                        String status = null;
                        
                        // Check if seats field exists (10th field)
                        if (parts.length >= 10) {
                            seats = parts[9].trim();
                        }
                        
                        // Check if status field exists (11th field)
                        if (parts.length >= 11) {
                            status = parts[10].trim();
                        }
                        
                        Booking booking;
                        if (status != null && !status.isEmpty()) {
                            // Has status field
                            booking = new Booking(bookingId, movieName, customerName, customerEmail,
                                                numberOfTickets, totalPrice, bookingDate, bookingTime, 
                                                showtime, seats, status);
                        } else {
                            // No status field, use default
                            booking = new Booking(bookingId, movieName, customerName, customerEmail,
                                                numberOfTickets, totalPrice, bookingDate, bookingTime, 
                                                showtime, seats);
                        }
                        bookings.add(booking);
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing booking line: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading bookings file: " + e.getMessage());
        }

        return bookings;
    }

    // Method to update booking in CSV file
    public static void updateBookingInFile(Booking updatedBooking) {
        ArrayList<Booking> allBookings = loadAllBookings();

        // Find and update the booking
        for (int i = 0; i < allBookings.size(); i++) {
            if (allBookings.get(i).getBookingId() == updatedBooking.getBookingId()) {
                allBookings.set(i, updatedBooking);
                break;
            }
        }

        // Write all bookings back to file
        saveAllBookings(allBookings);
    }

    // Save all bookings to CSV file
    private static void saveAllBookings(ArrayList<Booking> bookings) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
            // Write all bookings (no header needed)
            for (Booking booking : bookings) {
                writer.println(booking.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }

    // Method to display all bookings
    public void viewAllBookings() {
        System.out.println("\n=== ALL BOOKINGS ===");

        ArrayList<Booking> allBookings = loadAllBookings();

        if (allBookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        System.out.println("Total Bookings: " + allBookings.size());
        System.out.println("─────────────────────────────────────────────────────────────────");

        for (Booking booking : allBookings) {
            displayBookingDetails(booking);
            System.out.println("─────────────────────────────────────────────────────────────────");
        }
    }

    // Method to view bookings by email
    public void viewBookingsByEmail() {
        System.out.print("\nEnter your email to view bookings: ");
        String email = scanner.nextLine().trim();

        ArrayList<Booking> allBookings = loadAllBookings();
        ArrayList<Booking> userBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            if (booking.getCustomerEmail().equalsIgnoreCase(email)) {
                userBookings.add(booking);
            }
        }

        System.out.println("\n=== BOOKINGS FOR " + email + " ===");

        if (userBookings.isEmpty()) {
            System.out.println("No bookings found for this email.");
            return;
        }

        System.out.println("Total Bookings: " + userBookings.size());
        System.out.println("─────────────────────────────────────────────────────────────────");

        for (Booking booking : userBookings) {
            displayBookingDetails(booking);
            System.out.println("─────────────────────────────────────────────────────────────────");
        }
    }

    // Method to search booking by ID
    public void searchBookingById() {
        System.out.print("\nEnter Booking ID: ");

        try {
            int bookingId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Booking foundBooking = findBookingById(bookingId);

            if (foundBooking != null) {
                System.out.println("\n=== BOOKING FOUND ===");
                System.out.println("─────────────────────────────────────────────────────────────────");
                displayBookingDetails(foundBooking);
                System.out.println("─────────────────────────────────────────────────────────────────");
            } else {
                System.out.println("Booking ID " + bookingId + " not found.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input! Please enter a valid booking ID.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    // Helper method to find booking by ID
    public static Booking findBookingById(int bookingId) {
        ArrayList<Booking> allBookings = loadAllBookings();

        for (Booking booking : allBookings) {
            if (booking.getBookingId() == bookingId) {
                return booking;
            }
        }
        return null;
    }

    // Helper method to display booking details
    private void displayBookingDetails(Booking booking) {
        System.out.println("Booking ID      : " + booking.getBookingId());
        System.out.println("Movie           : " + booking.getMovieName());
        System.out.println("Tickets         : " + booking.getNumberOfTickets());
        String seatsStr = booking.getSeats();
        if (seatsStr != null && !seatsStr.isEmpty()) {
            System.out.println("Seats           : " + seatsStr.replace(";", ", "));
        }
        System.out.println("Price per Ticket: $" + String.format("%.2f", booking.getTicketPrice()));
        System.out.println("Total Price     : $" + String.format("%.2f", booking.getTotalPrice()));
        System.out.println("Customer Name   : " + booking.getCustomerName());
        System.out.println("Email           : " + booking.getCustomerEmail());
        System.out.println("Showtime        : " + booking.getShowtime());
        System.out.println("Status          : " + booking.getStatus());
        System.out.println("Booking Date    : " + booking.getBookingDate());
    }

    // Method to show booking menu
    public void showBookingsMenu() {
        while (true) {
            System.out.println("\n=== MY BOOKINGS MENU ===");
            System.out.println("1. View My Bookings (by Email)");
            System.out.println("2. Back to Main Menu");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        viewBookingsByEmail();
                        break;
                    case 2:
                        return; // Exit to main menu
                    default:
                        System.out.println("Invalid choice. Please select 1-2.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Method to get all bookings (for other classes to access)
    public static ArrayList<Booking> getAllBookings() {
        return loadAllBookings();
    }

    // Added method for compatibility with main.java
    public void viewMyBookings() {
        showBookingsMenu();
    }
}
