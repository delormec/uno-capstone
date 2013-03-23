
-- --------------------------------------------------
-- Entity Designer DDL Script for SQL Server 2005, 2008, and Azure
-- --------------------------------------------------
-- Date Created: 03/23/2013 13:42:50
-- Generated from EDMX file: C:\Users\unouser\Documents\GitHub\capstone\webapp\OST_Admin\HOST_Admin\Models\HOST.edmx
-- --------------------------------------------------

SET QUOTED_IDENTIFIER OFF;
GO
USE [OST];
GO
IF SCHEMA_ID(N'dbo') IS NULL EXECUTE(N'CREATE SCHEMA [dbo]');
GO

-- --------------------------------------------------
-- Dropping existing FOREIGN KEY constraints
-- --------------------------------------------------

IF OBJECT_ID(N'[dbo].[FK_FormQuestion]', 'F') IS NOT NULL
    ALTER TABLE [dbo].[Questions] DROP CONSTRAINT [FK_FormQuestion];
GO
IF OBJECT_ID(N'[dbo].[FK_LikertScaleQuestionLabel]', 'F') IS NOT NULL
    ALTER TABLE [dbo].[Labels] DROP CONSTRAINT [FK_LikertScaleQuestionLabel];
GO
IF OBJECT_ID(N'[dbo].[FK_ChoiceQuestionOption]', 'F') IS NOT NULL
    ALTER TABLE [dbo].[Options] DROP CONSTRAINT [FK_ChoiceQuestionOption];
GO
IF OBJECT_ID(N'[dbo].[FK_RoleUser]', 'F') IS NOT NULL
    ALTER TABLE [dbo].[Users] DROP CONSTRAINT [FK_RoleUser];
GO
IF OBJECT_ID(N'[dbo].[FK_QuestionHelpImage]', 'F') IS NOT NULL
    ALTER TABLE [dbo].[HelpImages] DROP CONSTRAINT [FK_QuestionHelpImage];
GO
IF OBJECT_ID(N'[dbo].[FK_LikertScaleQuestion_inherits_Question]', 'F') IS NOT NULL
    ALTER TABLE [dbo].[Questions_LikertScaleQuestion] DROP CONSTRAINT [FK_LikertScaleQuestion_inherits_Question];
GO
IF OBJECT_ID(N'[dbo].[FK_ChoiceQuestion_inherits_Question]', 'F') IS NOT NULL
    ALTER TABLE [dbo].[Questions_ChoiceQuestion] DROP CONSTRAINT [FK_ChoiceQuestion_inherits_Question];
GO
IF OBJECT_ID(N'[dbo].[FK_TextQuestion_inherits_Question]', 'F') IS NOT NULL
    ALTER TABLE [dbo].[Questions_TextQuestion] DROP CONSTRAINT [FK_TextQuestion_inherits_Question];
GO

-- --------------------------------------------------
-- Dropping existing tables
-- --------------------------------------------------

IF OBJECT_ID(N'[dbo].[Forms]', 'U') IS NOT NULL
    DROP TABLE [dbo].[Forms];
GO
IF OBJECT_ID(N'[dbo].[Users]', 'U') IS NOT NULL
    DROP TABLE [dbo].[Users];
GO
IF OBJECT_ID(N'[dbo].[Questions]', 'U') IS NOT NULL
    DROP TABLE [dbo].[Questions];
GO
IF OBJECT_ID(N'[dbo].[Options]', 'U') IS NOT NULL
    DROP TABLE [dbo].[Options];
GO
IF OBJECT_ID(N'[dbo].[Labels]', 'U') IS NOT NULL
    DROP TABLE [dbo].[Labels];
GO
IF OBJECT_ID(N'[dbo].[Roles]', 'U') IS NOT NULL
    DROP TABLE [dbo].[Roles];
GO
IF OBJECT_ID(N'[dbo].[Configurations]', 'U') IS NOT NULL
    DROP TABLE [dbo].[Configurations];
