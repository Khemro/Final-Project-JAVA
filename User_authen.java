import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.time.LocalDateTime;


public class User_authen 
{
    private Scanner scanner = new Scanner(System.in);
    private String usersFile = "users.csv";
    
    public User_authen()
    {
        initializeUserFiles();
    }

    private void initializeUserFiles()
    {
        try 
        {
            FileWriter write = new FileWriter(usersFile, true);
            write.close();
            System.out.println("✓ User system ready - CSV file initialized");
        } 
        catch (IOException e)
        {
            System.out.println("❌ An error occurred while initializing the users file: " + e.getMessage());
        }
    }

    private boolean isEmailExists(String email) 
    {
        try 
        {
            BufferedReader reader = new BufferedReader(new FileReader(usersFile));
            String line;
            
            while ((line = reader.readLine()) != null) 
            {
                // Split CSV line by commas
                String[] parts = line.split(",");
                
                // Check if this line has at least an email field
                if (parts.length >= 1) 
                {
                    String storedEmail = parts[0].trim().toLowerCase();
                    String inputEmail = email.trim().toLowerCase();
                    
                    if (storedEmail.equals(inputEmail)) 
                    {
                        reader.close();
                        return true; // ✅ Email exists
                    }
                }
            }
            reader.close();
        } 

        catch (IOException e) 
        {
            // File might not exist yet (first user), which is fine
            System.out.println("⚠️  Note: No existing users found (first registration)");
        }
        return false; // ❌ Email doesn't exist
    }
        
    
    
    

    public void sign_up()
    {   
        System.out.println("=========================================");
        System.out.println("Sign Up for a new account to get started!");
        System.out.println("=========================================");
        System.out.println("\n📋 ACCOUNT REQUIREMENTS:");
        System.out.println("   👤 Name: At least 2 characters");
        System.out.println("   📧 Email: Valid email (gmail.com, yahoo.com, etc.)");
        System.out.println("   🔒 Password: At least 6 characters");
        System.out.println("=".repeat(50));
        
        while (true) {
            String email = getValidEmail();
            String name = getValidName();
            String password = getValidPassword();
            String confirmPassword = getConfirmedPassword(password);
            
            // Final confirmation
            System.out.println("\n" + "=".repeat(40));
            System.out.println("   📋 ACCOUNT SUMMARY");
            System.out.println("=".repeat(40));
            System.out.println("📧 Email: " + email);
            System.out.println("👤 Name: " + name);
            System.out.println("🔒 Password: " + "*".repeat(password.length()));
            System.out.println("=".repeat(40));
            
            System.out.print("✅ Confirm and create account? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (confirm.equals("yes"))
            {
                if (saveUserAccount_toCSV(email, password, name)) 
                {
                    System.out.println("\n🎉 ACCOUNT CREATED SUCCESSFULLY!");
                    System.out.println("📧 You can now login with your email and password.");
                    return;
                } 
                else 
                {
                    System.out.println("❌ Failed to save account. Please try again.");
                    // Loop continues for new attempt
                }
            } 

            else 
            {
                System.out.println("🚫 Account creation cancelled.");
                System.out.print("🔄 Start over? (yes/no): ");
                String restart = scanner.nextLine().trim().toLowerCase();
                if (!restart.equals("yes")) {
                    return;
                }
                // Loop continues for new attempt
            }
            
        }
    }
    
    private String getValidEmail() 
    {
        while (true) {
            System.out.print("\n📧 Enter your email: ");
            String email = scanner.nextLine().trim();
            
            if (email.isEmpty()) 
            {
                System.out.println("❌ Email cannot be empty.");
                continue;
            }
            
            if (email.contains(",")) 
            {
                System.out.println("❌ Email cannot contain commas.");
                continue;
            }
            
            if (!email.contains("@") || !email.contains(".")) 
            {
                System.out.println("❌ Invalid email format. Must contain '@' and '.'");
                continue;
            }
            
            String[] validDomains = 
            {
                "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", 
                "icloud.com", "protonmail.com", "aol.com", "mail.com",
                "edu", "ac.uk", "school.edu"  // Educational domains
            };
            
            String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
            boolean domainIsValid = false;
            for (String Domains : validDomains) 
            {
                if (domain.endsWith(Domains)) 
                {
                    domainIsValid = true;
                    break; 
                }    
            }
            
            if (!domainIsValid) 
            {
                System.out.println("❌ Email domain not recognized. Use: gmail.com, yahoo.com, etc.");
                continue;
            }
            
            // Check if email already exists (you can add this later)
            // if (isEmailExists(email)) {
            //     System.out.println("❌ Email already registered. Use a different email.");
            //     continue;
            // }
            if (isEmailExists(email)) 
            {
                System.out.println("❌ This email is already registered. Please use a different email.");
                continue;
            }
                return email; // ✅ Valid email
        }
    }
    
    private String getValidName() 
    {
        while (true) {
            System.out.print("👤 Enter your name: ");
            String name = scanner.nextLine().trim();
            
            if (name.isEmpty()) 
            {
                System.out.println("❌ Name cannot be empty.");
                continue;
            }
            
            if (name.length() < 2) 
            {
                System.out.println("❌ Name must be at least 2 characters long.");
                continue;
            }
            
            if (name.contains(",")) 
            {
                System.out.println("❌ Name cannot contain commas.");
                continue;
            }
            
            return name; // ✅ Valid name
        }
    }
    
    private String getValidPassword() 
    {
        while (true) 
        {
            System.out.print("🔒 Enter your password (min 6 characters): ");
            String password = scanner.nextLine();
            
            if (password.isEmpty()) 
            {
                System.out.println("❌ Password cannot be empty.");
                continue;
            }
            
            if (password.length() < 6) 
            {
                System.out.println("❌ Password must be at least 6 characters long.");
                continue;
            }
            
            if (password.contains(",")) 
            {
                System.out.println("❌ Password cannot contain commas.");
                continue;
            }
            
            return password; // ✅ Valid password
        }
    }
    
    private String getConfirmedPassword(String originalPassword) 
    {
        while (true) {
            System.out.print("🔒 Confirm your password: ");
            String confirmPassword = scanner.nextLine();
            
            if (!originalPassword.equals(confirmPassword)) 
            {
                System.out.println("❌ Passwords do not match. Please try again.");
                continue;
            }
            
            return confirmPassword; // ✅ Passwords match
        }
    }
    
    private boolean saveUserAccount_toCSV(String email, String password, String name) 
    {
        try 
        {
            FileWriter write = new FileWriter(usersFile, true);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter signUp_Date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter signUp_Time = DateTimeFormatter.ofPattern("HH:mm:ss");
            String date = now.format(signUp_Date);
            String time = now.format(signUp_Time);

            String Data_Line = String.format("%s,%s,%s,%s,%s\n", email, password, name, date, time);

            write.write(Data_Line);
            write.close();
            return true; // ✅ Success
        } 
        
        catch (IOException e) 
        {
            System.out.println("❌ An error occurred while saving user account: " + e.getMessage());
            return false; // ❌ Failed
        }
    }  
}