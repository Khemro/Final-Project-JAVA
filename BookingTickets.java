import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;


public class BookingTickets {
    private Scanner scanner;
    private MovieData movieData; // New field for MovieData
    private int bookingIDCounter; // Will be initialized by loading from CSV
    private String bookingsFile = "movie_bookings.csv";
    private Map<String, Boolean> seatingMap = new HashMap<>(); // Key: seat coordinate (e.g., "A1"), Value: true if booked, false if available
    private Set<String> usedAlphanumericBookingIds = new HashSet<>(); // To ensure unique alphanumeric IDs
    private Random random = new Random(); // For generating random alphanumeric IDs

    
    // Constructor - runs automatically when BookingTickets object is created
    public BookingTickets(MovieData movieData, Scanner scanner) 
        {
            this.movieData = movieData; // Initialize MovieData
            this.scanner = scanner; // Initialize scanner
            bookingIDCounter = loadLastBookingId(); // Initialize counter from CSV
            initializeCSVFile(); // Setup the CSV file for storing bookings
            loadUsedAlphanumericBookingIds(); // Load existing alphanumeric IDs to ensure uniqueness
            initializeSeatingMap(); // Initialize 10×10 seating map
            loadBookedSeatsFromCSV(); // Load existing bookings to mark seats as booked
        }
    
    // Initialize the CSV file - creates it if doesn't exist
    private void initializeCSVFile()
    {
        try 
        {
            FileWriter write = new FileWriter(bookingsFile, true);
            write.close();
        } catch (IOException e)

        {
            System.out.println("❌ An error occurred while initializing the bookings file: " + e.getMessage());
        }
    }

