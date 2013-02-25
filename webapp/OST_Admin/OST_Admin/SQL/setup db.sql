-- Set up database with default values
USE [OST]

INSERT INTO Roles (Name) VALUES ('Administrator');
INSERT INTO Roles (Name) VALUES ('User');

INSERT INTO Users (UserName, Active, [Password], Role_RoleId) VALUES ('admin', 0, '', 1);