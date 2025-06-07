package org.example;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static Connection connection;
    private static Integer currentMemberId = null;

    private static Integer currentTrainerId = null;

    private static String currentTrainerFirstName = null;

    private static String currentTrainerLastName = null;
    private static String currentMemberFirstName = null;
    private static String currentMemberLastName = null;

    public static void main(String[] args) {
        connectToDatabase();
        start();
    }

    private static void connectToDatabase() {
        // You would never do this on production, but this was a school project
        String url = "jdbc:postgresql://localhost:5432/Project3005";
        String user = "postgres";
        String password = "uzonna";
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void start(){
        int command_flow = 0;
        while (command_flow == 0) {
            System.out.println("Commands:");
            System.out.println("1: Member (Create new member, select existing member)");
            System.out.println("2: Trainer (Manage schedules, view members)");
            System.out.println("3: Admin (Manage rooms, equipment and payment)");
            String input = scanner.nextLine(); // Read user input

            if (input.equalsIgnoreCase("exit")) {
                command_flow = -1; //Exit the program
                break;
            }

            if(input.equalsIgnoreCase("1")){
                command_flow = 1;
            }
            if(input.equalsIgnoreCase("2")){
                command_flow = 2;
            }
            if(input.equalsIgnoreCase("3")){
                command_flow = 3;
            }
        }

        if(command_flow == 1){
            Create_Select();
        }

        if(command_flow == 2){
            Trainer_Start();
        }

        if(command_flow == 3){
            Admin_MainMenu();
        }

    }
    //Choose between creating and selecting a member
    private static void Create_Select(){
        int command_flow = 0;
        while (command_flow == 0) {
            System.out.println("Commands:");
            System.out.println("1: Create new member");
            System.out.println("2: Select existing member");
            String input = scanner.nextLine(); // Read user input

            if (input.equalsIgnoreCase("exit")) {
                command_flow = -1; //Exit the program
                break;
            }

            if(input.equalsIgnoreCase("1")){
                command_flow = 1;
            }
            if(input.equalsIgnoreCase("2")){
                command_flow = 2;
            }
        }

        if(command_flow == 1){
            Create_Member();
        }

        if(command_flow == 2){
            Select_Member();
        }
    }



    private static void Update_Profile(){
        int command_flow = 0;
        while (command_flow == 0) {
            System.out.println("Commands:");
            System.out.println("1: Update member information");
            System.out.println("2: Add goals");
            System.out.println("3: Add health metrics");
            System.out.println("4: Link bank account");
            System.out.println("5: Main Menu");

            String input = scanner.nextLine(); // Read user input

            if (input.equalsIgnoreCase("exit")) {
                command_flow = -1; //Exit the program
                break;
            }

            if(input.equalsIgnoreCase("1")){
                command_flow = 1;
                break;
            }
            if(input.equalsIgnoreCase("2")){
                command_flow = 2;
                break;
            }
            if(input.equalsIgnoreCase("3")){
                command_flow = 3;
                break;
            }
            if(input.equalsIgnoreCase("4")){
                command_flow = 4;
                break;
            }
            if(input.equalsIgnoreCase("5")){
                command_flow = 5;
                break;
            }
        }

        if(command_flow == 1){ Update_Member_Info();}
        if(command_flow == 2){ Update_Goals();}
        if(command_flow == 3){ Update_Health_Metrics();}
        if(command_flow == 4){ Link_Bank_Account();}
        if(command_flow == 5){ Update_View_Schedule();}
    }

    private static void Link_Bank_Account(){
        int command_flow = 0;
        while (command_flow == 0) {

            String checkAccountQuery = "SELECT COUNT(*) FROM BankAccount WHERE MemberID = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkAccountQuery)) {
                checkStmt.setInt(1, currentMemberId);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("You already have a bank account in the system.");
                        break;
                    }
                }


                System.out.println("Enter the initial amount for the bank account:");
                BigDecimal amount = scanner.nextBigDecimal();


                String insertAccountQuery = "INSERT INTO BankAccount (MemberID, Amount) VALUES (?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertAccountQuery)) {
                    insertStmt.setInt(1, currentMemberId);
                    insertStmt.setBigDecimal(2, amount);

                    int rowsAffected = insertStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Bank account created successfully.");
                        break;
                    } else {
                        System.out.println("Failed to create a bank account.");
                        break;
                    }
                }
            } catch (SQLException sqle) {
                System.out.println("A database error occurred.");
                sqle.printStackTrace();
                break;
            } catch (Exception e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
                break;
            }
        }

        Update_Profile();




    }

    private static void View_Dashboard(){
        try {
            // Members Data
            System.out.println("Member Information:");
            displayQueryResults("SELECT * FROM Members WHERE MemberID = ?", currentMemberId);

            // MemberGoals Data
            System.out.println("\nMember Goals:");
            displayQueryResults("SELECT * FROM MemberGoals WHERE MemberID = ?", currentMemberId);

            // HealthMetrics Data
            System.out.println("\nHealth Metrics:");
            displayQueryResults("SELECT * FROM HealthMetrics WHERE MemberID = ?", currentMemberId);

            // TrainingSessions Data
            System.out.println("\nTraining Sessions:");
            displayQueryResults("SELECT * FROM TrainingSessions WHERE MemberID = ?", currentMemberId);

            // ClassRegistrations Data
            System.out.println("\nClass Registrations:");
            displayQueryResults("SELECT * FROM ClassRegistrations WHERE MemberID = ?", currentMemberId);

            // Billing Data
            System.out.println("\nBilling Information:");
            displayQueryResults("SELECT * FROM Billing WHERE MemberID = ?", currentMemberId);

            // BankAccount Data
            System.out.println("\nBank Account Information:");
            displayQueryResults("SELECT * FROM BankAccount WHERE MemberID = ?", currentMemberId);

        } catch (SQLException e) {
            System.out.println("An error occurred while retrieving member data.");
            e.printStackTrace();
        }
        Update_View_Schedule();
    }

    private static void Update_Member_Info(){
        int command_flow = 0;
        while (command_flow == 0) {
            System.out.println("Updating data is in the form (Field, NewEntry)");
            System.out.println("Field Options: FirstName, LastName, Email, Password, DateOfBirth");
            System.out.println("Use 'back' to return to previous menu");


            String input = scanner.nextLine(); // Read user input

            if (input.equalsIgnoreCase("back")) {
                command_flow = 1;
                break;
            }
            if (input.equalsIgnoreCase("exit")) {
                command_flow = -1;
                break;
            }

            String[] parts = input.split(",", 2);
            if (parts.length != 2) {
                System.out.println("Invalid update command. Please use the format: Field, NewEntry");
                command_flow = 2;
            }

            String fieldName = parts[0].trim();
            String newValue = parts[1].trim();

            // Validate the field name
            switch (fieldName) {
                case "FirstName":
                case "LastName":
                case "Email":
                case "Password":
                case "DateOfBirth":
                    break;
                default:
                    System.out.println("Invalid field name.");
                    //There may be a bug here if an invalid field name is entered
            }

            String sql = String.format("UPDATE Members SET %s = ? WHERE MemberID = ?;", fieldName);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newValue);
                preparedStatement.setInt(2, currentMemberId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Member information updated successfully.");
                    command_flow = 1;
                } else {
                    System.out.println("Failed to update member information.");
                    command_flow = 2;
                }
            } catch (Exception e) {
                System.out.println("An error occurred while updating member information.");
                command_flow = 2;
            }
        }

        if(command_flow == 1){
            Update_Profile();
        }
        if(command_flow == 2){
            Update_Member_Info();
        }



    }

    private static void Update_Goals(){
        int command_flow = 0;
        while (command_flow == 0) {
            System.out.println("Adding goals is in the form: GoalType, TargetValue, StartDate (YYYY-MM-DD), EndDate (YYYY-MM-DD)");
            System.out.println("Use 'back' to return to previous menu");
            String input = scanner.nextLine(); // Read user input

            if (input.equalsIgnoreCase("back")) {
                command_flow = 1;
                break;
            }
            if (input.equalsIgnoreCase("exit")) {
                command_flow = -1;
                break;
            }

            String[] parts = input.split(",", -1);

            if (parts.length == 4) {
                String goalType = parts[0].trim();
                String targetValue = parts[1].trim();
                String startDateStr = parts[2].trim();
                String endDateStr = parts[3].trim();

                try {
                    java.sql.Date startDate = java.sql.Date.valueOf(startDateStr);
                    java.sql.Date endDate = java.sql.Date.valueOf(endDateStr);


                    String sql = "INSERT INTO MemberGoals (MemberID, GoalType, TargetValue, StartDate, EndDate) VALUES (?, ?, ?, ?, ?);";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setInt(1, currentMemberId);
                        preparedStatement.setString(2, goalType);
                        preparedStatement.setString(3, targetValue);
                        preparedStatement.setDate(4, startDate);
                        preparedStatement.setDate(5, endDate);

                        int rowsChanged = preparedStatement.executeUpdate();
                        if (rowsChanged > 0) {
                            System.out.println("Goal added successfully.");
                            command_flow = 1;
                        } else {
                            System.out.println("Failed to add the goal.");
                            command_flow = 2;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred. Please ensure the dates are in the format YYYY-MM-DD and try again.");
                    command_flow = 2;
                }
            } else {
                System.out.println("Invalid input format. Please ensure your input matches the expected format: GoalType, TargetValue, StartDate (YYYY-MM-DD), EndDate (YYYY-MM-DD)");
                command_flow = 2;
            }
        }

        if(command_flow == 1){
            Update_Profile();
        }

        if(command_flow == 2){
            Update_Goals();
        }


    }

    private static void Update_Health_Metrics(){
        int command_flow = 0;
        while (command_flow == 0) {
            System.out.println("Adding health metrics is in the form: MetricType, Value, RecordDate (YYYY-MM-DD)");
            System.out.println("Use 'back' to return to previous menu");
            String input = scanner.nextLine(); // Read user input

            if (input.equalsIgnoreCase("back")) {
                command_flow = 1;
                break;
            }
            if (input.equalsIgnoreCase("exit")) {
                command_flow = -1;
                break;
            }

            String[] parts = input.split(",", -1);

            if (parts.length == 3) {
                String metricType = parts[0].trim();
                String valueStr = parts[1].trim();
                String recordDateStr = parts[2].trim();

                try {
                    java.sql.Date recordDate = java.sql.Date.valueOf(recordDateStr);

                    java.math.BigDecimal value = new java.math.BigDecimal(valueStr);


                    String sql = "INSERT INTO HealthMetrics (MemberID, MetricType, Value, RecordDate) VALUES (?, ?, ?, ?);";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setInt(1, currentMemberId);
                        preparedStatement.setString(2, metricType);
                        preparedStatement.setBigDecimal(3, value);
                        preparedStatement.setDate(4, recordDate);

                        int rowsChanged = preparedStatement.executeUpdate();
                        if (rowsChanged > 0) {
                            System.out.println("Health metric added successfully.");
                            command_flow = 1;
                        } else {
                            System.out.println("Failed to add the health metric.");
                            command_flow = 2;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred. Please ensure the input format is correct and try again.");
                    command_flow = 2;
                }
            } else {
                System.out.println("Invalid input format. Please ensure your input matches the expected format: MetricType, Value, RecordDate (YYYY-MM-DD)");
                command_flow = 2;
            }
        }

        if(command_flow == 1){
            Update_Profile();
        }

        if(command_flow == 2){
            Update_Health_Metrics();
        }
    }

    private static void Single_or_Group(){
        int command_flow = 0;
        while (command_flow == 0) {
            System.out.println("Commands:");
            System.out.println("1: Schedule a personal session");
            System.out.println("2: Attend a group class");
            System.out.println("3: Cancel a personal session");
            System.out.println("4: Leave a group class");
            String input = scanner.nextLine(); // Read user input

            if (input.equalsIgnoreCase("exit")) {
                command_flow = -1; //Exit the program
                break;
            }

            if (input.equalsIgnoreCase("back")) {
                command_flow = -2;
            }

            if(input.equalsIgnoreCase("1")){
                command_flow = 1;
            }
            if(input.equalsIgnoreCase("2")){
                command_flow = 2;
            }
            if(input.equalsIgnoreCase("3")){
                command_flow = 3;
            }
            if(input.equalsIgnoreCase("4")){
                command_flow = 4;
            }
        }

        if(command_flow == 1){
            Schedule_Personal();
        }

        if(command_flow == 2){
            Schedule_Group();
        }

        if(command_flow == -2){
            Update_View_Schedule();
        }

        if(command_flow == 3){
            Cancel_Personal();
        }

        if(command_flow == 4){
            Cancel_Group();
        }
    }

    private static void Cancel_Group(){
        while (true) {
            try {

                String listRegistrationsQuery = "SELECT RegistrationID, ClassName, StartTime, EndTime " +
                        "FROM ClassRegistrations cr " +
                        "JOIN GroupClasses gc ON cr.ClassID = gc.ClassID " +
                        "WHERE cr.MemberID = ?";
                try (PreparedStatement listRegistrationsStmt = connection.prepareStatement(listRegistrationsQuery)) {
                    listRegistrationsStmt.setInt(1, currentMemberId);
                    ResultSet rs = listRegistrationsStmt.executeQuery();

                    System.out.println("Your Class Registrations:");
                    while (rs.next()) {

                        System.out.println("RegistrationID: " + rs.getInt("RegistrationID") +
                                ", ClassName: " + rs.getString("ClassName") +
                                ", StartTime: " + rs.getTimestamp("StartTime") +
                                ", EndTime: " + rs.getTimestamp("EndTime"));
                    }
                }


                System.out.println("Enter the RegistrationID of the registration you wish to cancel:");
                int registrationIdToCancel = Integer.parseInt(scanner.nextLine());


                String deleteRegistrationQuery = "DELETE FROM ClassRegistrations WHERE RegistrationID = ? AND MemberID = ?";
                try (PreparedStatement deleteRegistrationStmt = connection.prepareStatement(deleteRegistrationQuery)) {
                    deleteRegistrationStmt.setInt(1, registrationIdToCancel);
                    deleteRegistrationStmt.setInt(2, currentMemberId);

                    int rowsAffected = deleteRegistrationStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Your registration has been successfully canceled.");
                        break;
                    } else {
                        System.out.println("No registration found with the provided ID for the current member.");
                        break;
                    }
                }
            } catch (SQLException sqle) {
                System.out.println("Database error: " + sqle.getMessage());
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid input. Please enter a valid RegistrationID.");
                break;
            }
        }
        Single_or_Group();
    }

    private static void Cancel_Personal(){
        while (true) {
            try {

                String listSessionsQuery = "SELECT SessionID, TrainerID, StartTime, EndTime " +
                        "FROM TrainingSessions " +
                        "WHERE MemberID = ?";
                try (PreparedStatement listSessionsStmt = connection.prepareStatement(listSessionsQuery)) {
                    listSessionsStmt.setInt(1, currentMemberId);

                    ResultSet rs = listSessionsStmt.executeQuery();

                    System.out.println("Your Training Sessions:");
                    while (rs.next()) {

                        System.out.println("SessionID: " + rs.getInt("SessionID") +
                                ", TrainerID: " + rs.getInt("TrainerID") +
                                ", StartTime: " + rs.getTimestamp("StartTime") +
                                ", EndTime: " + rs.getTimestamp("EndTime"));
                    }
                }


                System.out.println("Enter the SessionID of the session you wish to cancel:");
                int sessionIdToCancel = Integer.parseInt(scanner.nextLine());


                String deleteSessionQuery = "DELETE FROM TrainingSessions WHERE SessionID = ? AND MemberID = ?";
                try (PreparedStatement deleteSessionStmt = connection.prepareStatement(deleteSessionQuery)) {
                    deleteSessionStmt.setInt(1, sessionIdToCancel);
                    deleteSessionStmt.setInt(2, currentMemberId);

                    int rowsAffected = deleteSessionStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Your training session has been successfully canceled.");
                        break;
                    } else {
                        System.out.println("No session found with the provided ID for the current member, or you cannot cancel this session.");
                        break;
                    }
                }
            } catch (SQLException sqle) {
                System.out.println("Database error: " + sqle.getMessage());
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid input. Please enter a valid SessionID.");
                break;
            }
        }
        Single_or_Group();
    }

    private static void Schedule_Personal(){
        // Print all trainers
        try {
            String trainersSql = "SELECT * FROM Trainers;"; // Changed to select all columns
            try (PreparedStatement psTrainers = connection.prepareStatement(trainersSql);
                 ResultSet rsTrainers = psTrainers.executeQuery()) {
                System.out.println("Available Trainers:");
                while (rsTrainers.next()) {
                    int trainerId = rsTrainers.getInt("TrainerID");
                    String firstName = rsTrainers.getString("FirstName");
                    String lastName = rsTrainers.getString("LastName");
                    String specialization = rsTrainers.getString("Specialization");
                    Timestamp startTime = rsTrainers.getTimestamp("StartTime");
                    Timestamp endTime = rsTrainers.getTimestamp("EndTime");

                    System.out.println("ID: " + trainerId +
                            " - Name: " + firstName + " " + lastName +
                            " - Specialization: " + specialization +
                            " - Availability: From " + startTime +
                            " to " + endTime);
                }
            }
        } catch (Exception e) {
            System.out.println("Could not fetch trainers.");
            e.printStackTrace();
            return; // Exit if unable to fetch trainers
        }

        int command_flow = 0;
        while (command_flow == 0) {
            System.out.println("Enter session details in the format: TrainerID, StartTime (YYYY-MM-DD HH:MM:SS), EndTime (YYYY-MM-DD HH:MM:SS)");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                command_flow = -1; //Exit the program
                break;
            }

            if (input.equalsIgnoreCase("back")) {
                command_flow = 2;
                break;
            }

            String[] parts = input.split(",", 3);

            if (parts.length != 3) {
                System.out.println("Invalid input format.");
                continue;
            }

            int trainerID = Integer.parseInt(parts[0].trim());
            String startTimeStr = parts[1].trim();
            String endTimeStr = parts[2].trim();

            if (!isTrainerAvailable(trainerID, startTimeStr, endTimeStr)) {
                System.out.println("Trainer is not available during the specified time.");
                continue;
            }

            if (doesSessionOverlap(trainerID, startTimeStr, endTimeStr)) {
                System.out.println("There is an overlapping session for the selected trainer.");
                continue;
            }


            try {
                String sql = "INSERT INTO TrainingSessions (MemberID, TrainerID, StartTime, EndTime) VALUES (?, ?, ?, ?);";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, currentMemberId);
                    preparedStatement.setInt(2, trainerID);
                    preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(startTimeStr));
                    preparedStatement.setTimestamp(4, java.sql.Timestamp.valueOf(endTimeStr));

                    int rowsChanged = preparedStatement.executeUpdate();
                    if (rowsChanged > 0) {
                        System.out.println("Training session added successfully.");

                        Update_View_Schedule();
                        break;
                    } else {
                        System.out.println("Failed to add the training session.");
                    }
                }
            } catch (Exception e) {
                System.out.println("An error occurred while adding the training session.");
                e.printStackTrace();
            }
        }
        if (command_flow == 2) {
            Update_View_Schedule();
        }
    }

    private static void Schedule_Group() {
        int command_flow = 0;
        while (command_flow == 0) {
            try {

                String listClassesQuery = "SELECT gc.ClassID, gc.ClassName, gc.StartTime, gc.EndTime, t.FirstName, t.LastName, r.RoomName, r.Capacity, COUNT(cr.MemberID) AS Registrations " +
                        "FROM GroupClasses gc " +
                        "LEFT JOIN Trainers t ON gc.TrainerID = t.TrainerID " +
                        "LEFT JOIN Rooms r ON gc.RoomID = r.RoomID " +
                        "LEFT JOIN ClassRegistrations cr ON gc.ClassID = cr.ClassID " +
                        "GROUP BY gc.ClassID, t.FirstName, t.LastName, r.RoomName, r.Capacity " +
                        "ORDER BY gc.ClassID";

                try (PreparedStatement listClassesStmt = connection.prepareStatement(listClassesQuery);
                     ResultSet rs = listClassesStmt.executeQuery()) {

                    System.out.println("Available Classes:");
                    while (rs.next()) {

                        System.out.println("ClassID: " + rs.getInt("ClassID") + ", ClassName: " + rs.getString("ClassName") +
                                ", Trainer: " + rs.getString("FirstName") + " " + rs.getString("LastName") +
                                ", StartTime: " + rs.getTimestamp("StartTime") +
                                ", EndTime: " + rs.getTimestamp("EndTime") +
                                ", Room: " + rs.getString("RoomName") +
                                ", Capacity: " + rs.getInt("Capacity") +
                                ", Registrations: " + rs.getInt("Registrations"));
                    }
                }

                System.out.println("Enter the ClassID you wish to join or 'exit' to return:");
                String input = scanner.nextLine();

                if ("exit".equalsIgnoreCase(input.trim())) {
                    return;
                }

                int selectedClassId = Integer.parseInt(input);


                String checkClassAndCapacityQuery = "SELECT gc.ClassID, r.Capacity, COUNT(cr.ClassID) AS Registrations " +
                        "FROM GroupClasses gc " +
                        "JOIN Rooms r ON gc.RoomID = r.RoomID " +
                        "LEFT JOIN ClassRegistrations cr ON gc.ClassID = cr.ClassID " +
                        "WHERE gc.ClassID = ? " +
                        "GROUP BY gc.ClassID, r.Capacity";
                try (PreparedStatement checkClassAndCapacityStmt = connection.prepareStatement(checkClassAndCapacityQuery)) {
                    checkClassAndCapacityStmt.setInt(1, selectedClassId);
                    ResultSet rsCheck = checkClassAndCapacityStmt.executeQuery();

                    if (rsCheck.next()) {
                        int registrations = rsCheck.getInt("Registrations");
                        int capacity = rsCheck.getInt("Capacity");
                        if (registrations < capacity) {

                            String registerQuery = "INSERT INTO ClassRegistrations (ClassID, MemberID) VALUES (?, ?)";
                            try (PreparedStatement registerStmt = connection.prepareStatement(registerQuery)) {
                                registerStmt.setInt(1, selectedClassId);
                                registerStmt.setInt(2, currentMemberId);
                                registerStmt.executeUpdate();
                                System.out.println("You have successfully joined the class.");
                                command_flow = 1;
                                break;
                            }
                        } else {
                            System.out.println("The class is full.");
                        }
                    } else {
                        System.out.println("The class does not exist.");
                    }
                }
            } catch (SQLException sqle) {
                System.out.println("Database error: " + sqle.getMessage());
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid input. Please enter a valid ClassID.");
            }
        }

        if (command_flow == 1) {
            Update_View_Schedule();
        }
    }

    private static void Update_View_Schedule(){
        int command_flow = 0;
        while (command_flow == 0) {
            System.out.println("\nMAIN MENU (" + currentMemberFirstName + " " + currentMemberLastName + ")");
            System.out.println("1: Update any profile information");
            System.out.println("2: View Dashboard");
            System.out.println("3: Manage Sessions and Classes");
            System.out.println("4: Logout > Login as new user");
            System.out.println("5: Logout > Return to user selection");
            String input = scanner.nextLine(); // Read user input

            if (input.equalsIgnoreCase("exit")) {
                command_flow = -1; //Exit the program
                break;
            }

            if(input.equalsIgnoreCase("1")){
                command_flow = 1;
            }
            if(input.equalsIgnoreCase("2")){
                command_flow = 2;
            }
            if(input.equalsIgnoreCase("3")){
                command_flow = 3;
            }
            if(input.equalsIgnoreCase("4")){
                command_flow = 4;
            }
            if(input.equalsIgnoreCase("5")){
                command_flow = 5;
            }
        }

        if(command_flow == 1){
            Update_Profile();
        }

        if(command_flow == 2){
            View_Dashboard();
        }

        if(command_flow == 3){
            Single_or_Group();
        }

        if(command_flow == 4){
            Select_Member();
        }

        if(command_flow == 5){
            start();
        }
    }

    private static void Create_Member(){
        int command_flow = 0;
        while (command_flow == 0) {
            System.out.println("Enter member details in the format: FirstName, LastName, Email, Password, DateOfBirth (YYYY-MM-DD)");
            String input = scanner.nextLine(); // Read user input

            if (input.equalsIgnoreCase("1")) {
                command_flow = -1; //Exit the program
                break;
            }

            String[] parts = input.split(",");
            if (parts.length == 5) {
                String firstName = parts[0].trim();
                String lastName = parts[1].trim();
                String email = parts[2].trim();
                String password = parts[3].trim();
                String dateOfBirthStr = parts[4].trim();



                try {
                    java.sql.Date dateOfBirth = java.sql.Date.valueOf(dateOfBirthStr);


                    String sql = "INSERT INTO Members (FirstName, LastName, Email, Password, DateOfBirth) VALUES (?, ?, ?, ?, ?);";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, firstName);
                    preparedStatement.setString(2, lastName);
                    preparedStatement.setString(3, email);
                    preparedStatement.setString(4, password);
                    preparedStatement.setDate(5, dateOfBirth);

                    int rowsChanged = preparedStatement.executeUpdate();
                    if (rowsChanged > 0) {
                        System.out.println("Member added successfully.");
                        command_flow = 1;
                    } else {
                        System.out.println("Failed to add the member.");
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred while adding the member. Please ensure the input format is correct and try again.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("Invalid input format. Please ensure your input matches the expected format: FirstName, LastName, Email, Password, DateOfBirth (YYYY-MM-DD)");
            }


        }

        if (command_flow == 1){
            Create_Select();
        }


    }

    private static void Select_Member(){
        int command_flow = 0;
        while(command_flow == 0){
            System.out.println("Enter your login credentials in the format: email,password");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                command_flow = -1; //Exit the program
                break;
            }

            if (input.equalsIgnoreCase("back")) {
                command_flow = 2; //Exit the program
                break;
            }

            String[] credentials = input.split(",");
            if (credentials.length == 2) {
                String email = credentials[0].trim();
                String password = credentials[1].trim();


                String sql = "SELECT MemberID, FirstName, LastName FROM Members WHERE Email = ? AND Password = ?;";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, email);
                    preparedStatement.setString(2, password);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            // found the user
                            String firstName = resultSet.getString("FirstName");
                            currentMemberFirstName = firstName;
                            String lastName = resultSet.getString("LastName");
                            currentMemberLastName = lastName;
                            currentMemberId = resultSet.getInt("MemberID");
                            System.out.println("Connected as " + firstName + " " + lastName);
                            command_flow = 1;
                        } else {
                            // user not found
                            System.out.println("Login failed. Please check your credentials and try again.");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred during login.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("Invalid input format. Please ensure your input matches the expected format: email,password");
            }
        }

        if(command_flow == 1){
            Update_View_Schedule();
        }

        if(command_flow == 2){
            Create_Select();
        }
    }

    private static boolean isTrainerAvailable(int trainerID, String startTimeStr, String endTimeStr) {

        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(startTimeStr);
        java.sql.Timestamp endTime = java.sql.Timestamp.valueOf(endTimeStr);


        String sql = "SELECT StartTime, EndTime FROM Trainers WHERE TrainerID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, trainerID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {

                    java.sql.Timestamp trainerStartTime = resultSet.getTimestamp("StartTime");
                    java.sql.Timestamp trainerEndTime = resultSet.getTimestamp("EndTime");


                    return !startTime.before(trainerStartTime) && !endTime.after(trainerEndTime);
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while checking the trainer's availability.");
            e.printStackTrace();
        }
        return false;
    }

    private static boolean doesSessionOverlap(int trainerID, String startTimeStr, String endTimeStr) {

        java.sql.Timestamp startTime = java.sql.Timestamp.valueOf(startTimeStr);
        java.sql.Timestamp endTime = java.sql.Timestamp.valueOf(endTimeStr);


        String sql = "SELECT COUNT(*) FROM TrainingSessions WHERE TrainerID = ? AND NOT (EndTime <= ? OR StartTime >= ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, trainerID);
            preparedStatement.setTimestamp(2, startTime);
            preparedStatement.setTimestamp(3, endTime);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while checking for overlapping sessions.");
            e.printStackTrace();
        }
        return false;
    }

    private static void displayQueryResults(String query, int memberId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= columnsNumber; i++) {
                        if (i > 1) System.out.print(",  ");
                        String columnValue = rs.getString(i);
                        System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                    }
                    System.out.println();
                }
            }
        }
    }

    // TRAINER SECTION BEGINS HERE

    private static void Trainer_Start(){
        int command_flow = 0;
        while (true) {
            try {
                // Print everything from the trainer table
                String query = "SELECT * FROM Trainers";
                try (PreparedStatement stmt = connection.prepareStatement(query);
                     ResultSet rs = stmt.executeQuery()) {

                    System.out.println("Available Trainers:");
                    while (rs.next()) {
                        System.out.println("TrainerID: " + rs.getInt("TrainerID") +
                                ", FirstName: " + rs.getString("FirstName") +
                                ", LastName: " + rs.getString("LastName") +
                                ", Specialization: " + rs.getString("Specialization"));
                    }
                }


                System.out.println("Enter a TrainerID to select:");
                currentTrainerId = Integer.parseInt(scanner.nextLine());


                query = "SELECT FirstName, LastName FROM Trainers WHERE TrainerID = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setInt(1, currentTrainerId);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            currentTrainerFirstName = rs.getString("FirstName");
                            currentTrainerLastName = rs.getString("LastName");
                            System.out.println("You have selected Trainer: " +
                                    currentTrainerFirstName + " " + currentTrainerLastName);
                            command_flow = 1;
                            break;
                        } else {
                            System.out.println("No trainer found with the provided TrainerID.");
                            currentTrainerId = null; // Reset the trainer ID as no valid selection was made
                            command_flow = 2;
                            break;
                        }
                    }
                }
            } catch (SQLException sqle) {
                System.out.println("Database error occurred: " + sqle.getMessage());
                command_flow = 2;
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid TrainerID entered. Please enter a numeric ID.");
                command_flow = 2;
                break;
            }
        }
        if(command_flow == 1){
            Trainer_MainMenu();
        }else{
            start();
        }

    }

    public static void Trainer_MainMenu(){
        int command_flow = 0;
        while (command_flow == 0) {
            System.out.println("\nMAIN MENU (" + currentTrainerFirstName + " " + currentTrainerLastName + ")");
            System.out.println("1: Change Availability");
            System.out.println("2: Search for Member");
            System.out.println("3: Logout > Login as new trainer");
            System.out.println("4: Logout > Return to user selection");
            String input = scanner.nextLine(); // Read user input

            if (input.equalsIgnoreCase("exit")) {
                command_flow = -1; //Exit the program
                break;
            }

            if(input.equalsIgnoreCase("1")){
                command_flow = 1;
            }
            if(input.equalsIgnoreCase("2")){
                command_flow = 2;
            }
            if(input.equalsIgnoreCase("3")){
                command_flow = 3;
            }
            if(input.equalsIgnoreCase("4")){
                command_flow = 4;
            }

        }

        if(command_flow == 1){
            Trainer_Availability();
        }

        if(command_flow == 2){
            Trainer_Search();
        }

        if(command_flow == 3){
            Trainer_Start();
        }

        if(command_flow == 4){
            start();
        }


    }

    public static void Trainer_Availability(){
        while (true) {
            System.out.println("Enter your new availability (start time and end time) in the format: YYYY-MM-DD HH:MM, YYYY-MM-DD HH:MM");
            String input = scanner.nextLine();
            String[] times = input.split(",", 2);

            if (times.length == 2) {
                String newStartTime = times[0].trim() + ":00"; // Append seconds to match the TIMESTAMP format
                String newEndTime = times[1].trim() + ":00";

                String updateQuery = "UPDATE Trainers SET StartTime = ?, EndTime = ? WHERE TrainerID = ?";

                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setTimestamp(1, java.sql.Timestamp.valueOf(newStartTime));
                    updateStmt.setTimestamp(2, java.sql.Timestamp.valueOf(newEndTime));
                    updateStmt.setInt(3, currentTrainerId);

                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Your availability has been successfully updated.");
                        break;
                    } else {
                        System.out.println("Failed to update availability. Please check the Trainer ID and try again.");
                        break;
                    }
                } catch (SQLException sqle) {
                    System.out.println("Database error: " + sqle.getMessage());
                    break;

                } catch (IllegalArgumentException iae) {
                    System.out.println("Invalid date and time format. Please ensure the format is correct and try again.");
                    break;
                }
            } else {
                System.out.println("Invalid input format. Please enter the start time and end time in the correct format.");
                break;
            }
        }
        Trainer_MainMenu();
    }

    public static void Trainer_Search() {
        int selected = -1;
        int command_flow = 0;
        while (true) {
            try {
                String membersSql = "SELECT MemberID, FirstName, LastName FROM Members;";
                try (PreparedStatement psMembers = connection.prepareStatement(membersSql);
                     ResultSet rsMembers = psMembers.executeQuery()) {
                    System.out.println("Members:");
                    while (rsMembers.next()) {
                        int memberId = rsMembers.getInt("MemberID");
                        String firstName = rsMembers.getString("FirstName");
                        String lastName = rsMembers.getString("LastName");
                        System.out.println("ID: " + memberId + " - Name: " + firstName + " " + lastName);
                    }

                    System.out.println("Enter the ID of the member you want to view:");
                    selected = Integer.parseInt(scanner.nextLine());
                    try {
                        // Print member data from Members table
                        System.out.println("Member Information:");
                        printQueryResults("SELECT * FROM Members WHERE MemberID = ?", selected);

                        // Print member goals from MemberGoals table
                        System.out.println("\nMember Goals:");
                        printQueryResults("SELECT * FROM MemberGoals WHERE MemberID = ?", selected);

                        // Print health metrics from HealthMetrics table
                        System.out.println("\nHealth Metrics:");
                        printQueryResults("SELECT * FROM HealthMetrics WHERE MemberID = ?", selected);
                        break;

                    } catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                        break;
                    }

                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid MemberID entered.");
                break;
            }

        }
        Trainer_MainMenu();
    }

    private static void printQueryResults(String query, int memberId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        String columnName = rsmd.getColumnName(i);
                        String value = rs.getString(i);
                        System.out.print(columnName + ": " + value + "; ");
                    }
                    System.out.println();
                }
            }
        }
    }

