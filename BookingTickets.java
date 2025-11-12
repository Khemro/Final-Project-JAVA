import java.util.Scanner;
import java.util.InputMismatchException;

public class BookingTickets {
    private Scanner scanner = new Scanner(System.in);
    
    public void browseAndBookTickets() {
        int choice;
        
        while (true) {
            // Display movie menu
            System.out.println("\n=== BROWSE AND BOOK SHOWS ===");
            System.out.println("Which movie do you want to watch?\n");
            System.out.println("1. Inception\n2. The Dark Knight\n3. Interstellar\n4. Tenet\n5. Dunkirk\n");
            System.out.print("Select a movie by entering the corresponding number: ");
            
            // Validate integer input
            choice = getValidIntegerInput();
            if (choice == -1) { // Invalid input
                continue; // Restart the loop
            }
            
            String selectedMovie = "";
            double ticketPrice = 0;
            int availableSeats = 50;
            
            // Display movie details and get booking info
            switch (choice) {
                case 1:
                    selectedMovie = "Inception";
                    ticketPrice = 50.0;
                    displayMovieDetails("Inception - A mind-bending thriller by Christopher Nolan.", 
                                       "1:00 PM", "3D and IMAX", ticketPrice, availableSeats);
                    break;
                    
                case 2:
                    selectedMovie = "The Dark Knight";
                    ticketPrice = 45.0;
                    displayMovieDetails("The Dark Knight - A superhero film directed by Christopher Nolan.", 
                                       "4:00 PM", "IMAX", ticketPrice, availableSeats);
                    break;

                case 3:
                    selectedMovie = "Interstellar";
                    ticketPrice = 55.0;
                    displayMovieDetails("Interstellar - A science fiction film directed by Christopher Nolan.", 
                                       "7:00 PM", "3D and IMAX", ticketPrice, availableSeats);
                    break;

                case 4:
                    selectedMovie = "Tenet";
                    ticketPrice = 40.0;
                    displayMovieDetails("Tenet - A science fiction action-thriller film directed by Christopher Nolan.", 
                                       "9:00 PM", "IMAX", ticketPrice, availableSeats);
                    break;
                    
                case 5:
                    selectedMovie = "Dunkirk";
                    ticketPrice = 35.0;
                    displayMovieDetails("Dunkirk - A war film directed by Christopher Nolan.", 
                                       "11:00 PM", "IMAX", ticketPrice, availableSeats);
                    break;

                default:
                    System.out.println("Invalid choice. Please select a valid movie number (1-5).");
                    continue;
            }
            
            // Ask if user wants to book tickets for this movie
            System.out.print("\nDo you want to book tickets for " + selectedMovie + "? (yes/no): ");
            String bookChoice = getValidYesNoInput();
            
            if (bookChoice.equalsIgnoreCase("yes")) {
                bookTickets(selectedMovie, ticketPrice, availableSeats);
            }
            
            // Ask if user wants to browse more shows
            System.out.print("\nDo you want to browse more shows? (yes/no): ");
            String continueChoice = getValidYesNoInput();
            
            if (!continueChoice.equalsIgnoreCase("yes")) {
                System.out.println("Thank you for using our booking system!");
                break;
            }
        }
    }
    
    // Method to validate integer input
    private int getValidIntegerInput() {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                return input;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number (1-5).");
                scanner.nextLine(); // Clear the invalid input from scanner
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    // Method to validate yes/no input
    private String getValidYesNoInput() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("no")) {
                return input;
            } else {
                System.out.print("Please enter 'yes' or 'no': ");
            }
        }
    }
    
    private void displayMovieDetails(String description, String showtime, String format, double price, int seats) {
        System.out.println("\n=== MOVIE DETAILS ===");
        System.out.println(description);
        System.out.println("Showtimes: " + showtime);
        System.out.println("Available in " + format + " Format. - $" + price);
        System.out.println("Available Seats: " + seats);
    }
    
    private void bookTickets(String movieName, double ticketPrice, int availableSeats) {
        System.out.println("\n=== BOOKING TICKETS FOR " + movieName.toUpperCase() + " ===");
        
        // Get number of tickets with validation - loop until valid number is entered
        int numberOfTickets;
        while (true) {
            System.out.print("How many tickets would you like to book? ");
            numberOfTickets = getValidPositiveInteger();
            
            if (numberOfTickets <= 0) {
                System.out.println("Invalid number of tickets. Please enter a positive number.");
                continue; // Ask again
            }
            
            if (numberOfTickets > availableSeats) {
                System.out.println("Sorry, only " + availableSeats + " seats available. Please enter a smaller number.");
                continue; // Ask again
            }
            
            break; // Valid number of tickets, exit the loop
        }
        
        // Calculate total price
        double totalPrice = numberOfTickets * ticketPrice;
        
        // Get customer details
        System.out.print("Enter your name: ");
        String customerName = scanner.nextLine();
        
        System.out.print("Enter your email: ");
        String customerEmail = scanner.nextLine();
        
        // Confirm booking
        System.out.println("\n=== BOOKING SUMMARY ===");
        System.out.println("Movie: " + movieName);
        System.out.println("Number of Tickets: " + numberOfTickets);
        System.out.println("Price per Ticket: $" + ticketPrice);
        System.out.println("Total Price: $" + totalPrice);
        System.out.println("Customer: " + customerName);
        System.out.println("Email: " + customerEmail);
        
        System.out.print("\nConfirm booking? (yes/no): ");
        String confirm = getValidYesNoInput();
        
        if (confirm.equalsIgnoreCase("yes")) {
            System.out.println("🎉 Booking confirmed! Tickets have been sent to " + customerEmail);
            System.out.println("Thank you for your purchase!");
        } else {
            System.out.println("Booking cancelled.");
        }
    }
    
    // Method to validate positive integer for ticket count
    private int getValidPositiveInteger() {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (input > 0) {
                    return input;
                } else {
                    System.out.print("Please enter a positive number: ");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    public static void main(String[] args) {
        BookingTickets bookingSystem = new BookingTickets();
        bookingSystem.browseAndBookTickets();
    }
}