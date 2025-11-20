import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class My_Bookings {
    private Scanner scanner;
    private MovieData movieData; // New field for MovieData

    // CSV file path
    private static final String BOOKINGS_FILE = "movie_bookings.csv";

    // Inner class to represent a booking
    public static class Booking {
        private int bookingId;
        private String alphanumericBookingId; // New field for alphanumeric ID
        private String movieId; 
        private String movieTitle;
        private String movieGenre;
        private String customerName;
        private String customerEmail;
        private int numberOfTickets;
        private double totalPrice;
        private String bookingDate;
        private String bookingTime;
        private String showtime;
        private String seats; // Seat coordinates (e.g., "A1;B5;J10")
        private String status; // "Confirmed", "Cancelled"

        // Constructor for new format (with numerical ID, alphanumeric ID, movieId, movieTitle, movieGenre)
        public Booking(int bookingId, String alphanumericBookingId, String movieId, String movieTitle, String movieGenre, String customerName, String customerEmail,
                       int numberOfTickets, double totalPrice, String bookingDate, 
                       String bookingTime, String showtime, String seats) {
            this.bookingId = bookingId;
            this.alphanumericBookingId = alphanumericBookingId;
            this.movieId = movieId;
            this.movieTitle = movieTitle;
            this.movieGenre = movieGenre;
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

        // Constructor with status
        public Booking(int bookingId, String alphanumericBookingId, String movieId, String movieTitle, String movieGenre, String customerName, String customerEmail,
                       int numberOfTickets, double totalPrice, String bookingDate, 
                       String bookingTime, String showtime, String seats, String status) {
            this.bookingId = bookingId;
            this.alphanumericBookingId = alphanumericBookingId;
            this.movieId = movieId;
            this.movieTitle = movieTitle;
            this.movieGenre = movieGenre;
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
        public String getAlphanumericBookingId() { return alphanumericBookingId; }
        public String getMovieId() { return movieId; }
        public String getMovieTitle() { return movieTitle; }
        public String getMovieGenre() { return movieGenre; }
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
            if (this.numberOfTickets > 0) { // Avoid division by zero
                double pricePerTicket = this.totalPrice / this.numberOfTickets;
                this.numberOfTickets = numberOfTickets;
                this.totalPrice = numberOfTickets * pricePerTicket;
            } else { // Handle case where initial tickets were 0
                this.numberOfTickets = numberOfTickets;
                // totalPrice would need to be recalculated based on a default ticket price or passed in
            }
        }

        // Convert booking to CSV line
        public String toCSV() {
            String seatsStr = seats != null ? seats : "";
            // Ensure all fields are present for consistency
            return bookingId + "," +
                   alphanumericBookingId + "," + // Include alphanumeric ID
                   movieId + "," +
                   movieTitle + "," +
                   movieGenre + "," +
                   customerName + "," +
                   customerEmail + "," +
                   numberOfTickets + "," +
                   String.format("%.2f", totalPrice) + "," +
                   bookingDate + "," +
                   bookingTime + "," +
                   showtime + "," +
                   seatsStr + "," +
                   status;
        }
    }

    // Constructor
    public My_Bookings(MovieData movieData, Scanner scanner) {
        this.movieData = movieData;
        this.scanner = scanner;
    }

    // Method to read all bookings from CSV file
    public ArrayList<Booking> loadAllBookings() { // Made non-static
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
                    
                    // Newest format: numerical_ID,alphanumeric_ID,MovieID,MovieTitle,MovieGenre,CustomerName,CustomerEmail,Tickets,TotalPrice,BookingDate,BookingTime,Showtime,Seats,Status
                    // Expected minimum parts for new format: 14 (if status is present) or 13 (if no status)
                    // Old format (from previous refactor): numerical_ID,MovieID,MovieTitle,MovieGenre,CustomerName,CustomerEmail,Tickets,TotalPrice,BookingDate,BookingTime,Showtime,Seats,Status
                    // Even older format: numerical_ID,MovieName,CustomerName,CustomerEmail,Tickets,TotalPrice,BookingDate,BookingTime,Showtime,Seats,Status
                    
                    if (parts.length >= 10) { // Minimum fields for old format (without seats and status)
                        int bookingId = Integer.parseInt(parts[0].trim());
                        String alphanumericBookingId = ""; // Default to empty for old entries
                        String movieId;
                        String movieTitle;
                        String movieGenre;
                        String customerName;
                        String customerEmail;
                        int numberOfTickets;
                        double totalPrice;
                        String bookingDate;
                        String bookingTime;
                        String showtime;
                        String seats = "";
                        String status = "Confirmed"; // Default status

                        // Determine format based on number of parts
                        if (parts.length >= 13) { // Newest format with alphanumericBookingId
                            alphanumericBookingId = parts[1].trim();
                            movieId = parts[2].trim();
                            movieTitle = parts[3].trim();
                            movieGenre = parts[4].trim();
                            customerName = parts[5].trim();
                            customerEmail = parts[6].trim();
                            numberOfTickets = Integer.parseInt(parts[7].trim());
                            totalPrice = Double.parseDouble(parts[8].trim());
                            bookingDate = parts[9].trim();
                            bookingTime = parts[10].trim();
                            showtime = parts[11].trim();
                            seats = parts[12].trim();
                            if (parts.length >= 14) { // Status field
                                status = parts[13].trim();
                            }
                        } else if (parts.length >= 12) { // Old format (from previous refactor) without alphanumeric ID
                            movieId = parts[1].trim();
                            movieTitle = parts[2].trim();
                            movieGenre = parts[3].trim();
                            customerName = parts[4].trim();
                            customerEmail = parts[5].trim();
                            numberOfTickets = Integer.parseInt(parts[6].trim());
                            totalPrice = Double.parseDouble(parts[7].trim());
                            bookingDate = parts[8].trim();
                            bookingTime = parts[9].trim();
                            showtime = parts[10].trim();
                            seats = parts[11].trim();
                            if (parts.length >= 13) { // Status field
                                status = parts[12].trim();
                            }
                        } else { // Even older format (MovieName directly)
                            // We need to infer movieId and movieGenre from movieTitle
                            movieTitle = parts[1].trim(); // Old MovieName is now movieTitle
                            movieId = ""; // No movieId in old format
                            movieGenre = ""; // No movieGenre in old format

                            // Try to find movie_id and genre_prefix using movieTitle from MovieData
                            // This is a best-effort attempt for old records.
                            MovieData.Movie foundMovie = null;
                            for (MovieData.Genre genre : movieData.getMenuNumberToGenreMapping().values()) {
                                for (MovieData.Movie movie : movieData.getMoviesByGenrePrefix(genre.prefix)) {
                                    if (movie.title.equalsIgnoreCase(movieTitle)) {
                                        foundMovie = movie;
                                        break;
                                    }
                                }
                                if (foundMovie != null) break;
                            }

                            if (foundMovie != null) {
                                movieId = foundMovie.movieId;
                                movieGenre = movieData.getGenreByPrefix(foundMovie.genrePrefix).name;
                            } else {
                                // If not found, log a warning or leave as empty
                                System.err.println("Warning: Could not infer movieId/movieGenre for old booking with title: " + movieTitle);
                            }

                            customerName = parts[2].trim();
                            customerEmail = parts[3].trim();
                            numberOfTickets = Integer.parseInt(parts[4].trim());
                            totalPrice = Double.parseDouble(parts[5].trim());
                            bookingDate = parts[6].trim();
                            bookingTime = parts[7].trim();
                            showtime = parts[8].trim();
                            
                            if (parts.length >= 10) { // Seats in old format
                                seats = parts[9].trim();
                            }
                            if (parts.length >= 11) { // Status in old format
                                status = parts[10].trim();
                            }
                        }
                        
                        bookings.add(new Booking(bookingId, alphanumericBookingId, movieId, movieTitle, movieGenre, customerName, customerEmail,
                                                numberOfTickets, totalPrice, bookingDate, bookingTime, 
                                                showtime, seats, status));
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing booking line (skipping): " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading bookings file: " + e.getMessage());
        }

        return bookings;
    }

    // Method to update booking in CSV file
    public void updateBookingInFile(Booking updatedBooking) { // Made non-static
        ArrayList<Booking> allBookings = loadAllBookings();

        // Find and update the booking
        for (int i = 0; i < allBookings.size(); i++) {
            // Updated to check alphanumericBookingId as well, if available
            if (allBookings.get(i).getBookingId() == updatedBooking.getBookingId() &&
                (!updatedBooking.getAlphanumericBookingId().isEmpty() && allBookings.get(i).getAlphanumericBookingId().equals(updatedBooking.getAlphanumericBookingId()))
            ) {
                allBookings.set(i, updatedBooking);
                break;
            } else if (allBookings.get(i).getBookingId() == updatedBooking.getBookingId() && updatedBooking.getAlphanumericBookingId().isEmpty()) {
                // Fallback for old bookings without alphanumeric ID, match only by numerical ID
                 allBookings.set(i, updatedBooking);
                 break;
            }
        }

        // Write all bookings back to file
        saveAllBookings(allBookings);
    }

    // Save all bookings to CSV file
    private void saveAllBookings(ArrayList<Booking> bookings) { // Made non-static
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
            // For old format, if alphanumericBookingId is empty, it's not a match for user input
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

    // Method to search booking by alphanumeric ID (now primary)
    public void searchBookingById() { // Renamed from searchBookingByAlphanumericId
        System.out.print("\nPlease enter your ID: ");
        String alphanumericBookingId = scanner.nextLine().trim();

        Booking foundBooking = findBookingById(alphanumericBookingId); // Corrected call

        if (foundBooking != null) {
            System.out.println("\n=== BOOKING FOUND ===");
            System.out.println("─────────────────────────────────────────────────────────────────");
            displayBookingDetails(foundBooking);
            System.out.println("─────────────────────────────────────────────────────────────────");
        } else {
            System.out.println("Booking ID " + alphanumericBookingId + " not found.");
        }
    }

    // Helper method to find booking by alphanumeric ID (now primary)
    public Booking findBookingById(String alphanumericBookingId) { // Renamed from findBookingByAlphanumericId
        ArrayList<Booking> allBookings = loadAllBookings();

        for (Booking booking : allBookings) {
            if (!booking.getAlphanumericBookingId().isEmpty() && booking.getAlphanumericBookingId().equalsIgnoreCase(alphanumericBookingId)) {
                return booking;
            }
        }
        return null;
    }

    // Helper method to display booking details
    private void displayBookingDetails(Booking booking) {
        // System.out.println("Numerical Booking ID: " + booking.getBookingId()); // Removed
        System.out.println("Booking ID: " + booking.getAlphanumericBookingId()); // Changed label and kept only alphanumeric
        System.out.println("Movie ID        : " + booking.getMovieId());
        System.out.println("Movie Title     : " + booking.getMovieTitle());
        System.out.println("Movie Genre     : " + booking.getMovieGenre());
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
            System.out.println("2. Search Booking by ID"); // Renamed and re-numbered
            System.out.println("3. Back to Main Menu"); // Re-numbered
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        viewBookingsByEmail();
                        break;
                    case 2:
                        searchBookingById(); // Corrected call
                        break;
                    case 3: // Re-numbered
                        return; // Exit to main menu
                    default:
                        System.out.println("Invalid choice. Please select 1-3."); // Adjusted message
                }
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Method to get all bookings (for other classes to access)
    // This now calls the non-static method
    public ArrayList<Booking> getAllBookings() { 
        return loadAllBookings();
    }

    // Added method for compatibility with main.java
    public void viewMyBookings() {
        showBookingsMenu();
    }
}

