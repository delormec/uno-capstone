-- Set up database with default values
USE [OST];

INSERT INTO Roles VALUES ("Administrator");
INSERT INTO Roles VALUES ("User");

INSERT INTO Users (UserName, Deleted, Password, Role_RoleId) VALUES ("admin", 0, "", 1);