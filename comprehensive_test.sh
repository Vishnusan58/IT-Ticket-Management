#!/bin/bash

echo "===== COMPREHENSIVE TEST OF SQLITE INTEGRATION ====="
echo ""
echo "Test 1: Testing Agent Menu"
{
  echo "1"     # Login
  echo "A1"    # Agent ID
  echo "123"   # Password
  sleep 0.5
  echo "1"     # My Tickets
  sleep 1
  echo "5"     # Search Tickets
  sleep 0.5
  echo "1"     # open status
  sleep 1
  echo "6"     # Logout
  sleep 0.5
  echo "2"     # Exit
} | timeout 15 java -cp ".:lib/*" Main 2>&1 | grep -v "NoSuchElementException\|at java.base\|at TicketSystem\|at UserMenu\|at AgentMenu\|at AdminMenu\|at Main" | head -50

echo ""
echo "====="
echo "Test 2: Testing Admin Menu"
rm -f ticket_system.db
{
  echo "1"     # Login
  echo "AD1"   # Admin ID
  echo "123"   # Password
  sleep 0.5
  echo "1"     # View All Tickets
  sleep 1
  echo "4"     # View Change Requests
  sleep 1
  echo "6"     # Report
  sleep 1
  echo "7"     # Logout
  sleep 0.5
  echo "2"     # Exit
} | timeout 15 java -cp ".:lib/*" Main 2>&1 | grep -v "NoSuchElementException\|at java.base\|at TicketSystem\|at UserMenu\|at AgentMenu\|at AdminMenu\|at Main" | head -60

echo ""
echo "===== TESTS COMPLETE ====="
