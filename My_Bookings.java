public class My_Bookings {
    public void viewMyBookings() {
    System.out.println("Viewing my bookings...");
        
    private Scanner scanner = new Scanner(System.in);

    // CSV file path
    private static final String BOOKINGS_FILE = "bookings.csv";
    private static final String COUNTER_FILE = "booking_counter.txt";

    // Inner class to represent a booking
    public static class Booking {
        private int bookingId;
        private String movieName;
        private int numberOfTickets;
        private double ticketPrice;
        private double totalPrice;
        private String customerName;
        private String customerEmail;
        private String status; // "Confirmed", "Cancelled"
        private String bookingDate;

        public Booking(String movieName, int numberOfTickets, double ticketPrice,
                       String customerName, String customerEmail) {
            this.bookingId = getNextBookingId();
            this.movieName = movieName;
            this.numberOfTickets = numberOfTickets;
            this.ticketPrice = ticketPrice;
            this.totalPrice = numberOfTickets * ticketPrice;
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.status = "Confirmed";
            this.bookingDate = java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // Constructor for loading from CSV
        public Booking(int bookingId, String movieName, int numberOfTickets, double ticketPrice,
                       double totalPrice, String customerName, String customerEmail,
                       String status, String bookingDate) {
            this.bookingId = bookingId;
            this.movieName = movieName;
            this.numberOfTickets = numberOfTickets;
            this.ticketPrice = ticketPrice;
            this.totalPrice = totalPrice;
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.status = status;
            this.bookingDate = bookingDate;
        }

        // Getters
        public int getBookingId() { return bookingId; }
        public String getMovieName() { return movieName; }
        public int getNumberOfTickets() { return numberOfTickets; }
        public double getTicketPrice() { return ticketPrice; }
        public double getTotalPrice() { return totalPrice; }
        public String getCustomerName() { return customerName; }
        public String getCustomerEmail() { return customerEmail; }
        public String getStatus() { return status; }
        public String getBookingDate() { return bookingDate; }

        // Setters
        public void setStatus(String status) { this.status = status; }
        public void setNumberOfTickets(int numberOfTickets) {
            this.numberOfTickets = numberOfTickets;
            this.totalPrice = numberOfTickets * ticketPrice;
        }

        // Convert booking to CSV line
        public String toCSV() {
            return bookingId + "," +
                    escapeCSV(movieName) + "," +
                    numberOfTickets + "," +
                    ticketPrice + "," +
                    totalPrice + "," +
                    escapeCSV(customerName) + "," +
                    escapeCSV(customerEmail) + "," +
                    status + "," +
                    escapeCSV(bookingDate);
        }

        // Escape special characters in CSV
        private String escapeCSV(String value) {
            if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                return "\"" + value.replace("\"", "\"\"") + "\"";
            }
            return value;
        }
    }

    // Initialize CSV file with header if it doesn't exist
    private static void initializeCSVFile() {
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
                writer.println("BookingID,MovieName,NumberOfTickets,TicketPrice,TotalPrice,CustomerName,CustomerEmail,Status,BookingDate");
            } catch (IOException e) {
                System.out.println("Error initializing CSV file: " + e.getMessage());
            }
        }
    }

    // Get next booking ID from counter file
    private static synchronized int getNextBookingId() {
        int nextId = 1000; // Default starting ID

        // Read current counter
        File counterFile = new File(COUNTER_FILE);
        if (counterFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(COUNTER_FILE))) {
                String line = reader.readLine();
                if (line != null) {
                    nextId = Integer.parseInt(line.trim());
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println("Error reading counter file: " + e.getMessage());
            }
        }

        // Increment and save new counter
        nextId++;
        try (PrintWriter writer = new PrintWriter(new FileWriter(COUNTER_FILE))) {
            writer.println(nextId);
        } catch (IOException e) {
            System.out.println("Error writing counter file: " + e.getMessage());
        }

        return nextId;
    }

    // Method to add a booking to the CSV file
    public static void addBooking(String movieName, int numberOfTickets, double ticketPrice,
                                  String customerName, String customerEmail) {
        initializeCSVFile();

        Booking newBooking = new Booking(movieName, numberOfTickets, ticketPrice,
                customerName, customerEmail);

        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKINGS_FILE, true))) {
            writer.println(newBooking.toCSV());
            System.out.println("\n✓ Booking recorded with ID: " + newBooking.getBookingId());
        } catch (IOException e) {
            System.out.println("Error saving booking: " + e.getMessage());
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
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = parseCSVLine(line);
                    if (parts.length >= 9) {
                        Booking booking = new Booking(
                                Integer.parseInt(parts[0]),      // bookingId
                                parts[1],                        // movieName
                                Integer.parseInt(parts[2]),      // numberOfTickets
                                Double.parseDouble(parts[3]),    // ticketPrice
                                Double.parseDouble(parts[4]),    // totalPrice
                                parts[5],                        // customerName
                                parts[6],                        // customerEmail
                                parts[7],                        // status
                                parts[8]                         // bookingDate
                        );
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

    // Parse CSV line handling quoted values
    private static String[] parseCSVLine(String line) {
        ArrayList<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());

        return result.toArray(new String[0]);
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
            // Write header
            writer.println("BookingID,MovieName,NumberOfTickets,TicketPrice,TotalPrice,CustomerName,CustomerEmail,Status,BookingDate");

            // Write all bookings
            for (Booking booking : bookings) {
                writer.println(booking.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }

    // Method to display all bookings
    public void viewAllBookings() {
        System.out.println("\n=== MY BOOKINGS ===");

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
        System.out.println("Price per Ticket: $" + booking.getTicketPrice());
        System.out.println("Total Price     : $" + booking.getTotalPrice());
        System.out.println("Customer Name   : " + booking.getCustomerName());
        System.out.println("Email           : " + booking.getCustomerEmail());
        System.out.println("Status          : " + booking.getStatus());
        System.out.println("Booking Date    : " + booking.getBookingDate());
    }

    // Method to show booking menu
    public void showBookingsMenu() {
        while (true) {
            System.out.println("\n=== MY BOOKINGS MENU ===");
            System.out.println("1. View All Bookings");
            System.out.println("2. View My Bookings (by Email)");
            System.out.println("3. Search Booking by ID");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        viewAllBookings();
                        break;
                    case 2:
                        viewBookingsByEmail();
                        break;
                    case 3:
                        searchBookingById();
                        break;
                    case 4:
                        return; // Exit to main menu
                    default:
                        System.out.println("Invalid choice. Please select 1-4.");
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
}
