import java.sql.*;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {
    private Statement stmt;
    private Scanner sc;
    private String username;

    public  User(Statement stmt, Scanner sc) {
        this.stmt = stmt;
        this.sc = sc;
        this.username ="";
    }
    public void printlines(){
        int numberOfLines = 4; // Number of blank lines
            String blankLines = System.lineSeparator().repeat(numberOfLines);
            System.out.print(blankLines);
    }

    public void UserHome() {
        while (true) {
            int numberOfLines = 4; // Number of blank lines
            String blankLines = System.lineSeparator().repeat(numberOfLines);
            System.out.print(blankLines);
            System.out.println("--------------------------------");
            System.out.println("Welcome to FPMS User");
            System.out.println("1. Signup");
            System.out.println("2. Login");
            System.out.println("3. Back to Main Menu"); // Option to go back
            int a = Integer.parseInt(sc.nextLine());
            switch (a) {
                case 0:
                    System.exit(0);
                case 1:
                    UserSignup();
                    break;
                case 2:
                    UserLogin();
                    break;
                case 3: // Option to return to the main menu
                    return; // Exits    UserHome method and goes back to menu in JdbcDemo
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void UserSignup() {
        int numberOfLines = 4; // Number of blank lines
        String blankLines = System.lineSeparator().repeat(numberOfLines);
        System.out.print(blankLines);
        System.out.println("--------------------------------");
        System.out.println( "User Register");

        System.out.print("Enter New User name: ");
        String uname = sc.nextLine(); // Take input for username

        System.out.print("Enter New Password: ");
        String pwd = sc.nextLine(); // Take input for password

        // Now you can use uname and pwd to insert the new  User user into your database
        try {
            String query = "INSERT INTO user (uname, password) VALUES ('" + uname + "', '" + pwd + "')";
            String query2 = "CREATE TABLE "+uname+"_Bookings (floor INT,room INT,start_time DATETIME,end_time DATETIME);";
            int result = stmt.executeUpdate(query);
            stmt.executeUpdate(query2);
            if (result > 0) {
                System.out.println( "User registered successfully!");
            } else {
                System.out.println("Failed to register  User.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void  UserLogin() {
        int numberOfLines = 4; // Number of blank lines
        String blankLines = System.lineSeparator().repeat(numberOfLines);
        System.out.print(blankLines);
        System.out.println("--------------------------------");
        System.out.println( "User Login");

        System.out.print("Enter your username: ");
        String uname = sc.nextLine(); // Take input for username

        System.out.print("Enter your Password: ");
        String pwd = sc.nextLine(); // Take input for password

        // Check credentials in the User table
        try {
            String query = "SELECT * FROM   user WHERE uname = '" + uname + "' AND password = '" + pwd + "'";
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) { // If a record is found
                System.out.println("Login successful! Welcome, " + uname + "!");
                username = uname;
                menu();
            } else {
                System.out.println("Invalid username or password.");
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void menu() throws SQLException{
        while (true) {
            printlines();
            System.out.println("--------------------------------");
            System.out.println("Select one of the following options");
            System.out.println("1. Book a Room");
            System.out.println("2. List My Bookings");
            System.out.println("3. Logout");
            int a = Integer.parseInt(sc.nextLine());
            switch (a) {
                case 0:
                    System.exit(0);
                case 1:
                    bookroom();
                    break;
                case 2:
                    bookings();
                    break;
                case 3: // Option to return to the main menu
                    return; // Exits adminHome method and goes back to menu in JdbcDemo
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void bookroom() throws SQLException{
            printlines();
            int floorCount = 0;
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'floor%'"); // Check for existing floor tables
            while (rs.next()) {
                String tableName = rs.getString(1);
                // Extract the number from the table name (e.g., floor1 -> 1)
                if (tableName.startsWith("floor")) {
                    try {
                        floorCount = Math.max(floorCount, Integer.parseInt(tableName.substring(5))); // Extract the number
                    } catch (NumberFormatException e) {
                    }
                }
            }
            rs.close();
            System.out.println("--------------------------------");
            System.out.println("Enter the capacity of the room you want to book");
            int a = Integer.parseInt(sc.nextLine());
            StringBuilder query = new StringBuilder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            String currentTime = LocalDateTime.now().format(formatter); 
            for (int i = 1; i <= floorCount; i++) {
                if (i > 1) {
                    query.append(" UNION ALL "); // Add UNION ALL between the SELECT statements
                }
                
                query.append("SELECT ")
                .append(i) // This will be the floor number
                .append(" AS floor_number, roomnumber, capacity ")
                .append("FROM floor")
                .append(i)
                .append(" WHERE capacity >= ")
                .append(a)
                .append(" AND (booked = false OR (booked = true AND EAT <= '")
                .append(currentTime)
                .append("'))");
            }   

            ResultSet res = stmt.executeQuery(query.toString());

            // Loop through the ResultSet to print the data
            while (res.next()) {
                int floorNumber = res.getInt("floor_number");
                int roomNumber = res.getInt("roomnumber");
                int capacity = res.getInt("capacity");
                System.out.println("Floor: " + floorNumber + ", Room: " + roomNumber + ", Capacity: " + capacity);
            }

            System.out.println("Enter the floor number you want to book: ");
            int floornumber = Integer.parseInt(sc.nextLine());

            System.out.println("Enter the Room number you want to book: ");
            int roomnumber = Integer.parseInt(sc.nextLine());

            
            System.out.println("Enter the time (in format yyyy-MM-dd HH:mm:ss) upto when you want to book the room:");
            String input = sc.nextLine().trim();
            
            // Parse the input string to LocalDateTime
            LocalDateTime bookingTime = LocalDateTime.parse(input, formatter);
            String query2 = "UPDATE floor" + String.valueOf(floornumber) + 
                       " SET booked = " + true + 
                       ", EAT = '" + bookingTime + 
                       "' WHERE roomnumber = " + String.valueOf(roomnumber);
            // Execute the update query
            int result = stmt.executeUpdate(query2);

            if (result > 0) {
                System.out.println("Room booking updated successfully.");
            } else {
                    System.out.println("Failed to update room booking. No such room number found.");
            }
            String bookingTable = username + "_Bookings";

            // Insert the booking details into the user's booking table
            String insertBookingQuery = "INSERT INTO " + bookingTable + 
                                        " (floor, room, start_time, end_time) VALUES (" +
                                        floornumber + ", " +
                                        roomnumber + ", '" +
                                        currentTime + "', '" +
                                        bookingTime + "')";
            
            int bookingResult = stmt.executeUpdate(insertBookingQuery);

            if (bookingResult > 0) {
                System.out.println("Booking entry inserted successfully in the user bookings table.");
            } else {
                System.out.println("Failed to insert booking entry in the user bookings table.");
            }
            res.close(); 
    }
    private void bookings(){
        String query = "SELECT * FROM "+username+"_Bookings"; // Query to select all records from the table

        try {
            ResultSet rs = stmt.executeQuery(query); // Execute the query and get the result set

            // Check if the result set is empty
            if (!rs.isBeforeFirst()) {
                System.out.println("No bookings found.");
                return;
            }

            // Iterate through the result set and print each row
            while (rs.next()) {
                int floor = rs.getInt("floor");  // Get the floor number
                int room = rs.getInt("room");    // Get the room number
                Timestamp startTime = rs.getTimestamp("start_time");  // Get the start time
                Timestamp endTime = rs.getTimestamp("end_time");      // Get the end time
                System.out.println("Floor: " + floor + ", Room: " + room + 
                                   ", Start Time: " + startTime + ", End Time: " + endTime);
            }

            rs.close(); // Close the result set when done
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}