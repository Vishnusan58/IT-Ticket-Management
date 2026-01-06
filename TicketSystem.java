import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

// Core system
public class TicketSystem {

    Scanner sc = new Scanner(System.in);

    public TicketSystem() {

        try {
            // Initialize database
            DatabaseManager.initializeDatabase();

            // Check if database is already populated
            if (UserDAO.getAllUsers().isEmpty()) {
                // USERS WITH PASSWORD
                User u1 = new User("U1", "123", "Manu", "user");
                User u2 = new User("U2", "123", "Karthi", "user");
                User a1 = new User("A1", "123", "Vishnu", "agent");
                User a2 = new User("A2", "123", "Priya", "agent");
                User ad1 = new User("AD1", "123", "Rishi", "admin");

                UserDAO.insertUser(u1);
                UserDAO.insertUser(u2);
                UserDAO.insertUser(a1);
                UserDAO.insertUser(a2);
                UserDAO.insertUser(ad1);

                // EXISTING TICKETS
                Ticket t1 = new Ticket("Email Issue", "Email not working", "IT", "Software", "U1");
                t1.assignedTo = "A1";
                t1.status = "in-progress";
                TicketDAO.insertTicket(t1);
                TicketDAO.addNote(t1.id, "Checking email server configuration - Vishnu");

                Ticket t2 = new Ticket("Network Slow", "Internet is very slow", "IT", "Network", "U2");
                t2.assignedTo = "A2";
                t2.status = "open";
                TicketDAO.insertTicket(t2);
                TicketDAO.addNote(t2.id, "Will check network bandwidth - Priya");

                Ticket t3 = new Ticket("Mouse Broken", "Left click not working", "Hardware", "Peripherals", "U1");
                t3.assignedTo = "A1";
                t3.status = "resolved";
                t3.resolvedDate = new Date();
                t3.rating = 4;
                TicketDAO.insertTicket(t3);
                TicketDAO.addNote(t3.id, "Replaced with new mouse - Vishnu");

                Ticket t4 = new Ticket("Payroll Error", "Salary mismatch", "HR", "Payroll", "U2");
                t4.assignedTo = "A2";
                t4.status = "open";
                TicketDAO.insertTicket(t4);
                TicketDAO.addNote(t4.id, "Forwarded to HR team for verification - Priya");

                Ticket t5 = new Ticket("VPN Access", "Need VPN access", "IT", "Network", "U1");
                t5.assignedTo = "A1";
                t5.status = "waiting";
                TicketDAO.insertTicket(t5);
                TicketDAO.addNote(t5.id, "Waiting for manager approval document - Vishnu");

                ChangeRequest cr1 = new ChangeRequest(101, "laptop", "renew", "U1");
                ChangeRequestDAO.insertChangeRequest(cr1);
            }

        } catch (Exception e) {
            System.out.println("Error while initializing system data: " + e.getMessage());
        }
    }

    // Auto-assign ticket to agent with LEAST number of active tickets
    public void assignTicket(Ticket t) {
        try {
            String bestAgent = null;
            int minTickets = Integer.MAX_VALUE;

            ArrayList<User> agents = UserDAO.getUsersByRole("agent");
            for (User u : agents) {
                int count = TicketDAO.countActiveTicketsForAgent(u.id);

                if (count < minTickets) {
                    minTickets = count;
                    bestAgent = u.id;
                }
            }

            if (bestAgent != null) {
                t.assignedTo = bestAgent;
                System.out.println("Auto-assigned to agent: " + bestAgent);
                t.addHistory("System", "ASSIGNED", "Auto-assigned to " + bestAgent);
            } else {
                System.out.println("No agents available.");
            }

        } catch (Exception e) {
            System.out.println("Error while assigning ticket: " + e.getMessage());
        }
    }

    public void start() {
        while (true) {
            try {
                System.out.println("\n===== IT TICKET SYSTEM =====");
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.print("Choice: ");

                int ch = sc.nextInt();
                sc.nextLine(); // clear buffer

                if (ch == 1)
                    login();
                else if (ch == 2) {
                    DatabaseManager.closeConnection();
                    break;
                }
                else
                    System.out.println("Invalid choice!");

            } catch (InputMismatchException e) {
                System.out.println("Please enter a number only!");
                sc.nextLine(); // clear wrong input
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    // LOGIN WITH PASSWORD
    private void login() {
        try {
            System.out.print("User ID: ");
            String id = sc.nextLine();

            System.out.print("Password: ");
            String pass = sc.nextLine();

            User u = UserDAO.validateCredentials(id, pass);
            if (u != null) {
                System.out.println("Welcome " + u.name);

                if (u.role.equals("user"))
                    new UserMenu(this, u).menu();
                else if (u.role.equals("agent"))
                    new AgentMenu(this, u).menu();
                else
                    new AdminMenu(this).menu();
                return;
            }

            System.out.println("Invalid credentials!");

        } catch (Exception e) {
            System.out.println("Login failed due to error: " + e.getMessage());
        }
    }
}

