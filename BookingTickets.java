import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class BookingTickets {
    private Scanner scanner = new Scanner(System.in);
    private int bookingIDCounter = 1;
    private String bookingsFile = "movie_bookings.csv";
    
    // Constructor - runs automatically when BookingTickets object is created
    public BookingTickets() 
        {
            System.out.println("=========================================");
            System.out.println("   MOVIE TICKET BOOKING SYSTEM STARTED  ");
            System.out.println("=========================================");
            initializeCSVFile(); // Setup the CSV file for storing bookings
        }
    
    // Initialize the CSV file - creates it if doesn't exist
    private void initializeCSVFile()
    {
        try 
        {
            FileWriter write = new FileWriter(bookingsFile, true);
            write.close();
            System.out.println("✓ Booking system ready - CSV file initialized");
        } catch (IOException e)

        {
            System.out.println("❌ An error occurred while initializing the bookings file: " + e.getMessage());
        }
    }
    
    // Main method for browsing and booking tickets
    public void browseAndBookTickets()
    {
        while (true){
            int user_choice;
            System.out.println("\n" + "=".repeat(50));
            System.out.println("        🎬 BROWSE AND BOOK TICKETS 🎬");
            System.out.println("=".repeat(50));
            System.out.println("1. Inception - $50 per ticket");
            System.out.println("2. Interstellar - $60 per ticket"); 
            System.out.println("3. The Dark Knight - $55 per ticket");
            System.out.println("4. Dunkirk - $45 per ticket");
            System.out.println("5. Return to Main Menu");
            System.out.println("-".repeat(50));
            System.out.print("🎭 Select a movie (1-5): ");
         
                user_choice = getValidnumber();
                if (user_choice == 5){
                    System.out.println("\n" + "=".repeat(40));
                    System.out.println("   RETURNING TO MAIN MENU...");
                    System.out.println("=".repeat(40));
                    return;
                }
                if (user_choice == -1){
                    continue;
                }

            String movie = "";
            double PriceperTicket = 0.0;
            int available_tickets = 100; // Assume each movie has 100 tickets available
            String Showtime = "";
            
            // Handle movie selection based on user choice
            switch (user_choice)
            {
                case 1:
                    movie = "Inception";
                    PriceperTicket = 50.0;
                    Showtime = "1:00 PM";
                    displayMovieDetails("Inception - A mind-bending thriller about dream infiltration and subconscious security.", 
                                    PriceperTicket, "3D and IMAX", available_tickets, Showtime);
                    break;
                case 2:
                    movie = "Interstellar";
                    PriceperTicket = 60.0;
                    Showtime = "4:00 PM";
                    displayMovieDetails("Interstellar - An epic space odyssey through wormholes to save humanity from extinction.", 
                                    PriceperTicket, "IMAX", available_tickets, Showtime);
                    break;  
                case 3:
                    movie = "The Dark Knight";
                    PriceperTicket = 55.0;
                    Showtime = "7:00 PM";
                    displayMovieDetails("The Dark Knight - Batman faces his greatest challenge against the chaotic Joker in Gotham City.", 
                                    PriceperTicket, "IMAX", available_tickets, Showtime);
                    break;
                case 4:
                    movie = "Dunkirk";
                    PriceperTicket = 45.0;
                    Showtime = "9:00 PM";
                    displayMovieDetails("Dunkirk - A intense war film depicting the miraculous evacuation of Allied soldiers from Dunkirk beaches.", 
                                    PriceperTicket, "IMAX", available_tickets, Showtime);
                    break;

                default:
                    System.out.println("\n❌ Invalid choice. Please select a valid movie (1-5).");
                    continue;
                }
                
                System.out.println("\n" + "-".repeat(50));
                System.out.print("📝 Do you want to book tickets for '" + movie + "'? (yes/no): ");
                String bookchoice = getYesNoInput();

                // If user wants to book, proceed with booking process
                if (bookchoice.equalsIgnoreCase("yes")){
                    book_Tickets(movie, PriceperTicket, available_tickets, Showtime);
                }
                
                System.out.println("\n" + "=".repeat(50));
                System.out.print("🔍 Do you want to browse more movies? (yes/no): ");
                String continueChoice = getYesNoInput();
                
                if (!continueChoice.equalsIgnoreCase("yes")){
                    System.out.println("\n" + "=".repeat(50));
                    System.out.println("   👋 Thank you for using our booking system!");
                    System.out.println("=".repeat(50));
                    return;
                }
           }
           
    }
    
    

    // Save booking details to CSV file
    public void saveToCSV(String MovieName, String CustomerName, String CustomerEmail, int Tickets, double TotalPrice, String Showtime){
        try {
                FileWriter write = new FileWriter(bookingsFile, true);
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                String BookingDate = now.format(dateFormatter);
                String BookingTime = now.format(timeFormatter);
                
                // Format all booking data into a CSV line
                String Data_Line = String.format("%d,%s,%s,%s,%d,%.2f,%s,%s,%s",
                bookingIDCounter++,
                        MovieName,
                        CustomerName,
                        CustomerEmail,
                        Tickets,
                        TotalPrice,
                        BookingDate,
                        BookingTime,
                        Showtime
                );

            write.write(Data_Line + "\n");
            write.close();
        }   catch (IOException e) {
            System.out.println("\n❌ An error occurred while saving the booking: " + e.getMessage());
        }
    }
    
    // Handle the ticket booking process
    private void book_Tickets(String MovieName, double PriceperTicket, int available_tickets, String Showtime){
        int number_of_tickets;
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("   🎟️  BOOKING TICKETS FOR: " + MovieName.toUpperCase());
        System.out.println("=".repeat(50));
        
        // Get and validate number of tickets
        while (true)
        {
            System.out.print("🔢 How many tickets would you like to book? ");
            number_of_tickets = getValidnumber();
            if (number_of_tickets <= 0)
            {
                System.out.println("❌ Please enter a valid number of tickets (must be positive).");
                continue;
            }
             if (number_of_tickets > available_tickets)
            {
                System.out.println("❌ Sorry, only " + available_tickets + " tickets are available.");
                continue;
            }
                  break;
            }
            
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
        System.out.println("🎬 Movie: " + MovieName);
        System.out.println("🎟️  Number of Tickets: " + number_of_tickets);
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
            System.out.println("\n" + "=".repeat(40));
            System.out.println("   🚫 BOOKING CANCELLED");
            System.out.println("   Returning to movie browsing...");
            System.out.println("=".repeat(40));
            return;
        }

        if (confirm.equalsIgnoreCase("yes")){
            saveToCSV(MovieName, CustomerName, CustomerEmail, number_of_tickets, TotalPrice, Showtime);
            System.out.println("\n" + "🎉".repeat(20));
            System.out.println("   🎉 BOOKING CONFIRMED!");
            System.out.println("   📧 Tickets have been sent to: " + CustomerEmail);
            System.out.println("🎉".repeat(20));
        }
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
    public void displayMovieDetails(String movie, double PriceperTicket, String format, int available_tickets, String Showtime)
    {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("   🎥 MOVIE DETAILS");
        System.out.println("=".repeat(50));
        System.out.println("📖 " + movie);
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
    
    // Main method - program entry point
    public static void main(String[] args) {
        System.out.println("\n" );
        System.out.println("  WELCOME TO MOVIE TICKET BOOKING SYSTEM");
        System.out.println(" ");
        
        BookingTickets bookingSystem = new BookingTickets();  // Create booking system object
        bookingSystem.browseAndBookTickets();  // Start the booking interface
        
        System.out.println("\n");
        System.out.println("THANK YOU FOR USING OUR SERVICE!");
        System.out.println(" ");
    }
}