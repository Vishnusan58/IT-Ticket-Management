#!/bin/bash
{
  echo "1"     # Login
  echo "U1"    # User ID
  echo "123"   # Password
  sleep 0.5
  echo "2"     # My Tickets
  sleep 1
  echo "8"     # Logout
  sleep 0.5
  echo "2"     # Exit
} | timeout 10 java -cp ".:lib/*" Main 2>&1 | grep -v "NoSuchElementException\|at java.base\|at TicketSystem\|at UserMenu\|at AgentMenu\|at AdminMenu\|at Main" | head -40
