# Weekly Shift Scheduler

## Overview
The **Weekly Shift Scheduler** is a Java-based application designed to automate the assignment of employees to shifts across a weekly schedule. It ensures fair distribution of shifts based on employee preferences and constraints.

## Features
- Assigns employees to shifts (Morning, Afternoon, Evening) based on their preferences.
- Ensures minimum employee coverage for each shift.
- Prevents employees from exceeding their maximum weekly shift limit.
- Handles invalid or incomplete input gracefully.

## Project Structure
- **`src/main/java/org/user/schedule`**: Contains the main application logic.
    - `Scheduler.java`: Entry point for the application.
    - `ShiftAssigner.java`: Core logic for assigning shifts.
- **`src/main/resources`**: Contains input files for employee preferences.
    - `weeklyshift.txt`: Example input file for scenario 1.
    - `weeklyshift2.txt`: Example input file for scenario 2.

## How to Run
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd <repository-folder>