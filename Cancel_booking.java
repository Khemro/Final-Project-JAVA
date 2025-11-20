import java.util.Scanner;
import java.util.ArrayList;

public class Cancel_booking {
    private Scanner scanner;
    private MovieData movieData; // New field for MovieData
    private My_Bookings myBookings; // New field for My_Bookings instance

    // Constructor
    public Cancel_booking(MovieData movieData, Scanner scanner, My_Bookings myBookings) {
        this.movieData = movieData;
        this.scanner = scanner;
        this.myBookings = myBookings;
    }

    // Method to cancel a booking
    public void cancelBooking() {
        System.out.println("\n=== CANCEL RESERVATION ===");

        // Load all bookings from CSV file using the My_Bookings instance
        ArrayList<My_Bookings.Booking> allBookings = myBookings.loadAllBookings();

        if (allBookings.isEmpty()) {
            System.out.println("No bookings available to cancel.");
            return;
        }

        My_Bookings.Booking bookingToCancel = null;
        int identificationChoice;

        while (true) { // Loop until valid input or exit
            System.out.println("\nHow do you want to identify the booking to cancel?");
            System.out.println("1. By Email (and then choose from your confirmed bookings)");
            System.out.println("2. Directly by Booking ID");
            System.out.println("3. Back to Main Menu"); // New option
            System.out.print("Enter your choice (1, 2 or 3): "); // Adjusted prompt

            try {
                identificationChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (identificationChoice == 3) { // New option to go back
                    System.out.println("Returning to main menu.");
                    return; // Exit this method
                } else if (identificationChoice == 1 || identificationChoice == 2) {
                    break; // Valid choice, exit loop
                } else {
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number (1, 2, or 3).");
                scanner.nextLine(); // Clear invalid input
            }
        }

        try {
            if (identificationChoice == 1) {
                System.out.print("Enter your email: ");
                String email = scanner.nextLine().trim();

                ArrayList<My_Bookings.Booking> userBookings = new ArrayList<>();
                for (My_Bookings.Booking booking : allBookings) {
                    if (booking.getCustomerEmail().equalsIgnoreCase(email) &&
                            booking.getStatus().equals("Confirmed")) {
                        userBookings.add(booking);
                    }
                }

                if (userBookings.isEmpty()) {
                    System.out.println("No confirmed bookings found for email: " + email);
                    return;
                }

                // Display user's bookings
                System.out.println("\n=== YOUR CONFIRMED BOOKINGS ===");
                for (My_Bookings.Booking booking : userBookings) {
                    System.out.println("─────────────────────────────────────────────");
                    System.out.println("Booking ID: " + booking.getAlphanumericBookingId());
                    System.out.println("Movie: " + booking.getMovieTitle() + " (" + booking.getMovieId() + ") - " + booking.getMovieGenre());
                    System.out.println("Tickets: " + booking.getNumberOfTickets());
                    System.out.println("Total: $" + booking.getTotalPrice());
                    System.out.println("Date: " + booking.getBookingDate());
                }
                System.out.println("─────────────────────────────────────────────");

                System.out.print("\nEnter the Booking ID you want to cancel: ");
                String bookingId = scanner.nextLine().trim();
                bookingToCancel = myBookings.findBookingById(bookingId); // Use findBookingById (alphanumeric)

                // Verify booking belongs to this email
                if (bookingToCancel == null || !bookingToCancel.getCustomerEmail().equalsIgnoreCase(email)) {
                    System.out.println("Booking ID " + bookingId + " not found or does not belong to email " + email);
                    return;
                }

            } else { // identificationChoice == 2
                System.out.print("\nEnter the Booking ID you want to cancel: ");
                String bookingId = scanner.nextLine().trim();
                bookingToCancel = myBookings.findBookingById(bookingId); // Use findBookingById (alphanumeric)

                if (bookingToCancel == null) {
                    System.out.println("Booking ID " + bookingId + " not found.");
                    return;
                }
            }

            // At this point, bookingToCancel should be identified
            if (bookingToCancel == null) { // Should ideally not happen with previous checks, but for safety
                System.out.println("Booking could not be identified.");
                return;
            }

            // Check if booking is already cancelled
            if (bookingToCancel.getStatus().equals("Cancelled")) {
                System.out.println("This booking has already been cancelled.");
                return;
            }

            // Display booking summary
            System.out.println("\n=== BOOKING TO CANCEL ===");
            System.out.println("Booking ID      : " + bookingToCancel.getAlphanumericBookingId());
            System.out.println("Movie           : " + bookingToCancel.getMovieTitle() + " (" + bookingToCancel.getMovieId() + ") - " + bookingToCancel.getMovieGenre());
            System.out.println("Tickets         : " + bookingToCancel.getNumberOfTickets());
            System.out.println("Total Price     : $" + bookingToCancel.getTotalPrice());
            System.out.println("=".repeat(40));

            // Confirm cancellation
            System.out.print("\nAre you sure you want to cancel this booking? (yes/no): ");
            String confirmation = getValidYesNoInput();
            
            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Cancellation aborted. Your booking remains active.");
                return;
            }

            // Cancel the booking and update in CSV file using the My_Bookings instance
            bookingToCancel.setStatus("Cancelled");
            myBookings.updateBookingInFile(bookingToCancel);

            System.out.println("\n✓ Booking cancelled successfully!");
            System.out.println("Booking ID: " + bookingToCancel.getAlphanumericBookingId());
            System.out.println("Refund of $" + bookingToCancel.getTotalPrice() +
                    " will be processed to " + bookingToCancel.getCustomerEmail());
            System.out.println("The booking status has been updated in the system.");

        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage()); // More specific error message
            scanner.nextLine(); // Clear invalid input
        }
    }

    // Method to update booking status (for admin or system use)
    public void updateBookingStatus() {
        System.out.println("\n=== UPDATE BOOKING STATUS ===");

        // Load all bookings from CSV file using the My_Bookings instance
        ArrayList<My_Bookings.Booking> allBookings = myBookings.loadAllBookings();

        if (allBookings.isEmpty()) {
            System.out.println("No bookings available.");
            return;
        }

        My_Bookings.Booking booking = null;

        System.out.print("Enter your Booking ID to update: ");
        String bookingId = scanner.nextLine().trim(); // Prompt directly for alphanumeric ID

        try {
            booking = myBookings.findBookingById(bookingId); // Use findBookingById (alphanumeric)

            if (booking == null) {
                System.out.println("Booking ID " + bookingId + " not found.");
                return;
            }

            // Display current booking details
            System.out.println("\n=== CURRENT BOOKING DETAILS ===");
            System.out.println("Booking ID: " + booking.getAlphanumericBookingId()); // Display only alphanumeric
            System.out.println("Movie: " + booking.getMovieTitle() + " (" + booking.getMovieId() + ") - " + booking.getMovieGenre());
            System.out.println("Customer: " + booking.getCustomerName());
            System.out.println("Current Status: " + booking.getStatus());
            System.out.println("Current Tickets: " + booking.getNumberOfTickets());

            // Update options
            System.out.println("\n=== UPDATE OPTIONS ===");
            System.out.println("1. Change Status");
            System.out.println("2. Modify Number of Tickets");
            System.out.println("3. Cancel and Return");
            System.out.print("Enter your choice: ");

            int updateChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            boolean updated = false;

            switch (updateChoice) {
                case 1:
                    // Change status
                    System.out.println("\nSelect new status:");
                    System.out.println("1. Confirmed");
                    System.out.println("2. Cancelled");
                    System.out.print("Enter choice: ");
                    int statusChoice = scanner.nextInt();
                    scanner.nextLine();

                    if (statusChoice == 1) {
                        booking.setStatus("Confirmed");
                        System.out.println("✓ Status updated to: Confirmed");
                        updated = true;
                    } else if (statusChoice == 2) {
                        booking.setStatus("Cancelled");
                        System.out.println("✓ Status updated to: Cancelled");
                        updated = true;
                    } else {
                        System.out.println("Invalid choice.");
                    }
                    break;

                case 2:
                    // Modify tickets
                    System.out.print("Enter new number of tickets: ");
                    int newTickets = scanner.nextInt();
                    scanner.nextLine();

                    if (newTickets <= 0) {
                        System.out.println("Invalid number of tickets.");
                    } else {
                        int oldTickets = booking.getNumberOfTickets();
                        double oldTotal = booking.getTotalPrice();
                        booking.setNumberOfTickets(newTickets);
                        System.out.println("✓ Tickets updated from " + oldTickets + " to " + newTickets);
                        System.out.println("New Total: $" + booking.getTotalPrice() +
                                " (Previous: $" + oldTotal + ")");
                        updated = true;
                    }
                    break;

                case 3:
                    System.out.println("Update cancelled.");
                    return;

                default:
                    System.out.println("Invalid choice.");
            }

            // Save changes to CSV file if any updates were made
            if (updated) {
                myBookings.updateBookingInFile(booking);
                System.out.println("Changes saved to file successfully.");
            }

        } catch (Exception e) {
            System.out.println("Invalid input! Please try again.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    // Helper method to validate yes/no input
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
}
