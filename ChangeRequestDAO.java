import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Data Access Object for ChangeRequest operations
 */
public class ChangeRequestDAO {

    /**
     * Insert a new change request
     */
    public static void insertChangeRequest(ChangeRequest cr) {
        String sql = "INSERT OR REPLACE INTO change_requests (id, asset_type, change_type, requested_by, " +
                     "status, assigned_agent, created_date, expiry_date, renewal_count) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cr.id);
            pstmt.setString(2, cr.assetType);
            pstmt.setString(3, cr.changeType);
            pstmt.setString(4, cr.requestedBy);
            pstmt.setString(5, cr.status);
            pstmt.setString(6, cr.assignedAgent);
            pstmt.setLong(7, cr.createdDate.getTime());
            pstmt.setLong(8, cr.expiryDate.getTime());
            pstmt.setInt(9, cr.renewalCount);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error inserting change request: " + e.getMessage());
        }
    }

    /**
     * Update an existing change request
     */
    public static void updateChangeRequest(ChangeRequest cr) {
        insertChangeRequest(cr); // Using INSERT OR REPLACE
    }

    /**
     * Get all change requests
     */
    public static ArrayList<ChangeRequest> getAllChangeRequests() {
        ArrayList<ChangeRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM change_requests";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ChangeRequest cr = createChangeRequestFromResultSet(rs);
                requests.add(cr);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving change requests: " + e.getMessage());
        }
        
        return requests;
    }

    /**
     * Get change request by ID
     */
    public static ChangeRequest getChangeRequestById(int id) {
        String sql = "SELECT * FROM change_requests WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createChangeRequestFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving change request: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get change requests by user
     */
    public static ArrayList<ChangeRequest> getChangeRequestsByUser(String userId) {
        ArrayList<ChangeRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM change_requests WHERE requested_by = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ChangeRequest cr = createChangeRequestFromResultSet(rs);
                requests.add(cr);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving user change requests: " + e.getMessage());
        }
        
        return requests;
    }

    /**
     * Helper method to create ChangeRequest object from ResultSet
     */
    private static ChangeRequest createChangeRequestFromResultSet(ResultSet rs) throws SQLException {
        ChangeRequest cr = new ChangeRequest(
            rs.getInt("id"),
            rs.getString("asset_type"),
            rs.getString("change_type"),
            rs.getString("requested_by")
        );
        
        cr.status = rs.getString("status");
        cr.assignedAgent = rs.getString("assigned_agent");
        cr.createdDate = new Date(rs.getLong("created_date"));
        cr.expiryDate = new Date(rs.getLong("expiry_date"));
        cr.renewalCount = rs.getInt("renewal_count");
        
        return cr;
    }
}
