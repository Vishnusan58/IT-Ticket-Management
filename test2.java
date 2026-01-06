import java.util.ArrayList;

public class test2 {
    public static void main(String[] args) {
        try {
            DatabaseManager.initializeDatabase();
            
            // Insert a user
            User u1 = new User("U1", "123", "Manu", "user");
            UserDAO.insertUser(u1);
            
            // Get tickets for this user
            ArrayList<Ticket> tickets = u1.getMyTickets();
            System.out.println("Found " + tickets.size() + " tickets");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
