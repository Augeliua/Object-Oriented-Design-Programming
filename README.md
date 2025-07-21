
# SC2002 BTO Management System

## Overview
This is a Java-based console application for managing HDB Build-To-Order (BTO) flats. It supports the roles of Applicants, HDB Officers, and HDB Managers. Features include:
- Project creation and management
- Application submission and processing
- Flat booking and withdrawal handling
- Enquiry submission and response
- Report and Receipt generation 

## Project Structure
- `src/sc2002/bto/entity`: Entity classes such as `Applicant`, `Project`, `Application`, etc.
- `src/sc2002/bto/repository`: Repositories that manage persistence for applications, enquiries, users, etc.
- `src/sc2002/bto/ui`: UI classes for different user roles (`ApplicantUI`, `OfficerUI`, `ManagerUI`).
- `src/sc2002/bto/util`: Utility classes, including `ProjectFilter`.
- `src/sc2002/bto/enums`: Enumerations for domain constants (e.g. `FlatType`, `ApplicationStatus`).
- `src/sc2002/bto/interfaces`: Interfaces for various functionalities (e.g. enquiry management, application processing).

## Setup Instructions
1. Import the project into any Java IDE (e.g., IntelliJ IDEA or Eclipse).
2. Ensure that the Java version is at least Java 8.
3. Run the main class (usually inside a separate `Main.java` file, not shown here).
4. Ensure that the .csv files in the data/ folder are closed in all programs before running the application.

## How to Run on Vscode
You can compile and run the application using the terminal (e.g. PowerShell on Windows):
### Step 1: Navigate to the `src` folder
```bash
cd sc2002_group_project/sc2002_group_project/src
```
### Step 2: Compile all Java files
```bash
javac sc2002/bto/ui/*.java sc2002/bto/entity/*.java sc2002/bto/util/*.java sc2002/bto/repository/*.java sc2002/bto/enums/*.java
```
### Step 3: Run the main program
```bash
java sc2002.bto.ui.MainUI
```

## Notes
- All data is saved via file-based repositories (e.g., CSV).
- Follow the menu to log in and operate based on your user role.
