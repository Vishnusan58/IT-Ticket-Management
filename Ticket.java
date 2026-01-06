import java.util.ArrayList;
import java.util.Date;

// Ticket class
public class Ticket {
    public int id;
    public String title;
    public String description;
    public String category;
    public String subCategory; // NEW
    public String status;
    public String createdBy;
    public String assignedTo;
    public boolean escalated;
    public int rating; // 1-5, 0 if not rated
    
    public Date createdDate;
    public Date resolvedDate;

    // CHANGE REQUEST FIELDS (NEW)
    public boolean changeRequest;          // raised or not
    public String changeType;              // raise / remove / renew
    public String changeStatus;             // pending / approved / rejected

    private boolean skipHistoryOnCreate = false;

    public Ticket(String title, String description, String category, String subCategory, String createdBy) {
        this(title, description, category, subCategory, createdBy, false);
    }

    public Ticket(String title, String description, String category, String subCategory, String createdBy, boolean skipHistory) {
        this.skipHistoryOnCreate = skipHistory;
        this.id = new java.util.Random().nextInt(9000) + 1000;
        this.title = title;
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
        this.createdBy = createdBy;
        this.status = "open";
        this.escalated = false;
        this.rating = 0;
        
        this.createdDate = new Date();
        
        this.changeRequest = false;
        this.changeStatus = "NA";
        
        // Log creation in database only if not loading from DB
        if (!skipHistory) {
            addHistory(createdBy, "CREATED", "Ticket created");
        }
    }

    public void addHistory(String actor, String action, String details) {
        TicketHistory history = new TicketHistory(actor, action, details);
        TicketDAO.addHistory(this.id, history);
    }

    /**
     * Get history from database
     */
    public ArrayList<TicketHistory> getHistory() {
        return TicketDAO.getHistory(this.id);
    }

    /**
     * Get notes from database
     */
    public ArrayList<String> getNotes() {
        return TicketDAO.getNotes(this.id);
    }

    /**
     * Add note to database
     */
    public void addNote(String note) {
        TicketDAO.addNote(this.id, note);
    }
}
