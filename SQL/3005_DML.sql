-- May not need this first line, but I did
ALTER SEQUENCE members_memberid_seq RESTART WITH 1;

INSERT INTO Members (FirstName, LastName, Email, Password, DateOfBirth) VALUES
('John', 'Doe', 'johndoe@example.com', 'password123', '1990-01-01'),
('Alice', 'Alexander', 'alice_alex@gmail.com', 'pass123', '1991-02-01'),
('Richard', 'Conly', 'thelegend27@outlook.com', 'thegreat', '1992-03-01');


INSERT INTO MemberGoals (MemberID, GoalType, TargetValue, StartDate, EndDate) VALUES
(1, 'Weight Loss', '20kg', '2024-01-01', '2024-06-01'),
(2, 'Muscle Gain', '5kg', '2024-02-01', '2024-07-01'),
(3, 'Running Distance', '100km', '2024-03-01', '2024-08-01');


INSERT INTO HealthMetrics (MemberID, MetricType, Value, RecordDate) VALUES
(1, 'Weight', 80.00, '2024-04-01'),
(2, 'Muscle Mass', 65.00, '2024-04-01'),
(3, 'Running Distance', 10.00, '2024-04-01');


INSERT INTO Trainers (FirstName, LastName, Specialization, StartTime, EndTime) VALUES
('Tony', 'Stark', 'Weight Training', '2024-01-01 08:00:00', '2024-01-01 16:00:00'),
('Steve', 'Rogers', 'Cardio Training', '2024-01-01 09:00:00', '2024-01-01 17:00:00');


INSERT INTO TrainingSessions (MemberID, TrainerID, StartTime, EndTime) VALUES
(1, 1, '2024-04-01 09:00:00', '2024-04-01 10:00:00'),
(2, 2, '2024-04-02 10:00:00', '2024-04-02 11:00:00'),
(3, 1, '2024-04-03 11:00:00', '2024-04-03 12:00:00');


INSERT INTO Rooms (RoomName, Capacity) VALUES
('Yoga Studio', 20),
('Weight Room', 15);


INSERT INTO GroupClasses (ClassName, TrainerID, StartTime, EndTime, RoomID) VALUES
('Yoga Class', 2, '2024-04-01 09:00:00', '2024-04-01 10:00:00', 1),
('Strength Training', 1, '2024-04-02 10:00:00', '2024-04-02 11:00:00', 2);


INSERT INTO ClassRegistrations (ClassID, MemberID) VALUES
(1, 1),
(2, 2),
(1, 3);


INSERT INTO Equipment (EquipmentName, Status) VALUES
('Treadmill', 'Available'),
('Dumbbell Set', 'In Use');


INSERT INTO Billing (MemberID, Amount, PaymentStatus) VALUES
(1, 50.00, 'Paid'),
(2, 75.00, 'Due'),
(3, 20.00, 'Paid');


INSERT INTO BankAccount (MemberID, Amount) VALUES
(1, 1000.00),
(2, 1500.00),
(3, 2000.00);
