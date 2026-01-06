# IT Ticket Management System - SQLite JDBC Integration

This document describes how to run the application with SQLite database backend.

## Prerequisites

- Java JDK 11 or higher
- SQLite JDBC driver (included in `lib/` directory)

## Setup

The required dependencies are already included in the `lib/` directory:
- `sqlite-jdbc-3.45.0.0.jar` - SQLite JDBC driver
- `slf4j-api-2.0.9.jar` - SLF4J API
- `slf4j-simple-2.0.9.jar` - SLF4J Simple implementation

## Compilation

To compile the application:

```bash
javac -cp ".:lib/*" *.java
```

On Windows:
```cmd
javac -cp ".;lib/*" *.java
```

## Running the Application

To run the application:

```bash
java -cp ".:lib/*" Main
```

On Windows:
```cmd
java -cp ".;lib/*" Main
```

## Database

The application uses SQLite and will automatically create a `ticket_system.db` file in the current directory on first run. The database includes the following tables:

- **users** - System users (users, agents, admins)
- **tickets** - Support tickets
- **change_requests** - Asset change requests
- **ticket_history** - Audit trail for tickets
- **ticket_notes** - Notes added to tickets

## Default Login Credentials

The system comes pre-loaded with demo users:

**Users:**
- ID: U1, Password: 123, Name: Manu
- ID: U2, Password: 123, Name: Karthi

**Agents:**
- ID: A1, Password: 123, Name: Vishnu
- ID: A2, Password: 123, Name: Priya

**Admin:**
- ID: AD1, Password: 123, Name: Rishi

## Features

The application now uses SQLite for persistent storage instead of in-memory ArrayList collections. All data is saved to the database and persists between application restarts.

### User Features
- Create tickets
- View my tickets
- Edit ticket descriptions
- View notes on tickets
- Escalate tickets
- Raise change requests
- Rate resolved tickets

### Agent Features
- View assigned tickets
- Add notes to tickets
- Update ticket status
- Reassign tickets to other agents
- Search tickets by status

### Admin Features
- View all tickets
- View escalated tickets
- Assign escalated tickets
- View change requests
- Approve/reject change requests
- Generate reports

## Data Persistence

All data is now stored in the SQLite database (`ticket_system.db`). This means:
- Data persists between application restarts
- Multiple application instances can access the same data
- Data can be queried directly using SQLite tools
- Easy to backup (just copy the .db file)

## Architecture Changes

### DAO Pattern
The application now uses the Data Access Object (DAO) pattern:
- `UserDAO` - Handles all user-related database operations
- `TicketDAO` - Handles all ticket-related database operations
- `ChangeRequestDAO` - Handles all change request operations

### Database Manager
`DatabaseManager` class handles:
- Database connection management
- Schema initialization
- Connection lifecycle

### Updated Classes
- `User` class - Removed `myTickets` ArrayList, now fetches from DB
- `Ticket` class - Removed `notes` and `history` ArrayLists, now fetches from DB
- `TicketSystem` class - Removed all ArrayList collections, uses DAOs instead
- Menu classes - Updated to use DAO methods for data access

## Troubleshooting

If you encounter issues:

1. **ClassNotFoundException for SQLite JDBC**: Ensure the `lib/` directory contains all JAR files and that you're using the correct classpath separator (`:` for Unix/Linux/Mac, `;` for Windows)

2. **Database locked**: Close any other applications that might be accessing the database file

3. **Permission errors**: Ensure the application has write permissions in the current directory to create the database file

## Development

To reset the database and start fresh:
```bash
rm ticket_system.db
```

The next time you run the application, it will recreate the database with demo data.
