import java.util.Scanner;
import java.util.ArrayList;

public class Cancel_booking {
    private Scanner scanner = new Scanner(System.in);

    // Method to cancel a booking
    public void cancelBooking() {
        System.out.println("\n=== CANCEL RESERVATION ===");

        // Load all bookings from CSV file
        ArrayList<My_Bookings.Booking> allBookings = My_Bookings.loadAllBookings();

        if (allBookings.isEmpty()) {
            System.out.println("No bookings available to cancel.");
            return;
        }

        // Ask for booking ID or email
        System.out.println("How would you like to search for your booking?");
        System.out.println("1. By Booking ID");
        System.out.println("2. By Email");
        System.out.print("Enter your choice: ");

        try {
            int searchChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            My_Bookings.Booking bookingToCancel = null;

            if (searchChoice == 1) {
                // Search by Booking ID
                System.out.print("Enter your Booking ID: ");
                int bookingId = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                bookingToCancel = My_Bookings.findBookingById(bookingId);

                if (bookingToCancel == null) {
                    System.out.println("Booking ID " + bookingId + " not found.");
                    return;
                }

            } else if (searchChoice == 2) {
                // Search by Email
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
                    System.out.println("Booking ID: " + booking.getBookingId());
                    System.out.println("Movie: " + booking.getMovieName());
                    System.out.println("Tickets: " + booking.getNumberOfTickets());
                    System.out.println("Total: $" + booking.getTotalPrice());
                    System.out.println("Date: " + booking.getBookingDate());
                }
                System.out.println("─────────────────────────────────────────────");

                // Ask which booking to cancel
                System.out.print("\nEnter the Booking ID you want to cancel: ");
                int bookingId = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                bookingToCancel = My_Bookings.findBookingById(bookingId);

                if (bookingToCancel == null) {
                    System.out.println("Booking ID " + bookingId + " not found.");
                    return;
                }

                // Verify booking belongs to this email
                if (!bookingToCancel.getCustomerEmail().equalsIgnoreCase(email)) {
                    System.out.println("This booking does not belong to the email you provided.");
                    return;
                }

            } else {
                System.out.println("Invalid choice.");
                return;
            }

            // Check if booking is already cancelled
            if (bookingToCancel.getStatus().equals("Cancelled")) {
                System.out.println("This booking has already been cancelled.");
                return;
            }

            // Display booking details
            System.out.println("\n=== BOOKING DETAILS ===");
            System.out.println("Booking ID      : " + bookingToCancel.getBookingId());
            System.out.println("Movie           : " + bookingToCancel.getMovieName());
            System.out.println("Tickets         : " + bookingToCancel.getNumberOfTickets());
            System.out.println("Total Price     : $" + bookingToCancel.getTotalPrice());
            System.out.println("Customer Name   : " + bookingToCancel.getCustomerName());
            System.out.println("Email           : " + bookingToCancel.getCustomerEmail());
            System.out.println("Status          : " + bookingToCancel.getStatus());

            // Confirm cancellation
            System.out.print("\nAre you sure you want to cancel this booking? (yes/no): ");
            String confirmation = getValidYesNoInput();

            if (confirmation.equalsIgnoreCase("yes")) {
                // Cancel the booking and update in CSV file
                bookingToCancel.setStatus("Cancelled");
                My_Bookings.updateBookingInFile(bookingToCancel);

                System.out.println("\n✓ Booking cancelled successfully!");
                System.out.println("Booking ID: " + bookingToCancel.getBookingId());
                System.out.println("Refund of $" + bookingToCancel.getTotalPrice() +
                        " will be processed to " + bookingToCancel.getCustomerEmail());
                System.out.println("The booking status has been updated in the system.");
            } else {
                System.out.println("Cancellation aborted. Your booking remains active.");
            }

        } catch (Exception e) {
            System.out.println("Invalid input! Please try again.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    // Method to update booking status (for admin or system use)
    public void updateBookingStatus() {
        System.out.println("\n=== UPDATE BOOKING STATUS ===");

        ArrayList<My_Bookings.Booking> allBookings = My_Bookings.loadAllBookings();

        if (allBookings.isEmpty()) {
            System.out.println("No bookings available.");
            return;
        }

        System.out.print("Enter Booking ID to update: ");

        try {
            int bookingId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            My_Bookings.Booking booking = My_Bookings.findBookingById(bookingId);

            if (booking == null) {
                System.out.println("Booking ID " + bookingId + " not found.");
                return;
            }

            // Display current booking details
            System.out.println("\n=== CURRENT BOOKING DETAILS ===");
            System.out.println("Booking ID: " + booking.getBookingId());
            System.out.println("Movie: " + booking.getMovieName());
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
                My_Bookings.updateBookingInFile(booking);
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
