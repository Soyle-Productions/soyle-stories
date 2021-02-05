Feature: Create Character Dialog

  Background:
    Given A project has been opened
    And the Create Character Dialog has been opened

  Scenario: Invalid name with Enter key
    Given the Create Character Dialog Name input has an invalid Character Name
    When The user presses the Enter key
    Then an error message should be displayed in the Create Character Dialog

  Scenario: Enter key creates a Character
    Given the Create Character Dialog Name input has a valid Character Name
    When The user presses the Enter key
    Then the Create Character Dialog should be closed
    And a new Character should be created

  Scenario: Esc key cancels Character Creation
    When The user presses the Esc key
    Then the Create Character Dialog should be closed
