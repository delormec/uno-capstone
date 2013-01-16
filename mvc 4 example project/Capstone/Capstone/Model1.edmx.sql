
-- --------------------------------------------------
-- Entity Designer DDL Script for SQL Server 2005, 2008, and Azure
-- --------------------------------------------------
-- Date Created: 01/15/2013 17:43:42
-- Generated from EDMX file: c:\users\delormec\documents\visual studio 2010\Projects\Capstone\Capstone\Model1.edmx
-- --------------------------------------------------

SET QUOTED_IDENTIFIER OFF;
GO
USE [capstone];
GO
IF SCHEMA_ID(N'dbo') IS NULL EXECUTE(N'CREATE SCHEMA [dbo]');
GO

-- --------------------------------------------------
-- Dropping existing FOREIGN KEY constraints
-- --------------------------------------------------


-- --------------------------------------------------
-- Dropping existing tables
-- --------------------------------------------------


-- --------------------------------------------------
-- Creating all tables
-- --------------------------------------------------

-- Creating table 'Forms'
CREATE TABLE [dbo].[Forms] (
    [FormId] int IDENTITY(1,1) NOT NULL,
    [AutoUpdate] bit  NULL,
    [Deleted] bit  NOT NULL,
    [Content] nvarchar(max)  NULL,
    [Name] nvarchar(max)  NULL,
    [Description] nvarchar(max)  NULL,
    [DateCreated] datetime  NULL
);
GO

-- --------------------------------------------------
-- Creating all PRIMARY KEY constraints
-- --------------------------------------------------

-- Creating primary key on [FormId] in table 'Forms'
ALTER TABLE [dbo].[Forms]
ADD CONSTRAINT [PK_Forms]
    PRIMARY KEY CLUSTERED ([FormId] ASC);
GO

-- --------------------------------------------------
-- Creating all FOREIGN KEY constraints
-- --------------------------------------------------

-- --------------------------------------------------
-- Script has ended
-- --------------------------------------------------