import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Data Access Object for Ticket operations
 */
public class TicketDAO {

    /**
     * Insert a new ticket
     */
    public static void insertTicket(Ticket ticket) {
        String sql = "INSERT OR REPLACE INTO tickets (id, title, description, category, sub_category, " +
                     "status, created_by, assigned_to, escalated, rating, created_date, resolved_date, " +
                     "change_request, change_type, change_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ticket.id);
            pstmt.setString(2, ticket.title);
            pstmt.setString(3, ticket.description);
            pstmt.setString(4, ticket.category);
            pstmt.setString(5, ticket.subCategory);
            pstmt.setString(6, ticket.status);
            pstmt.setString(7, ticket.createdBy);
            pstmt.setString(8, ticket.assignedTo);
            pstmt.setInt(9, ticket.escalated ? 1 : 0);
            pstmt.setInt(10, ticket.rating);
            pstmt.setLong(11, ticket.createdDate.getTime());
            pstmt.setLong(12, ticket.resolvedDate != null ? ticket.resolvedDate.getTime() : 0);
            pstmt.setInt(13, ticket.changeRequest ? 1 : 0);
            pstmt.setString(14, ticket.changeType);
            pstmt.setString(15, ticket.changeStatus);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error inserting ticket: " + e.getMessage());
        }
    }

    /**
     * Update an existing ticket
     */
    public static void updateTicket(Ticket ticket) {
        insertTicket(ticket); // Using INSERT OR REPLACE
    }

    /**
     * Get all tickets
     */
    public static ArrayList<Ticket> getAllTickets() {
        ArrayList<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Ticket ticket = createTicketFromResultSet(rs);
                tickets.add(ticket);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving tickets: " + e.getMessage());
        }
        
        return tickets;
    }

    /**
     * Get ticket by ID
     */
    public static Ticket getTicketById(int id) {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createTicketFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving ticket: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get tickets by user ID (created by)
     */
    public static ArrayList<Ticket> getTicketsByUser(String userId) {
        ArrayList<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE created_by = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Ticket ticket = createTicketFromResultSet(rs);
                    tickets.add(ticket);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving user tickets: " + e.getMessage());
        }
        
        return tickets;
    }

    /**
     * Get tickets assigned to an agent
     */
    public static ArrayList<Ticket> getTicketsByAssignedAgent(String agentId) {
        ArrayList<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE assigned_to = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, agentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Ticket ticket = createTicketFromResultSet(rs);
                tickets.add(ticket);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving agent tickets: " + e.getMessage());
        }
        
        return tickets;
    }

    /**
     * Get tickets by status
     */
    public static ArrayList<Ticket> getTicketsByStatus(String status) {
        ArrayList<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE status = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Ticket ticket = createTicketFromResultSet(rs);
                tickets.add(ticket);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving tickets by status: " + e.getMessage());
        }
        
        return tickets;
    }

    /**
     * Get escalated tickets
     */
    public static ArrayList<Ticket> getEscalatedTickets() {
        ArrayList<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE escalated = 1";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Ticket ticket = createTicketFromResultSet(rs);
                tickets.add(ticket);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving escalated tickets: " + e.getMessage());
        }
        
        return tickets;
    }

    /**
     * Count active tickets for an agent
     */
    public static int countActiveTicketsForAgent(String agentId) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE assigned_to = ? AND status NOT IN ('resolved', 'closed')";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, agentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.out.println("Error counting active tickets: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Add a note to a ticket
     */
    public static void addNote(int ticketId, String note) {
        String sql = "INSERT INTO ticket_notes (ticket_id, note) VALUES (?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ticketId);
            pstmt.setString(2, note);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error adding note: " + e.getMessage());
        }
    }

    /**
     * Get all notes for a ticket
     */
    public static ArrayList<String> getNotes(int ticketId) {
        ArrayList<String> notes = new ArrayList<>();
        String sql = "SELECT note FROM ticket_notes WHERE ticket_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ticketId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                notes.add(rs.getString("note"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving notes: " + e.getMessage());
        }
        
        return notes;
    }

    /**
     * Add history entry for a ticket
     */
    public static void addHistory(int ticketId, TicketHistory history) {
        String sql = "INSERT INTO ticket_history (ticket_id, timestamp, actor, action, details) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ticketId);
            pstmt.setLong(2, history.timestamp.getTime());
            pstmt.setString(3, history.actor);
            pstmt.setString(4, history.action);
            pstmt.setString(5, history.details);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error adding history: " + e.getMessage());
        }
    }

    /**
     * Get all history for a ticket
     */
    public static ArrayList<TicketHistory> getHistory(int ticketId) {
        ArrayList<TicketHistory> history = new ArrayList<>();
        String sql = "SELECT timestamp, actor, action, details FROM ticket_history WHERE ticket_id = ? ORDER BY timestamp";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ticketId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TicketHistory th = new TicketHistory(
                    rs.getString("actor"),
                    rs.getString("action"),
                    rs.getString("details")
                );
                th.timestamp = new Date(rs.getLong("timestamp"));
                history.add(th);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving history: " + e.getMessage());
        }
        
        return history;
    }

    /**
     * Helper method to create Ticket object from ResultSet
     */
    private static Ticket createTicketFromResultSet(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket(
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("category"),
            rs.getString("sub_category"),
            rs.getString("created_by"),
            Ticket.SKIP_HISTORY_CREATION  // Skip history creation since we're loading from DB
        );
        
        ticket.id = rs.getInt("id");
        ticket.status = rs.getString("status");
        ticket.assignedTo = rs.getString("assigned_to");
        ticket.escalated = rs.getInt("escalated") == 1;
        ticket.rating = rs.getInt("rating");
        ticket.createdDate = new Date(rs.getLong("created_date"));
        
        long resolvedTime = rs.getLong("resolved_date");
        if (resolvedTime > 0) {
            ticket.resolvedDate = new Date(resolvedTime);
        }
        
        ticket.changeRequest = rs.getInt("change_request") == 1;
        ticket.changeType = rs.getString("change_type");
        ticket.changeStatus = rs.getString("change_status");
        
        return ticket;
    }
}
