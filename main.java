import java.util.Scanner;
public class main 
{ 
    public static void main(String[] args) 
    {
        Scanner user_input = new Scanner(System.in);
        Display_Menu menu = new Display_Menu();
        BookingTickets booking = new BookingTickets();
        Cancel_booking cancel = new Cancel_booking();
        My_Bookings myBookings = new My_Bookings();
        User_authen auth = new User_authen();
        auth.sign_up();
        try 
        {
            while (true)
            {
                int choice;
                    menu.showMenu();
                    System.out.println("-------------------------");
                    System.out.print("Enter your choice: ");
                    choice = user_input.nextInt();
                    switch(choice)
                    {
                        case 1: 
                            booking.browseAndBookTickets();
                            
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
        
    }
}
