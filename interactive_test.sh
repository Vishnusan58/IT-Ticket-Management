#!/bin/bash
{
  echo "1"     # Login
  echo "U1"    # User ID
  echo "123"   # Password
  sleep 0.5
  echo "2"     # My Tickets
  sleep 1.5
  echo "1"     # Create Ticket
  sleep 0.5
  echo "Test Ticket"  # Title
  echo "This is a test"  # Description
  echo "1"     # Category IT
  echo "Software"  # Sub-category
  sleep 1
  echo "8"     # Logout
  sleep 0.5
  echo "2"     # Exit
} | java -cp ".:lib/*" Main 2>&1 | head -80
