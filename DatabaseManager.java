import java.sql.*;

/**
 * Manages database connection and initialization
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:ticket_system.db";
    private static Connection connection;

    static {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
    }

    /**
     * Get database connection (creates a new connection each time)
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Initialize database schema
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Users table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "role TEXT NOT NULL)"
            );

            // Tickets table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS tickets (" +
                "id INTEGER PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "description TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "sub_category TEXT, " +
                "status TEXT NOT NULL, " +
                "created_by TEXT NOT NULL, " +
                "assigned_to TEXT, " +
                "escalated INTEGER DEFAULT 0, " +
                "rating INTEGER DEFAULT 0, " +
                "created_date INTEGER NOT NULL, " +
                "resolved_date INTEGER, " +
                "change_request INTEGER DEFAULT 0, " +
                "change_type TEXT, " +
                "change_status TEXT)"
            );

            // Change Requests table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS change_requests (" +
                "id INTEGER PRIMARY KEY, " +
                "asset_type TEXT NOT NULL, " +
                "change_type TEXT NOT NULL, " +
                "requested_by TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "assigned_agent TEXT, " +
                "created_date INTEGER NOT NULL, " +
                "expiry_date INTEGER NOT NULL, " +
                "renewal_count INTEGER DEFAULT 0)"
            );

            // Ticket History table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS ticket_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ticket_id INTEGER NOT NULL, " +
                "timestamp INTEGER NOT NULL, " +
                "actor TEXT NOT NULL, " +
                "action TEXT NOT NULL, " +
                "details TEXT NOT NULL, " +
                "FOREIGN KEY (ticket_id) REFERENCES tickets(id))"
            );

            // Ticket Notes table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS ticket_notes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ticket_id INTEGER NOT NULL, " +
                "note TEXT NOT NULL, " +
                "FOREIGN KEY (ticket_id) REFERENCES tickets(id))"
            );

            System.out.println("Database initialized successfully");

        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing database: " + e.getMessage());
        }
    }
}