// ADMIN SECTION BEGINS HERE

    private static void Admin_MainMenu(){
        int command_flow = 0;
        while (command_flow == 0) {
            System.out.println("\n ADMINISTRATION MAIN MENU");
            System.out.println("1: Add Room");
            System.out.println("2: Remove Room");
            System.out.println("3: Manage Equipment");
            System.out.println("4: Create Group Class");
            System.out.println("5: Delete Group Class");
            System.out.println("6: Manage Billing and Payment");
            System.out.println("7: Return to user selection");
            String input = scanner.nextLine(); // Read user input

            if (input.equalsIgnoreCase("exit")) {
                command_flow = -1; //Exit the program
                break;
            }

            if(input.equalsIgnoreCase("1")){
                command_flow = 1;
            }
            if(input.equalsIgnoreCase("2")){
                command_flow = 2;
            }
            if(input.equalsIgnoreCase("3")){
                command_flow = 3;
            }
            if(input.equalsIgnoreCase("4")){
                command_flow = 4;
            }
            if(input.equalsIgnoreCase("5")){
                command_flow = 5;
            }
            if(input.equalsIgnoreCase("6")){
                command_flow = 6;
            }
            if(input.equalsIgnoreCase("7")){
                command_flow = 7;
            }



        }

        if(command_flow == 1){
            Admin_AddRoom();
        }

        if(command_flow == 2){
            Admin_DeleteRoom();
        }

        if(command_flow == 3){
            Admin_Equipment();
        }

        if(command_flow == 4){
            Admin_CreateGroup();
        }

        if(command_flow == 5){
            Admin_DeleteGroup();
        }

        if(command_flow == 6){
            Admin_BillOrPay();
        }

        if(command_flow == 7){
            start();
        }
    }

    public static void Admin_AddRoom(){
        while (true) {
            System.out.println("Enter the room's name and capacity (Name, Capacity):");
            String input = scanner.nextLine();
            String[] parts = input.split(",", 2);

            if (parts.length != 2) {
                System.out.println("Invalid input format. Please make sure to use the format: Name, Capacity");
                break;
            }

            String roomName = parts[0].trim();
            int capacity;

            try {
                capacity = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for capacity. Please enter a numeric value.");
                break;
            }


            String sql = "INSERT INTO Rooms (RoomName, Capacity) VALUES (?, ?);";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, roomName);
                preparedStatement.setInt(2, capacity);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Room added successfully.");
                    break;
                } else {
                    System.out.println("Failed to add the room.");
                    break;
                }
            } catch (SQLException e) {
                System.out.println("Database error occurred.");
                e.printStackTrace();
                break;
            }
        }
        Admin_MainMenu();
    }

    public static void Admin_DeleteRoom(){
        while(true){
            try {

                String queryRooms = "SELECT * FROM Rooms";
                try (PreparedStatement psRooms = connection.prepareStatement(queryRooms);
                     ResultSet rsRooms = psRooms.executeQuery()) {

                    System.out.println("Available Rooms:");
                    while (rsRooms.next()) {
                        System.out.println("RoomID: " + rsRooms.getInt("RoomID") +
                                ", RoomName: " + rsRooms.getString("RoomName") +
                                ", Capacity: " + rsRooms.getInt("Capacity"));
                    }
                }


                System.out.println("Enter the RoomID of the room you want to delete:");
                int roomId = Integer.parseInt(scanner.nextLine());


                String queryClasses = "SELECT COUNT(*) AS classCount FROM GroupClasses WHERE RoomID = ?";
                try (PreparedStatement psClasses = connection.prepareStatement(queryClasses)) {
                    psClasses.setInt(1, roomId);

                    try (ResultSet rsClasses = psClasses.executeQuery()) {
                        if (rsClasses.next() && rsClasses.getInt("classCount") > 0) {
                            System.out.println("This room cannot be deleted because it is currently used in group classes.");
                            break;
                        }
                    }
                }


                String deleteRoom = "DELETE FROM Rooms WHERE RoomID = ?";
                try (PreparedStatement psDelete = connection.prepareStatement(deleteRoom)) {
                    psDelete.setInt(1, roomId);

                    int rowsAffected = psDelete.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Room deleted successfully.");
                        break;
                    } else {
                        System.out.println("No room found with the provided ID.");
                        break;
                    }
                }
            } catch (SQLException sqle) {
                System.out.println("Database error occurred: " + sqle.getMessage());
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid RoomID entered. Please enter a numeric ID.");
                break;
            }
        }
        Admin_MainMenu();
    }

    public static void Admin_Equipment(){
        while(true){
            try {

                System.out.println("Available Equipment:");
                String query = "SELECT * FROM Equipment";
                try (PreparedStatement stmt = connection.prepareStatement(query);
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("EquipmentID: " + rs.getInt("EquipmentID") +
                                ", EquipmentName: " + rs.getString("EquipmentName") +
                                ", Status: " + rs.getString("Status"));
                    }
                }


                System.out.println("Enter an EquipmentID and status to update, or a new Equipment Name and Status to add:");
                String input = scanner.nextLine();
                String[] parts = input.split(",", 2);

                if (parts.length == 2) {
                    try {
                        // User entered an ID and a string (update scenario)
                        int equipmentId = Integer.parseInt(parts[0].trim());
                        String newStatus = parts[1].trim();

                        String updateQuery = "UPDATE Equipment SET Status = ? WHERE EquipmentID = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, newStatus);
                            updateStmt.setInt(2, equipmentId);

                            int rowsAffected = updateStmt.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Equipment status updated successfully.");
                                break;
                            } else {
                                System.out.println("No equipment found with the provided ID.");
                                break;
                            }
                        }
                    } catch (NumberFormatException e) {
                        // User entered two strings (add scenario)
                        String equipmentName = parts[0].trim();
                        String status = parts[1].trim();

                        String insertQuery = "INSERT INTO Equipment (EquipmentName, Status) VALUES (?, ?)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                            insertStmt.setString(1, equipmentName);
                            insertStmt.setString(2, status);

                            insertStmt.executeUpdate();
                            System.out.println("New equipment added successfully.");
                            break;
                        }
                    }
                } else {
                    System.out.println("Invalid input. Please enter valid data.");
                    break;
                }
            } catch (SQLException e) {
                System.out.println("Database error occurred: " + e.getMessage());
                break;
            }
        }
        Admin_MainMenu();
    }

    public static void Admin_CreateGroup(){
        while(true){
            System.out.println("Enter the class details in the following format: ClassName, TrainerID, StartTime (YYYY-MM-DD HH:MM:SS), EndTime (YYYY-MM-DD HH:MM:SS), RoomID");
            String input = scanner.nextLine();
            String[] classDetails = input.split(",", 5);

            if (classDetails.length != 5) {
                System.out.println("Invalid input format. Please ensure you provide all required details.");
                break;
            }

            String className = classDetails[0].trim();
            int trainerId = Integer.parseInt(classDetails[1].trim());
            String startTime = classDetails[2].trim();
            String endTime = classDetails[3].trim();
            int roomID = Integer.parseInt(classDetails[4].trim());

            String sql = "INSERT INTO GroupClasses (ClassName, TrainerID, StartTime, EndTime, RoomID) VALUES (?, ?, ?, ?, ?);";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, className);
                preparedStatement.setInt(2, trainerId);
                preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(startTime));
                preparedStatement.setTimestamp(4, java.sql.Timestamp.valueOf(endTime));
                preparedStatement.setInt(5, roomID);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Group class created successfully.");
                    break;
                } else {
                    System.out.println("Failed to create the group class.");
                    break;
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric input for TrainerID or RoomID.");
                break;
            }
        }
        Admin_MainMenu();
    }

    public static void Admin_DeleteGroup(){
        while(true){
            try {

                System.out.println("Available Group Classes:");
                String query = "SELECT ClassID, ClassName FROM GroupClasses";
                try (PreparedStatement stmt = connection.prepareStatement(query);
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("ClassID: " + rs.getInt("ClassID") +
                                ", ClassName: " + rs.getString("ClassName"));
                    }
                }


                System.out.println("Enter the ClassID of the group class you want to delete:");
                int classIdToDelete = Integer.parseInt(scanner.nextLine());


                String deleteRegistrationsQuery = "DELETE FROM ClassRegistrations WHERE ClassID = ?";
                try (PreparedStatement deleteRegistrationsStmt = connection.prepareStatement(deleteRegistrationsQuery)) {
                    deleteRegistrationsStmt.setInt(1, classIdToDelete);
                    deleteRegistrationsStmt.executeUpdate();
                    System.out.println("All related class registrations have been deleted.");

                }


                String deleteClassQuery = "DELETE FROM GroupClasses WHERE ClassID = ?";
                try (PreparedStatement deleteClassStmt = connection.prepareStatement(deleteClassQuery)) {
                    deleteClassStmt.setInt(1, classIdToDelete);

                    int rowsAffected = deleteClassStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Group class deleted successfully.");
                        break;
                    } else {
                        System.out.println("No group class found with the provided ID.");
                        break;
                    }
                }
            } catch (SQLException sqle) {
                System.out.println("Database error: " + sqle.getMessage());
                sqle.printStackTrace();
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid input. Please enter a valid ClassID.");
                break;
            }
        }

        Admin_MainMenu();
    }

    public static void Admin_BillOrPay(){
        int command_flow = 0;
        while (command_flow == 0) {
            System.out.println("1: Bill a customer");
            System.out.println("2: Force customer to pay all bills");
            System.out.println("Use 'back' to return to previous menu");
            String input = scanner.nextLine(); // Read user input

            if (input.equalsIgnoreCase("back")) {
                command_flow = -2;
                break;
            }
            if (input.equalsIgnoreCase("exit")) {
                command_flow = -1;
                break;
            }
            if(input.equalsIgnoreCase("1")){
                command_flow = 1;
            }
            if(input.equalsIgnoreCase("2")){
                command_flow = 2;
            }
        }

        if(command_flow == -2){Admin_MainMenu();}
        if(command_flow == 1){Admin_CreateBill();}
        if(command_flow == 2){Admin_ForcePay();}
    }

    public static void Admin_CreateBill(){
        while(true){
            System.out.println("Enter the MemberID:");
            int memberID = Integer.parseInt(scanner.nextLine());

            System.out.println("Enter the bill amount:");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            // The payment status is set to "Outstanding" by default
            String paymentStatus = "Outstanding";


            String sql = "INSERT INTO Billing (MemberID, Amount, PaymentStatus) VALUES (?, ?, ?);";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, memberID);
                preparedStatement.setBigDecimal(2, amount);
                preparedStatement.setString(3, paymentStatus);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Bill created successfully with status 'Outstanding'.");
                    break;
                } else {
                    System.out.println("Failed to create the bill.");
                    break;
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please ensure you enter numeric values for MemberID and Amount.");
                break;
            }
        }
        Admin_BillOrPay();
    }

    public static void Admin_ForcePay(){
        while(true){

            System.out.println("Outstanding Bills:");
            try {
                String queryBills = "SELECT * FROM Billing WHERE PaymentStatus = 'Outstanding'";
                try (PreparedStatement stmt = connection.prepareStatement(queryBills);
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("BillID: " + rs.getInt("BillID") +
                                ", MemberID: " + rs.getInt("MemberID") +
                                ", Amount: " + rs.getBigDecimal("Amount") +
                                ", PaymentStatus: " + rs.getString("PaymentStatus"));
                    }
                }


                System.out.println("Enter MemberID to pay bills:");
                int memberID = Integer.parseInt(scanner.nextLine());


                BigDecimal bankAccountBalance = getBankAccountBalance(memberID);
                if (bankAccountBalance == null) {
                    System.out.println("No bank account found for MemberID: " + memberID);
                    break;
                }

               
                String queryOutstandingBills = "SELECT BillID, Amount FROM Billing WHERE MemberID = ? AND PaymentStatus = 'Outstanding'";
                try (PreparedStatement stmtOutstanding = connection.prepareStatement(queryOutstandingBills)) {
                    stmtOutstanding.setInt(1, memberID);

                    ResultSet rsOutstanding = stmtOutstanding.executeQuery();
                    while (rsOutstanding.next()) {
                        BigDecimal billAmount = rsOutstanding.getBigDecimal("Amount");
                        if (bankAccountBalance.compareTo(billAmount) >= 0) {
                            bankAccountBalance = bankAccountBalance.subtract(billAmount);
                            // Update bill status to 'Paid'
                            updateBillStatus(rsOutstanding.getInt("BillID"), "Paid");
                            // Update the member's bank account
                            updateBankAccountBalance(memberID, bankAccountBalance);
                            System.out.println("Bill of $" + billAmount +" has been paid.");
                        } else {
                            System.out.println("Insufficient funds to pay BillID: " + rsOutstanding.getInt("BillID") + ". Remaining bills are unpaid.");
                            break;
                        }
                    }
                    break;
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid MemberID.");
                break;
            }
        }
        Admin_BillOrPay();
    }



    private static BigDecimal getBankAccountBalance(int memberID) throws SQLException {
        String query = "SELECT Amount FROM BankAccount WHERE MemberID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, memberID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("Amount");
            }
        }
        return null;
    }

    private static void updateBillStatus(int billID, String status) throws SQLException {
        String update = "UPDATE Billing SET PaymentStatus = ? WHERE BillID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setString(1, status);
            stmt.setInt(2, billID);
            stmt.executeUpdate();
        }
    }

    private static void updateBankAccountBalance(int memberID, BigDecimal newBalance) throws SQLException {
        String update = "UPDATE BankAccount SET Amount = ? WHERE MemberID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, memberID);
            stmt.executeUpdate();
        }
    }


}

