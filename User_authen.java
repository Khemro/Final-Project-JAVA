import java.util.Scanner;
public class User_authen {
    private Scanner scanner = new Scanner(System.in);
    private String currentUser;

    public boolean login()
    {
        System.out.println("\n=== USER LOGIN ===");
        System.out.println("Enter your email: ");
        String email = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        
        if (authenticate(email, password)) {
            currentUser = email;
            System.out.println("Login successful! Welcome, " + currentUser);
            return true;
        } else {
            System.out.println("Login failed! Invalid email or password.");
            return false;
        }
    }

    // Simple authentication method (replace with real logic or external service)
    private boolean authenticate(String email, String password) {
        if (email == null || password == null) {
            return false;
        }
        // Example: accept a specific hardcoded user; modify as needed
        return email.equals("user@example.com") && password.equals("password123");
    }
}