GO
IF OBJECT_ID(N'[dbo].[HelpImages]', 'U') IS NOT NULL
    DROP TABLE [dbo].[HelpImages];
GO
IF OBJECT_ID(N'[dbo].[Questions_LikertScaleQuestion]', 'U') IS NOT NULL
    DROP TABLE [dbo].[Questions_LikertScaleQuestion];
GO
IF OBJECT_ID(N'[dbo].[Questions_ChoiceQuestion]', 'U') IS NOT NULL
    DROP TABLE [dbo].[Questions_ChoiceQuestion];
GO
IF OBJECT_ID(N'[dbo].[Questions_TextQuestion]', 'U') IS NOT NULL
    DROP TABLE [dbo].[Questions_TextQuestion];
GO

-- --------------------------------------------------
-- Creating all tables
-- --------------------------------------------------

-- Creating table 'Forms'
CREATE TABLE [dbo].[Forms] (
    [FormId] int IDENTITY(1,1) NOT NULL,
    [AutoUpdate] bit  NULL,
    [Active] bit  NOT NULL,
    [ListName] nvarchar(max)  NULL,
    [Name] nvarchar(max)  NULL,
    [Description] nvarchar(max)  NULL,
    [DateCreated] datetime  NULL,
    [URL] nvarchar(max)  NULL,
    [KeyField] int  NULL,
    [Group] nvarchar(max)  NULL,
    [CreatedBy] int  NULL,
    [FilledDateFieldName] nvarchar(max)  NULL,
    [FilledDateFieldType] nvarchar(max)  NULL,
    [FilledByFieldName] nvarchar(max)  NULL,
    [FilledByFieldType] nvarchar(max)  NULL
);
GO

-- Creating table 'Users'
CREATE TABLE [dbo].[Users] (
    [UserId] int IDENTITY(1,1) NOT NULL,
    [UserName] nvarchar(max)  NOT NULL,
    [Active] bit  NOT NULL,
    [Password] nvarchar(max)  NOT NULL,
    [Role_RoleId] int  NOT NULL
);
GO

-- Creating table 'Questions'
CREATE TABLE [dbo].[Questions] (
    [QuestionId] int IDENTITY(1,1) NOT NULL,
    [Text] nvarchar(max)  NULL,
    [HelpText] nvarchar(max)  NULL,
    [FieldName] nvarchar(max)  NULL,
    [FieldType] nvarchar(max)  NULL,
    [SortOrder] int  NOT NULL,
    [Form_FormId] int  NOT NULL
);
GO

-- Creating table 'Options'
CREATE TABLE [dbo].[Options] (
    [OptionId] int IDENTITY(1,1) NOT NULL,
    [Text] nvarchar(max)  NULL,
    [SortOrder] int  NOT NULL,
    [ChoiceQuestion_QuestionId] int  NOT NULL
);
GO

-- Creating table 'Labels'
CREATE TABLE [dbo].[Labels] (
    [LabelId] int IDENTITY(1,1) NOT NULL,
    [Text] nvarchar(max)  NULL,
    [Range] nvarchar(max)  NULL,
    [LikertScaleQuestion_QuestionId] int  NOT NULL
);
GO

-- Creating table 'Roles'
CREATE TABLE [dbo].[Roles] (
    [RoleId] int IDENTITY(1,1) NOT NULL,
    [Name] nvarchar(max)  NOT NULL
);
GO

-- Creating table 'Configurations'
CREATE TABLE [dbo].[Configurations] (
    [SharePointUserName] nvarchar(max)  NULL,
    [SharePointPassword] nvarchar(max)  NULL,
    [SharePointURL] nvarchar(max)  NULL,
    [ConfigurationId] int IDENTITY(1,1) NOT NULL
);
GO

-- Creating table 'HelpImages'
CREATE TABLE [dbo].[HelpImages] (
    [ImageId] int IDENTITY(1,1) NOT NULL,
    [Name] nvarchar(max)  NULL,
    [Data] varbinary(max)  NULL,
    [Question_QuestionId] int  NOT NULL
);
GO

