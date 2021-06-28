@location
Feature: Create New Location Dialog
  As a user working on a project
  I want to open the create new location dialog
  So that I can create a new location and receive feedback if I do something wrong

  Background:
    Given A project has been opened

  Scenario: Open dialog through menu
    When User selects the file->new->location menu option
    Then The create new location dialog should be open

  Scenario: Open dialog through empty Location List Tool
    Given The Location List Tool has been opened
    When User clicks the center create new location button
    Then The create new location dialog should be open

  Scenario: Open dialog through populated Location List Tool
    Given A location has been created
    And The Location List Tool has been opened
    When User clicks the bottom create new location button
    Then The create new location dialog should be open

  Scenario: Pressing Enter with an invalid name
    Given The create new location dialog has been opened
    And The user has entered an invalid location name
    When The user presses the Enter key
    Then An error message should be visible in the create new location dialog

  Scenario: Clicking Create button with an invalid name
    Given The create new location dialog has been opened
    And The user has entered an invalid location name
    When The user clicks the Create button
    Then An error message should be visible in the create new location dialog

  Scenario: Pressing the Esc key to close the dialog
    Given The create new location dialog has been opened
    When The user presses the Esc key
    Then The create new location dialog should be closed

  Scenario: Clicking the Cancel button to close the dialog
    Given The create new location dialog has been opened
    When The user clicks the Cancel button
    Then The create new location dialog should be closed

  Scenario: Pressing Enter with a valid name
    Given The create new location dialog has been opened
    And The user has entered a valid location name
    When The user presses the Enter key
    Then The create new location dialog should be closed

  Scenario: Clicking Create button with a valid name
    Given The create new location dialog has been opened
    And The user has entered a valid location name
    When The user clicks the Create button
    Then The create new location dialog should be closed

  Scenario: Creating multiple locations
    Given The create new location dialog has been opened
    And The user has entered a valid location name
    When The user clicks the Create button
    And The create new location dialog is reopened
    Then the location name field should be blank