    // Load the last used booking ID from the CSV file
    private int loadLastBookingId() {
        int maxBookingId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(bookingsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        int bookingId = Integer.parseInt(parts[0].trim());
                        if (bookingId > maxBookingId) {
                            maxBookingId = bookingId;
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore lines with invalid booking ID format
                    System.err.println("Warning: Invalid numerical booking ID format in CSV: " + line);
                }
            }
        } catch (IOException e) {
            // File might not exist or be empty, which is fine for initial load
            // System.err.println("Error reading bookings file for last ID: " + e.getMessage());
        }
        return maxBookingId + 1; // Return the next available booking ID
    }

    // Load existing alphanumeric booking IDs from CSV
    private void loadUsedAlphanumericBookingIds() {
        try (BufferedReader reader = new BufferedReader(new FileReader(bookingsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    // New format: numerical_booking_id,alphanumeric_booking_id,MovieId,MovieTitle,MovieGenre,...
                    // Check if the alphanumeric booking ID exists (second column)
                    if (parts.length >= 2) {
                        usedAlphanumericBookingIds.add(parts[1].trim());
                    }
                } catch (Exception e) {
                    System.err.println("Warning: Error reading alphanumeric booking ID from CSV line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            // File might not exist or be empty, which is fine
        }
    }
    
    // Initialize 10×10 seating map (A1-A10, B1-B10, ..., J1-J10)
    private void initializeSeatingMap()
    {
        for (char row = 'A'; row <= 'J'; row++) 
        {
            for (int col = 1; col <= 10; col++) 
            {
                String seat = String.valueOf(row) + col;
                seatingMap.put(seat, false); // false means available
            }
        }
    }
    
    // Display seating map
    private void displaySeatingMap()
    {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("   🪑 THEATER SEATING MAP (10×10)");
        System.out.println("=".repeat(60));
        System.out.println("   Legend: [ ] = Available, [X] = Booked");
        System.out.println("   " + " ".repeat(10) + "SCREEN");
        System.out.println("   " + "=".repeat(50));
        
        // Print column numbers
        System.out.print("   ");
        for (int col = 1; col <= 10; col++) 
        {
            System.out.print(String.format("%4d", col));
        }
        System.out.println();
        
        // Print rows with seat status
        for (char row = 'A'; row <= 'J'; row++) 
        {
            System.out.print(" " + row + " ");
            for (int col = 1; col <= 10; col++) 
            {
                String seat = String.valueOf(row) + col;
                boolean isBooked = seatingMap.get(seat);
                if (isBooked) 
                {
                    System.out.print("[X] ");
                } 
                else 
                {
                    System.out.print("[ ] ");
                }
            }
            System.out.println();
        }
        System.out.println("=".repeat(60));
    }
    
    // Get available seats count
    private int getAvailableSeatsCount()
    {
        int count = 0;
        for (boolean booked : seatingMap.values()) 
        {
            if (!booked) 
            {
                count++;
            }
        }
        return count;
    }
    
    // Load booked seats from existing bookings in CSV file
    private void loadBookedSeatsFromCSV()
    {
        try 
        {
            BufferedReader reader = new BufferedReader(new FileReader(bookingsFile));
            String line;
            
            while ((line = reader.readLine()) != null) 
            {
                try 
                {
                    String[] parts = line.split(",");
                    // Format (new): numerical_booking_id,alphanumeric_booking_id,MovieId,MovieTitle,MovieGenre,CustomerName,CustomerEmail,Tickets,TotalPrice,BookingDate,BookingTime,Showtime,Seats[,Status]
                    // We need at least 12 parts for a booking with seats in the new format to extract seats
                    if (parts.length >= 12) 
                    {
                        String seatsStr = parts[11].trim(); // Seats are now the 12th column (index 11)
                        if (!seatsStr.isEmpty()) 
                        {
                            // Seats are separated by semicolon
                            String[] seats = seatsStr.split(";");
                            for (String seat : seats) 
                            {
                                seat = seat.trim().toUpperCase();
                                if (isValidSeat(seat)) 
                                {
                                    // Check if booking is not cancelled (if status field exists)
                                    boolean isCancelled = false;
                                    // Status is now the 13th column (index 12)
                                    if (parts.length >= 13) 
                                    {
                                        String status = parts[12].trim();
                                        isCancelled = status.equalsIgnoreCase("Cancelled");
                                    }
                                    
                                    // Only mark as booked if not cancelled
                                    if (!isCancelled) 
                                    {
                                        seatingMap.put(seat, true);
                                    }
                                }
                            }
                        }
                    }
                     // Old format: BookingID,MovieID,GenreName,CustomerName,CustomerEmail,Tickets,TotalPrice,Date,Time,Showtime,Seats[,Status]
                    // If line matches old format, it will have fewer parts.
                    else if (parts.length >= 10) { // For old format entries
                        String seatsStr = parts[9].trim();
                        if (!seatsStr.isEmpty()) {
                            String[] seats = seatsStr.split(";");
                            for (String seat : seats) {
                                seat = seat.trim().toUpperCase();
                                if (isValidSeat(seat)) {
                                    boolean isCancelled = false;
                                    if (parts.length >= 11) {
                                        String status = parts[10].trim();
                                        isCancelled = status.equalsIgnoreCase("Cancelled");
                                    }
                                    if (!isCancelled) {
                                        seatingMap.put(seat, true);
                                    }
                                }
                            }
                        }
                    }
                } 
                catch (Exception e) 
                {
                    // Skip invalid lines
                    System.err.println("Error processing line for booked seats: " + line + " - " + e.getMessage());
                    continue;
                }
            }
            reader.close();
        } 
        catch (IOException e) 
        {
            // File might not exist yet, which is okay
        }
    }
    
    // Generate a unique alphanumeric booking ID (3 letters + 3 digits)
    private String generateUniqueAlphanumericBookingId() {
        String id;
        do {
            StringBuilder sb = new StringBuilder();
            // 3 random uppercase letters
            for (int i = 0; i < 3; i++) {
                sb.append((char) ('A' + random.nextInt(26)));
            }
            // 3 random digits
            sb.append(String.format("%03d", random.nextInt(1000))); // %03d ensures 3 digits, padding with leading zeros

            id = sb.toString();
        } while (usedAlphanumericBookingIds.contains(id)); // Check if ID is already used

        usedAlphanumericBookingIds.add(id); // Add to the set of used IDs
        return id;
    }

    // Main method for booking a specific movie
    public boolean bookMovie(MovieData.Movie selectedMovie)
    {
        String selectedMovieId = selectedMovie.movieId;
        String selectedMovieTitle = selectedMovie.title;
        String selectedMovieGenre = movieData.getGenreByPrefix(selectedMovie.genrePrefix).name;

        double PriceperTicket = 50.0; // Default price, can be customized per movie
        int available_tickets = getAvailableSeatsCount(); // Assuming 100 seats in total
        
        // Generate showtime (using fixed showtimes for now, could be dynamic)
        String[] showtimes = {"1:00 PM", "4:00 PM", "7:00 PM", "9:30 PM"};
        // For simplicity, let's just pick one or make it user choice later
        String Showtime = showtimes[0]; 
            
        displayMovieDetails(selectedMovieTitle, PriceperTicket, "Standard", available_tickets, Showtime);
        
        System.out.println("\n" + "-".repeat(50));
        System.out.println("🕐 Showtime: " + Showtime);
        System.out.println("-".repeat(50));
        System.out.print("📝 Do you want to book tickets for '" + selectedMovieTitle + "' (" + selectedMovieId + ") at " + Showtime + "? (yes/no): ");
        String bookchoice = getYesNoInput();

        // If user wants to book, proceed with booking process
        if (bookchoice.equalsIgnoreCase("yes")){
            // Generate unique alphanumeric booking ID
            String alphanumericBookingId = generateUniqueAlphanumericBookingId();
            return book_Tickets_process(alphanumericBookingId, selectedMovieId, selectedMovieTitle, selectedMovieGenre, PriceperTicket, available_tickets, Showtime);
        } else {
            System.out.println("\nBooking cancelled. Returning to main menu.");
            return false; // User cancelled booking
        }
    }
    
    // Validate seat coordinate (e.g., A1, B5, J10)
    private boolean isValidSeat(String seat)
    {
        if (seat == null || seat.length() < 2 || seat.length() > 3)
        {
            return false;
        }
        
        char row = Character.toUpperCase(seat.charAt(0));
        if (row < 'A' || row > 'J')
        {
            return false;
        }
        
        try 
        {
            int col = Integer.parseInt(seat.substring(1));
            if (col < 1 || col > 10)
            {
                return false;
            }
        } 
        catch (NumberFormatException e)
        {
            return false;
        }
        
        return true;
    }
    
    // Check if seat is available
    private boolean isSeatAvailable(String seat)
    {
        if (!isValidSeat(seat))
        {
            return false;
        }
        Boolean booked = seatingMap.get(seat.toUpperCase());
        return booked != null && !booked;
    }
    
    // Book a seat
    private void bookSeat(String seat)
    {
        seatingMap.put(seat.toUpperCase(), true);
    }
    
    // Save booking details to CSV file
    public void saveToCSV(String alphanumericBookingId, String MovieId, String MovieTitle, String MovieGenre, String CustomerName, String CustomerEmail, int Tickets, double TotalPrice, String Showtime, String Seats){
        try {
                FileWriter write = new FileWriter(bookingsFile, true);
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                String BookingDate = now.format(dateFormatter);
                String BookingTime = now.format(timeFormatter);
                
                // Corrected format string with 13 placeholders matching the 13 arguments
                String Data_Line = String.format("%d,%s,%s,%s,%s,%s,%s,%d,%.2f,%s,%s,%s,%s",
                bookingIDCounter++,            // 1: %d (numerical booking ID)
                        alphanumericBookingId, // 2: %s (alphanumeric booking ID)
                        MovieId,               // 3: %s
                        MovieTitle,            // 4: %s
                        MovieGenre,            // 5: %s
                        CustomerName,          // 6: %s
                        CustomerEmail,         // 7: %s
                        Tickets,               // 8: %d
                        TotalPrice,            // 9: %.2f
                        BookingDate,           // 10: %s
                        BookingTime,           // 11: %s
                        Showtime,              // 12: %s
                        Seats                  // 13: %s
                );

            write.write(Data_Line + "\n");
            write.close();
        }   catch (IOException e) {
            System.out.println("\n❌ An error occurred while saving the booking: " + e.getMessage());
        }
    }
    
    // Handle the ticket booking process
    private boolean book_Tickets_process(String alphanumericBookingId, String MovieId, String MovieTitle, String MovieGenre, double PriceperTicket, int available_tickets, String Showtime){
        int number_of_tickets;
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("   🎟️  BOOKING TICKETS FOR: " + MovieTitle.toUpperCase());
        System.out.println("=".repeat(50));
        
        // Display seating map
        displaySeatingMap();
        
        // Get and validate number of tickets
        while (true)
        {
            System.out.print("🔢 How many tickets would you like to book? ");
            number_of_tickets = getValidnumber();
            if (number_of_tickets <= 0)
            {
                System.out.println("❌ Please enter a valid number of tickets (must be positive).");
                return false; // Exit booking process if invalid tickets
            }
            if (number_of_tickets > available_tickets)
            {
                System.out.println("❌ Sorry, only " + available_tickets + " tickets are available.");
                return false; // Exit booking process if not enough tickets
            }
            break;
        }
        
        // Seat selection
        List<String> selectedSeats = new ArrayList<>();
        System.out.println("\n" + "-".repeat(50));
        System.out.println("   🪑 SEAT SELECTION");
        System.out.println("-".repeat(50));
        System.out.println("Please select " + number_of_tickets + " seat(s).");
        System.out.println("Enter seat coordinates (e.g., A1, B5, J10)");
        
        for (int i = 0; i < number_of_tickets; i++)
        {
            while (true)
            {
                System.out.print("🎫 Select seat " + (i + 1) + " of " + number_of_tickets + ": ");
                String seat = scanner.nextLine().trim().toUpperCase();
                
                if (!isValidSeat(seat))
                {
                    System.out.println("❌ Invalid seat format. Please enter a valid seat (e.g., A1, B5, J10).");
                    continue;
                }
                
                if (!isSeatAvailable(seat))
                {
                    System.out.println("❌ Seat " + seat + " is already booked. Please select another seat.");
                    continue;
                    
                }
                
                if (selectedSeats.contains(seat))
                {
                    System.out.println("❌ Seat " + seat + " is already selected. Please select another seat.");
                    continue;
                }
                
                selectedSeats.add(seat);
                System.out.println("✓ Seat " + seat + " selected.");
                break;
            }
        }
        
        // Book the selected seats
        for (String seat : selectedSeats)
        {
            bookSeat(seat);
        }
        
        String seatsString = String.join(";", selectedSeats);
        
        // Calculate total price
        double TotalPrice = number_of_tickets * PriceperTicket;
        
        // Get customer information
        System.out.println("\n" + "-".repeat(40));
        System.out.println("   👤 CUSTOMER INFORMATION");
        System.out.println("-".repeat(40));
        System.out.print("📛 Please enter your name: ");
        String CustomerName = scanner.nextLine();
        System.out.print("📧 Please enter your email: ");
        String CustomerEmail = scanner.nextLine();
        
        // Display booking summary
        System.out.println("\n" + "=".repeat(50));
        System.out.println("   📋 BOOKING SUMMARY");
        System.out.println("=".repeat(50));
        System.out.println("🆔 Booking ID: " + alphanumericBookingId); // Display new ID
        System.out.println("🎬 Movie: " + MovieTitle + " (" + MovieId + ")");
        System.out.println("🎭 Genre: " + MovieGenre);
        System.out.println("🎟️  Number of Tickets: " + number_of_tickets);
        System.out.println("🪑 Selected Seats: " + seatsString.replace(";", ", "));
        System.out.println("💰 Price per Ticket: $" + PriceperTicket);
        System.out.println("💵 Total Price: $" + TotalPrice);
        System.out.println("👤 Customer Name: " + CustomerName);
        System.out.println("📧 Customer Email: " + CustomerEmail);
        System.out.println("🕐 Showtime: " + Showtime);
        System.out.println("=".repeat(50));

        // Final confirmation
        System.out.print("\n✅ Confirm booking? (yes/no): ");
        String confirm = getYesNoInput();
        
        if (!confirm.equalsIgnoreCase("yes")){
            // Release the seats if booking is cancelled
            for (String seat : selectedSeats)
            {
                seatingMap.put(seat, false);
            }
            // Remove the generated alphanumeric ID from the set as it's not used
            usedAlphanumericBookingIds.remove(alphanumericBookingId); 
            System.out.println("\n" + "=".repeat(40));
            System.out.println("   🚫 BOOKING CANCELLED");
            System.out.println("   Returning to movie browsing...");
            System.out.println("=".repeat(40));
            return false; // User cancelled booking
        }

        if (confirm.equalsIgnoreCase("yes")){
            saveToCSV(alphanumericBookingId, MovieId, MovieTitle, MovieGenre, CustomerName, CustomerEmail, number_of_tickets, TotalPrice, Showtime, seatsString);
            System.out.println("\n");
            System.out.println("   🎉 BOOKING CONFIRMED!");
            System.out.println("   🆔 Your Booking ID: " + alphanumericBookingId); // Display new ID
            System.out.println("   🪑 Your seats: " + seatsString.replace(";", ", "));
            System.out.println("   📧 Tickets have been sent to: " + CustomerEmail);
            System.out.println(" ");
            System.out.println("\n" + "=".repeat(50));
            System.out.print("🔍 Do you want to browse more movies? (yes/no): ");
            String continueChoice = getYesNoInput();
            
            if (!continueChoice.equalsIgnoreCase("yes")){
                System.out.println("\n" + "=".repeat(50));
                System.out.println("   👋 Thank you for using our booking system!");
                System.out.println("=".repeat(50));
                return false; // User does not want to browse more
            }
        }
        return true; // User confirmed booking and wants to browse more
    }
    
    // Validate yes/no input from user
    public String getYesNoInput()
    {
        while (true)
        {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("no")){
                return input;
            }
            else {
                System.out.print("❌ Invalid input. Please enter 'yes' or 'no': ");
            }
        }
    }

    // Display detailed movie information
    public void displayMovieDetails(String movieTitle, double PriceperTicket, String format, int available_tickets, String Showtime)
    {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("   🎥 MOVIE DETAILS");
        System.out.println("=".repeat(50));
        System.out.println("📖 " + movieTitle);
        System.out.println("💰 Price per Ticket: $" + PriceperTicket);
        System.out.println("🎞️  Available Format: " + format);
        System.out.println("🎟️  Available Tickets: " + available_tickets);
        System.out.println("🕐 Showtime: " + Showtime);
        System.out.println("=".repeat(50));
    }   

    // Validate and get integer input from user
    public int getValidnumber()
    {
        int number;
        while (true){
            try {
                number = scanner.nextInt();
                scanner.nextLine(); // Consume newline character
                return number;
            } catch (InputMismatchException e) {
                System.out.print("❌ Invalid input. Please enter a valid number: ");
                scanner.nextLine(); // Clear the invalid input from scanner buffer
            }
        }
    }
}