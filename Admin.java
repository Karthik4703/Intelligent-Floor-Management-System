import java.sql.*;
import java.util.Scanner;

public class Admin {
    private Statement stmt;
    private Scanner sc;

    public Admin(Statement stmt, Scanner sc) {
        this.stmt = stmt;
        this.sc = sc;
    }

    public void adminHome() {
        
        while (true) {
            int numberOfLines = 4; 
            String blankLines = System.lineSeparator().repeat(numberOfLines);
            System.out.print(blankLines);
            System.out.println("--------------------------------");
            System.out.println("Welcome to FPMS Admin");
            System.out.println("1. Signup");
            System.out.println("2. Login");
            System.out.println("3. Back to Main Menu"); 
            int a = Integer.parseInt(sc.nextLine());
            switch (a) {
                case 0:
                    System.exit(0);
                case 1:
                    adminSignup();
                    break;
                case 2:
                    adminLogin();
                    break;
                case 3: 
                    return; 
                default:
                    System.out.println("Invalid option. Please try again.");
            }
           
        }
    }

    private void adminSignup() {
        int numberOfLines = 4; 
        String blankLines = System.lineSeparator().repeat(numberOfLines);
        System.out.print(blankLines);
        System.out.println("--------------------------------");
        System.out.println("Admin Register");

        System.out.print("Enter New User name: ");
        String uname = sc.nextLine();

        System.out.print("Enter New Password: ");
        String pwd = sc.nextLine(); 
        try {
            String query = "INSERT INTO Admin (uname, password) VALUES ('" + uname + "', '" + pwd + "')";
            int result = stmt.executeUpdate(query);
            if (result > 0) {
                System.out.println("Admin registered successfully!");

            } else {
                System.out.println("Failed to register admin.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void adminLogin() {
        int numberOfLines = 4;
        String blankLines = System.lineSeparator().repeat(numberOfLines);
        System.out.print(blankLines);
        System.out.println("--------------------------------");
        System.out.println("Admin Login");

        System.out.print("Enter your username: ");
        String uname = sc.nextLine(); 

        System.out.print("Enter your Password: ");
        String pwd = sc.nextLine(); // Take input for password
        try {
            String query = "SELECT * FROM Admin WHERE uname = '" + uname + "' AND password = '" + pwd + "'";
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) { // If a record is found
                System.out.println("Login successful! Welcome, " + uname + "!");
                floorplan();

            } else {
                System.out.println("Invalid username or password.");
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
    private void floorplan() {
        while (true) {
            int numberOfLines = 4; // Number of blank lines
            String blankLines = System.lineSeparator().repeat(numberOfLines);
            System.out.print(blankLines);
            System.out.println("--------------------------------");
            System.out.println("Select one of the following options");
            System.out.println("1. Add a floor map");
            System.out.println("2. Modify a floor map");
            System.out.println("3.logout");
            int a = Integer.parseInt(sc.nextLine());
            Floor floor= new Floor(stmt, sc);
            switch (a) {
               case 0:
                  System.exit(0);
                  break;
               case 1:
                  floor.createFloor();
                  break;
               case 2:
                  floor.modifyfloor();
                  break;
                case 3:
                  return;
               default:
                  System.out.println("Invalid option. Please try again.");
            }      
      }
        
    }
}




