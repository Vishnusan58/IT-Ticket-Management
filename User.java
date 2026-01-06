import java.util.ArrayList;

// Represents a system user
public class User {
    public String id;
    public String password; // NEW
    public String name;
    public String role; // user / agent / admin

    public User(String id, String password, String name, String role) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    /**
     * Get user's tickets from database
     */
    public ArrayList<Ticket> getMyTickets() {
        return TicketDAO.getTicketsByUser(this.id);
    }
}
