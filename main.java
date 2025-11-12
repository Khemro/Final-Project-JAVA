import java.util.Scanner;
public class main 
{ 
    public static void main(String[] args) 
    {
        try {
            Scanner user_input = new Scanner(System.in);
            DisplayMenu menu = new Display_Menu();
            
            
            int choice;
            while (true)
            {
                menu.showMenu();
                System.out.println("-------------------------");
                System.out.print("Enter your choice: ");
                choice = user_input.nextInt();
                switch(choice)
                {
                    

                }
            }
        
        }
        catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            }
    }
}