Uzonna Alexander
101233844
COMP 3005 Project


My project was done in Java with IntelliJ, using the same setup and functions that were shown in class and used in a previous assignment. My diagrams were done in Draw.io and the images for each diagram as well as the .drawio files will be included on GitHub.


My ER Model is relatively straightforward, but I’ll go over it briefly.

First, you have the Members class which stores basic information about each member. Billing, Bank Accounts, Health Metric, Member Goals, Training Sessions and Class Registrations are all linked to a member ID. 

Training Sessions are also linked to the Trainer class and the code ensures there are no time conflicts. Group Classes are linked to Trainers and Rooms, then we use Class Registrations to know which members are in the class. The Equipment class also exists, but isn’t linked to anything else in the database.


YouTube Link: https://youtu.be/U7RyJ1jICm0
