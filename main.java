import java.util.Scanner;
import java.io.IOException; // Import IOException
public class main 
{ 
    public static void main(String[] args) 
    {
        Scanner user_input = new Scanner(System.in);
        MovieData movieData;
        try {
            movieData = new MovieData("genres.csv", "movies.csv"); // Instantiate MovieData
        } catch (IOException e) {
            System.err.println("Error loading movie data: " + e.getMessage());
            e.printStackTrace();
            return; // Exit if movie data cannot be loaded
        }
        
        BookingTickets booking = new BookingTickets(movieData, user_input); // Pass movieData and user_input
        My_Bookings myBookings = new My_Bookings(movieData, user_input); // Pass movieData and user_input
        Cancel_booking cancel = new Cancel_booking(movieData, user_input, myBookings); // Pass movieData, user_input, and myBookings
        Display_Menu menu = new Display_Menu(movieData, user_input, booking); // Pass movieData, user_input, and booking
        User_authen auth = new User_authen();
        auth.sign_up();
        try 
        {
            while (true)
            {
                int choice;
                    menu.showMainMenu(); // Renamed showMenu to showMainMenu
                    System.out.println("-------------------------");
                    System.out.print("Enter your choice: ");
                    choice = user_input.nextInt();
                    switch(choice)
                    {
                        case 1: 
                            menu.displayGenreAndMovieSelection(); // New method for genre/movie selection
                            break;
                        case 2: 
                            myBookings.viewMyBookings();
                            break;
                        case 3: 
                            cancel.cancelBooking();
                            break;
                        case 4: 
                            System.out.println("Exiting the application. Goodbye!");
                            System.exit(0);
                            user_input.close();
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");    
                }
                
                
            }
            
        }
        catch (Exception e)
        {
            System.out.println("An error occurred: " + e.getMessage());
        }
        finally {
            if (user_input != null) {
                user_input.close();
            }
        }
    }
}
