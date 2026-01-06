import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object for User operations
 */
public class UserDAO {

    /**
     * Insert a new user
     */
    public static void insertUser(User user) {
        String sql = "INSERT OR REPLACE INTO users (id, password, name, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.id);
            pstmt.setString(2, user.password);
            pstmt.setString(3, user.name);
            pstmt.setString(4, user.role);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error inserting user: " + e.getMessage());
        }
    }

    /**
     * Get all users
     */
    public static ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT id, password, name, role FROM users";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User(
                    rs.getString("id"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("role")
                );
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }
        
        return users;
    }

    /**
     * Get user by ID
     */
    public static User getUserById(String id) {
        String sql = "SELECT id, password, name, role FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getString("id"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("role")
                );
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving user: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Validate user credentials
     */
    public static User validateCredentials(String id, String password) {
        String sql = "SELECT id, password, name, role FROM users WHERE id = ? AND password = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getString("id"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("role")
                );
            }
            
        } catch (SQLException e) {
            System.out.println("Error validating credentials: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get all users by role
     */
    public static ArrayList<User> getUsersByRole(String role) {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT id, password, name, role FROM users WHERE role = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User user = new User(
                    rs.getString("id"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("role")
                );
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving users by role: " + e.getMessage());
        }
        
        return users;
    }
}
