CREATE TABLE Members (
    MemberID SERIAL PRIMARY KEY,
    FirstName VARCHAR(255),
    LastName VARCHAR(255),
    Email VARCHAR(255) UNIQUE,
    Password VARCHAR(255),
    DateOfBirth DATE
);

CREATE TABLE MemberGoals (
    GoalID SERIAL PRIMARY KEY,
    MemberID INT REFERENCES Members(MemberID),
    GoalType VARCHAR(255),
    TargetValue VARCHAR(255),
    StartDate DATE,
    EndDate DATE
);

CREATE TABLE HealthMetrics (
    MetricID SERIAL PRIMARY KEY,
    MemberID INT REFERENCES Members(MemberID),
    MetricType VARCHAR(255),
    Value DECIMAL(10, 2),
    RecordDate DATE
);

CREATE TABLE Trainers (
    TrainerID SERIAL PRIMARY KEY,
    FirstName VARCHAR(255),
    LastName VARCHAR(255),
    Specialization VARCHAR(255),
    StartTime TIMESTAMP,
    EndTime TIMESTAMP
);

CREATE TABLE TrainingSessions (
    SessionID SERIAL PRIMARY KEY,
    MemberID INT REFERENCES Members(MemberID),
    TrainerID INT REFERENCES Trainers(TrainerID),
    StartTime TIMESTAMP,
    EndTime TIMESTAMP
    
);

CREATE TABLE Rooms (
    RoomID SERIAL PRIMARY KEY,
    RoomName VARCHAR(255),
    Capacity INT
);


CREATE TABLE GroupClasses (
    ClassID SERIAL PRIMARY KEY,
    ClassName VARCHAR(255),
    TrainerID INT REFERENCES Trainers(TrainerID),
    StartTime TIMESTAMP,
    EndTime TIMESTAMP,
    RoomID INT REFERENCES Rooms(RoomID)
);

CREATE TABLE ClassRegistrations (
    RegistrationID SERIAL PRIMARY KEY,
    ClassID INT REFERENCES GroupClasses(ClassID),
    MemberID INT REFERENCES Members(MemberID)
);


CREATE TABLE Equipment (
    EquipmentID SERIAL PRIMARY KEY,
    EquipmentName VARCHAR(255),
    Status VARCHAR(255)
);

CREATE TABLE Billing (
    BillID SERIAL PRIMARY KEY,
    MemberID INT REFERENCES Members(MemberID),
    Amount DECIMAL(10, 2),
    PaymentStatus VARCHAR(255) 
);

CREATE TABLE BankAccount (
    BankAccountID SERIAL PRIMARY KEY,
    MemberID INT REFERENCES Members(MemberID),
    Amount DECIMAL(10, 2)
);
