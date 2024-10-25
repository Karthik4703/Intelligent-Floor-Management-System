import java.sql.*;
import java.util.Scanner;

public class Floor {

    private Statement stmt;
    private Scanner sc;

    public Floor(Statement stmt, Scanner sc) {
        this.stmt = stmt;
        this.sc = sc;
    }

    public void printlines(){
        int numberOfLines = 4; // Number of blank lines
            String blankLines = System.lineSeparator().repeat(numberOfLines);
            System.out.print(blankLines);
    }

    public void createFloor() {
        try {
            System.out.println("Enter number of rooms: ");
            int rooms = Integer.parseInt(sc.nextLine());
            int[] roomsCap = new int[rooms];
            System.out.println("Enter each room capacity:");
            for (int i = 0; i < rooms; i++) {
                System.out.print("Capacity for room " + (i + 1) + ": ");
                roomsCap[i] = Integer.parseInt(sc.nextLine());
            }
            String floorTableName = getNewFloorTableName();
            String createTableSQL = "CREATE TABLE " + floorTableName + " (roomnumber INT AUTO_INCREMENT PRIMARY KEY, capacity INT,booked BOOLEAN NOT NULL,EAT TIMESTAMP)";
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table " + floorTableName + " created successfully.");
            for (int i = 0; i < rooms; i++) {
                String insertSQL = "INSERT INTO " + floorTableName + " (capacity, booked, EAT) VALUES (" + roomsCap[i] + ", FALSE, NOW())";
                stmt.executeUpdate(insertSQL); // Execute the SQL statement
            }
            System.out.println("All rooms have been added to the " + floorTableName + " table successfully.");

        } catch (SQLException e) {
            System.out.println("Error while inserting data: " + e.getMessage());
        }
    }

    private String getNewFloorTableName() throws SQLException {
        // Logic to determine the next available floor table name
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
        return "floor" + (floorCount + 1); // Return the next available floor name
    }

    public void modifyfloor(){
        while (true) {
            printlines();
            System.out.println("--------------------------------");
            System.out.println("Select one of the following options");
            System.out.println("1. List all floor details");
            System.out.println("2. Modify a floor");
            System.out.println("3. Back");
            int a = Integer.parseInt(sc.nextLine());
            switch (a) {
                case 0:
                    System.exit(0);
                case 1:
                    printFloorTables();
                    break;
                case 2:
                    System.out.println("Enter the floor number you want to modify");
                    int b = Integer.parseInt(sc.nextLine());
                    floormodify(b);
                    break;
                case 3: // Option to return to the main menu
                    return; // Exits adminHome method and goes back to menu in JdbcDemo
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    public void printFloorTables() {
        try {
            String query = "SELECT table_name FROM information_schema.tables " +
                           "WHERE table_schema = 'demodb' " +
                           "AND table_name LIKE 'floor%'";

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String tableName = rs.getString("table_name");
                System.out.println(tableName);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void floormodify(int floor){
        printlines();
        try {
            // Constructing the dynamic query for floor table
            String query = "SELECT * FROM floor" + floor;
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsMeta = rs.getMetaData();
            int columnCount = rsMeta.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rsMeta.getColumnName(i) + "\t");
            }
            System.out.println();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t\t");
                }
                System.out.println(); // Move to the next line after each row
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        while (true) {
            printlines();
            System.out.println("--------------------------------");
            System.out.println("Select one of the following options");
            System.out.println("1. If you want to delete a room");
            System.out.println("2. If you want to modify the room capacity");
            System.out.println("3. Back");
            int a = Integer.parseInt(sc.nextLine());
            switch (a) {
                case 0:
                    System.exit(0);
                case 1:
                    System.out.println("Enter the Room number you want to delete");
                    int b = Integer.parseInt(sc.nextLine());
                    deleteroom(floor,b);
                    break;
                case 2:
                    modifyroomcapacity(floor);
                    break;
                case 3: // Option to return to the main menu
                    return; // Exits adminHome method and goes back to menu in JdbcDemo
                default:
                    System.out.println("Invalid option. Please try again.");

                    
            }
        }
    }
    public void deleteroom(int floor,int room){
        try {
            // SQL query to delete the specific room
            String query = "DELETE FROM floor" + String.valueOf(floor) + " WHERE roomnumber = '" + String.valueOf(room) + "'";
            int result = stmt.executeUpdate(query);
            if (result > 0) {
                System.out.println("Room " + room + " deleted successfully from floor" + floor);
            } else {
                System.out.println("No matching room found to delete.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void modifyroomcapacity(int floor){
        try {
            System.out.println("Updating room capacity for floor" + floor);
            System.out.print("Enter the room number to update: ");
            String roomNumber = sc.nextLine();
            System.out.print("Enter the new capacity: ");
            int newCapacity = Integer.parseInt(sc.nextLine());
            String query = "UPDATE floor" + floor + " SET capacity = " + newCapacity + " WHERE roomnumber = '" + roomNumber + "'";
            int rowsAffected = stmt.executeUpdate(query);
            if (rowsAffected > 0) {
                System.out.println("Room " + roomNumber + " capacity updated successfully.");
            } else {
                System.out.println("Room " + roomNumber + " not found or capacity not updated.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for room capacity. Please enter a number.");
        }
    }
    
}