Feature: Start New Project
  The storyteller can start a new project from the welcome screen or any project window

  Background:
    Given The application has started
    And User has selected a directory
    And The selected directory exists
    And User has entered a name into the project name field
    And A file with the entered name does not exist in selected directory

  Scenario: Start new project from welcome screen
    Given Welcome screen has been opened
    When User starts a new project
    Then Created project should be open
      But Welcome screen should not be open

  Scenario: Start new project while project is open
    Given One project has been opened
    When User starts a new project
    Then Open project option dialog should be open
      But Created project should not be open