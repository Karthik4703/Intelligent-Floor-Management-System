import java.sql.*;
import java.util.Scanner;

public class JdbcDemo {

    // Set JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost:3306/demodb?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true";
    static final String USER = "karthik"; // Add your user
    static final String PASS = "Dck1503!";// Add password

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;

        // STEP 2. Connecting to the Database
        try {
            // STEP 2a: Register JDBC driver
            Class.forName(JDBC_DRIVER);
            // STEP 2b: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            System.out.println("Welcome to Floor Plan Management System\n\n");
            Scanner sc = new Scanner(System.in);
            menu(stmt, sc);

            // STEP 3: Query to database
            String sql = "SELECT fname, lname from employee";
            ResultSet rs = stmt.executeQuery(sql);

            // STEP 4: Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                String fname = rs.getString("fname");
                String lname = rs.getString("lname");

                // Display values
                System.out.print("fname: " + fname);
                System.out.println(", lname: " + lname);
            }

            // STEP 5: Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) { // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) { // Handle errors for Class.forName
            e.printStackTrace();
        } finally { // finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
        System.out.println("End of Code");
    } // end main

    static void menu(Statement stmt, Scanner sc) {
         while (true) {
               int numberOfLines = 4; // Number of blank lines
               String blankLines = System.lineSeparator().repeat(numberOfLines);
               System.out.print(blankLines);
               System.out.println("--------------------------------");
               System.out.println("Select Role");
               System.out.println("0. Exit");
               System.out.println("1. Admin");
               System.out.println("2. User");
               int a = Integer.parseInt(sc.nextLine());
               switch (a) {
                  case 0:
                     System.exit(0);
                  case 1:
                     Admin admin = new Admin(stmt, sc);
                     admin.adminHome();
                     break;
                  case 2:
                    User user = new User(stmt, sc);
                    user.UserHome();
                    break;
                  default:
                     System.out.println("Invalid option. Please try again.");
               }      
         }
      }
}