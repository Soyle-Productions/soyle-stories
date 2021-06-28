Feature: Create Scene Dialog

  Background:
    Given A project has been opened
    And the Create Scene Dialog has been opened

  Scenario: Invalid name with Enter key
    Given the Create Scene Dialog Name input has an invalid Scene Name
    When The user presses the Enter key
    Then an error message should be displayed in the Create Scene Dialog

  Scenario: Enter key creates a Scene
    Given the Create Scene Dialog Name input has a valid Scene Name
    When The user presses the Enter key
    Then the Create Scene Dialog should be closed
    And a new Scene should be created

  Scenario: Esc key cancels Scene Creation
    When The user presses the Esc key
    Then the Create Scene Dialog should be closed