-- Creating table 'Questions_LikertScaleQuestion'
CREATE TABLE [dbo].[Questions_LikertScaleQuestion] (
    [Steps] int  NOT NULL,
    [QuestionId] int  NOT NULL
);
GO

-- Creating table 'Questions_ChoiceQuestion'
CREATE TABLE [dbo].[Questions_ChoiceQuestion] (
    [Other] bit  NULL,
    [Multiple] bit  NULL,
    [QuestionId] int  NOT NULL
);
GO

-- Creating table 'Questions_TextQuestion'
CREATE TABLE [dbo].[Questions_TextQuestion] (
    [QuestionId] int  NOT NULL
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

-- Creating primary key on [UserId] in table 'Users'
ALTER TABLE [dbo].[Users]
ADD CONSTRAINT [PK_Users]
    PRIMARY KEY CLUSTERED ([UserId] ASC);
GO

-- Creating primary key on [QuestionId] in table 'Questions'
ALTER TABLE [dbo].[Questions]
ADD CONSTRAINT [PK_Questions]
    PRIMARY KEY CLUSTERED ([QuestionId] ASC);
GO

-- Creating primary key on [OptionId] in table 'Options'
ALTER TABLE [dbo].[Options]
ADD CONSTRAINT [PK_Options]
    PRIMARY KEY CLUSTERED ([OptionId] ASC);
GO

-- Creating primary key on [LabelId] in table 'Labels'
ALTER TABLE [dbo].[Labels]
ADD CONSTRAINT [PK_Labels]
    PRIMARY KEY CLUSTERED ([LabelId] ASC);
GO

-- Creating primary key on [RoleId] in table 'Roles'
ALTER TABLE [dbo].[Roles]
ADD CONSTRAINT [PK_Roles]
    PRIMARY KEY CLUSTERED ([RoleId] ASC);
GO

-- Creating primary key on [ConfigurationId] in table 'Configurations'
ALTER TABLE [dbo].[Configurations]
ADD CONSTRAINT [PK_Configurations]
    PRIMARY KEY CLUSTERED ([ConfigurationId] ASC);
GO

-- Creating primary key on [ImageId] in table 'HelpImages'
ALTER TABLE [dbo].[HelpImages]
ADD CONSTRAINT [PK_HelpImages]
    PRIMARY KEY CLUSTERED ([ImageId] ASC);
GO

-- Creating primary key on [QuestionId] in table 'Questions_LikertScaleQuestion'
ALTER TABLE [dbo].[Questions_LikertScaleQuestion]
ADD CONSTRAINT [PK_Questions_LikertScaleQuestion]
    PRIMARY KEY CLUSTERED ([QuestionId] ASC);
GO

-- Creating primary key on [QuestionId] in table 'Questions_ChoiceQuestion'
ALTER TABLE [dbo].[Questions_ChoiceQuestion]
ADD CONSTRAINT [PK_Questions_ChoiceQuestion]
    PRIMARY KEY CLUSTERED ([QuestionId] ASC);
GO

-- Creating primary key on [QuestionId] in table 'Questions_TextQuestion'
ALTER TABLE [dbo].[Questions_TextQuestion]
ADD CONSTRAINT [PK_Questions_TextQuestion]
    PRIMARY KEY CLUSTERED ([QuestionId] ASC);
GO

-- --------------------------------------------------
-- Creating all FOREIGN KEY constraints
-- --------------------------------------------------

-- Creating foreign key on [Form_FormId] in table 'Questions'
ALTER TABLE [dbo].[Questions]
ADD CONSTRAINT [FK_FormQuestion]
    FOREIGN KEY ([Form_FormId])
    REFERENCES [dbo].[Forms]
        ([FormId])
    ON DELETE NO ACTION ON UPDATE NO ACTION;

-- Creating non-clustered index for FOREIGN KEY 'FK_FormQuestion'
CREATE INDEX [IX_FK_FormQuestion]
ON [dbo].[Questions]
    ([Form_FormId]);
GO

-- Creating foreign key on [LikertScaleQuestion_QuestionId] in table 'Labels'
ALTER TABLE [dbo].[Labels]
ADD CONSTRAINT [FK_LikertScaleQuestionLabel]
    FOREIGN KEY ([LikertScaleQuestion_QuestionId])
    REFERENCES [dbo].[Questions_LikertScaleQuestion]
        ([QuestionId])
    ON DELETE CASCADE ON UPDATE NO ACTION;

-- Creating non-clustered index for FOREIGN KEY 'FK_LikertScaleQuestionLabel'
CREATE INDEX [IX_FK_LikertScaleQuestionLabel]
ON [dbo].[Labels]
    ([LikertScaleQuestion_QuestionId]);
GO

-- Creating foreign key on [ChoiceQuestion_QuestionId] in table 'Options'
ALTER TABLE [dbo].[Options]
ADD CONSTRAINT [FK_ChoiceQuestionOption]
    FOREIGN KEY ([ChoiceQuestion_QuestionId])
    REFERENCES [dbo].[Questions_ChoiceQuestion]
        ([QuestionId])
    ON DELETE CASCADE ON UPDATE NO ACTION;

-- Creating non-clustered index for FOREIGN KEY 'FK_ChoiceQuestionOption'
CREATE INDEX [IX_FK_ChoiceQuestionOption]
ON [dbo].[Options]
    ([ChoiceQuestion_QuestionId]);
GO

-- Creating foreign key on [Role_RoleId] in table 'Users'
ALTER TABLE [dbo].[Users]
ADD CONSTRAINT [FK_RoleUser]
    FOREIGN KEY ([Role_RoleId])
    REFERENCES [dbo].[Roles]
        ([RoleId])
    ON DELETE NO ACTION ON UPDATE NO ACTION;

-- Creating non-clustered index for FOREIGN KEY 'FK_RoleUser'
CREATE INDEX [IX_FK_RoleUser]
ON [dbo].[Users]
    ([Role_RoleId]);
GO

-- Creating foreign key on [Question_QuestionId] in table 'HelpImages'
ALTER TABLE [dbo].[HelpImages]
ADD CONSTRAINT [FK_QuestionHelpImage]
    FOREIGN KEY ([Question_QuestionId])
    REFERENCES [dbo].[Questions]
        ([QuestionId])
    ON DELETE NO ACTION ON UPDATE NO ACTION;

-- Creating non-clustered index for FOREIGN KEY 'FK_QuestionHelpImage'
CREATE INDEX [IX_FK_QuestionHelpImage]
ON [dbo].[HelpImages]
    ([Question_QuestionId]);
GO

-- Creating foreign key on [QuestionId] in table 'Questions_LikertScaleQuestion'
ALTER TABLE [dbo].[Questions_LikertScaleQuestion]
ADD CONSTRAINT [FK_LikertScaleQuestion_inherits_Question]
    FOREIGN KEY ([QuestionId])
    REFERENCES [dbo].[Questions]
        ([QuestionId])
    ON DELETE NO ACTION ON UPDATE NO ACTION;
GO

-- Creating foreign key on [QuestionId] in table 'Questions_ChoiceQuestion'
ALTER TABLE [dbo].[Questions_ChoiceQuestion]
ADD CONSTRAINT [FK_ChoiceQuestion_inherits_Question]
    FOREIGN KEY ([QuestionId])
    REFERENCES [dbo].[Questions]
        ([QuestionId])
    ON DELETE NO ACTION ON UPDATE NO ACTION;
GO

-- Creating foreign key on [QuestionId] in table 'Questions_TextQuestion'
ALTER TABLE [dbo].[Questions_TextQuestion]
ADD CONSTRAINT [FK_TextQuestion_inherits_Question]
    FOREIGN KEY ([QuestionId])
    REFERENCES [dbo].[Questions]
        ([QuestionId])
    ON DELETE NO ACTION ON UPDATE NO ACTION;
GO

-- --------------------------------------------------
-- Script has ended
-- --------------------------------------------------