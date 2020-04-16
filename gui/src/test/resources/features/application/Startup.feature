@application
Feature: Startup
  Application displays different screens based on previously opened projects

  Scenario: As a new user, I want to know what options are available to me when I start the application
    Given Application has never been run before
    When Application is started
    Then Welcome screen should be open

  Scenario: As a returning user with no previously opened project, I want to get started quickly
    Given Workspace has no open projects
    When Application is started
    Then Welcome screen should be open

  Scenario: As a returning user, I want to resume working where I left off
    Given Workspace has one open project
      And The workspace project file exists
    When Application is started
    Then The workspace project should be open
      But Welcome screen should not be open

  Scenario: As a returning user, I want to know if a project I was working on has been moved or deleted
    Given Workspace has one open project
      And The workspace project file doesn't exist
    When Application is started
    Then Project failure dialog should be open
      And Failed project location should be listed
      And Failed project name should be listed
      But Welcome screen should not be open