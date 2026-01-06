
import java.util.ArrayList;
import java.util.InputMismatchException;

// Handles ADMIN role actions
public class AdminMenu {
    TicketSystem system;

    public AdminMenu(TicketSystem system) {
        this.system = system;
    }

    public void menu() {
        while (true) {
            try {
                System.out.println("\n----- ADMIN MENU -----");
                System.out.println("1. View All Tickets");
                System.out.println("2. View Escalated Tickets");
                System.out.println("3. Assign Escalated Ticket");
                System.out.println("4. View Change Requests");
                System.out.println("5. Approve Change Request");
                System.out.println("6. Report");
                System.out.println("7. Logout");
                System.out.print("Choice: ");

                int ch = system.sc.nextInt();
                system.sc.nextLine();

                switch (ch) {
                    case 1: viewAll(); break;
                    case 2: viewEscalated(); break;
                    case 3: assignEscalatedTicket(); break;
                    case 4: viewChangeRequests(); break;
                    case 5: approveChange(); break;
                    case 6: report(); break;
                    case 7: return;
                    default:
                        System.out.println("Invalid choice! Try again.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                system.sc.nextLine();
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private void viewAll() {
        System.out.println("\n--- All Tickets ---");
        System.out.println("ID | Status | Agent | Title | Category");

        ArrayList<Ticket> tickets = TicketDAO.getAllTickets();
        for (Ticket t : tickets) {
            System.out.println(
                t.id + " | " + t.status + " | " + t.assignedTo + " | " + t.title + " | " + t.category
            );
        }
    }

    // ADMIN DASHBOARD: ESCALATED TICKETS
    private void viewEscalated() {
        System.out.println("\n--- Escalated Tickets ---");
        System.out.println("ID | Title | Category | Assigned To");

        boolean found = false;
        ArrayList<Ticket> tickets = TicketDAO.getEscalatedTickets();
        for (Ticket t : tickets) {
            System.out.println(
                t.id + " | " + t.title + " | " + t.category + " | " + t.assignedTo
            );
            found = true;
        }

        if (!found)
            System.out.println("No escalated tickets.");
    }

    // ADMIN ASSIGNS ESCALATED TICKET
    private void assignEscalatedTicket() {
        try {
            System.out.print("Enter Escalated Ticket ID: ");
            int id = system.sc.nextInt();
            system.sc.nextLine();

            Ticket targetTicket = TicketDAO.getTicketById(id);
            if (targetTicket == null || !targetTicket.escalated) {
                System.out.println("Ticket not found or not escalated");
                return;
            }

            System.out.println("\nAvailable Agents:");
            ArrayList<User> agents = UserDAO.getUsersByRole("agent");
            for (User u : agents) {
                System.out.println("- " + u.id + " (" + u.name + ")");
            }

            String agentId = "";
            boolean validAgent = false;

            while (!validAgent) {
                System.out.print("Enter Agent ID from above list: ");
                agentId = system.sc.nextLine();

                for (User u : agents) {
                    if (u.id.equals(agentId)) {
                        validAgent = true;
                        break;
                    }
                }

                if (!validAgent) {
                    System.out.println("Invalid Agent ID! Try again.");
                }
            }

            targetTicket.assignedTo = agentId;
            targetTicket.status = "in-progress";
            TicketDAO.updateTicket(targetTicket);
            System.out.println("Escalated ticket assigned to agent: " + agentId);

        } catch (InputMismatchException e) {
            System.out.println("Invalid Ticket ID!");
            system.sc.nextLine();
        }
    }

    private void report() {
        int resolved = 0;

        ArrayList<Ticket> tickets = TicketDAO.getAllTickets();
        for (Ticket t : tickets) {
            if (t.status != null &&
                (t.status.equalsIgnoreCase("resolved") || t.status.equalsIgnoreCase("closed"))) {
                resolved++;
            }
        }

        System.out.println("Total Resolved/Closed Tickets: " + resolved);

        int totalRating = 0;
        int count = 0;

        System.out.println("\n--- Agent Ratings ---");
        for (Ticket t : tickets) {
            if (t.rating > 0) {
                System.out.println(
                    "Ticket " + t.id + " (Agent " + t.assignedTo + "): " + t.rating + "/5"
                );
                totalRating += t.rating;
                count++;
            }
        }

        if (count > 0)
            System.out.println("Average Rating: " + (double) totalRating / count);
    }

    // ADMIN VIEWS CHANGE REQUESTS
    private void viewChangeRequests() {
        System.out.println("\n--- Change Requests ---");
        System.out.println("ID | Asset | Type | Status");

        ArrayList<ChangeRequest> changeRequests = ChangeRequestDAO.getAllChangeRequests();
        for (ChangeRequest cr : changeRequests) {
            System.out.println(
                cr.id + " | " + cr.assetType + " | " + cr.changeType + " | " + cr.status
            );
        }
    }

    // ADMIN APPROVES OR SENDS CHANGE REQUEST
    private void approveChange() {
        try {
            System.out.print("Enter Change Request ID: ");
            int id = system.sc.nextInt();
            system.sc.nextLine();

            ChangeRequest cr = ChangeRequestDAO.getChangeRequestById(id);

            if (cr != null && "pending".equals(cr.status)) {

                int choice = 0;
                boolean validChoice = false;

                while (!validChoice) {
                    try {
                        System.out.println("Select action:");
                        System.out.println("1. Approve Renewal");
                        System.out.println("2. Send to Agent");
                        System.out.print("Enter choice (1/2): ");

                        choice = system.sc.nextInt();
                        system.sc.nextLine();

                        if (choice == 1 || choice == 2) {
                            validChoice = true;
                        } else {
                            System.out.println("Invalid choice!");
                        }

                    } catch (InputMismatchException e) {
                        System.out.println("Enter number only!");
                        system.sc.nextLine();
                    }
                }

                if (choice == 1) {
                    cr.status = "approved";
                    ChangeRequestDAO.updateChangeRequest(cr);
                    System.out.println("Change request approved");
                } else {
                    System.out.println("\nAvailable Agents:");
                    ArrayList<User> agents = UserDAO.getUsersByRole("agent");
                    for (User u : agents) {
                        System.out.println("- " + u.id + " (" + u.name + ")");
                    }

                    String agentId = "";
                    boolean validAgent = false;

                    while (!validAgent) {
                        System.out.print("Enter Agent ID: ");
                        agentId = system.sc.nextLine();

                        for (User u : agents) {
                            if (u.id.equals(agentId)) {
                                validAgent = true;
                                break;
                            }
                        }

                        if (!validAgent) {
                            System.out.println("Invalid Agent ID!");
                        }
                    }

                    cr.assignedAgent = agentId;
                    cr.status = "sent-to-agent";
                    ChangeRequestDAO.updateChangeRequest(cr);
                    System.out.println("Sent to agent " + agentId + " for checking");
                }
                return;
            }

            System.out.println("Request not found or already handled");

        } catch (InputMismatchException e) {
            System.out.println("Invalid Change Request ID!");
            system.sc.nextLine();
        }
    }
}
