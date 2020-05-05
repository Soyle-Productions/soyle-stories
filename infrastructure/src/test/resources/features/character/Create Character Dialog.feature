Feature: Create Character Dialog

  Background:
    Given a Project has been opened
    And the Create Character Dialog has been opened

  Scenario: Invalid name with Enter key
    Given the Create Character Dialog Name input has an invalid Character Name
    When the Enter key is pressed
    Then an error message should be displayed in the Create Character Dialog

  Scenario: Enter key creates a Character
    Given the Create Character Dialog Name input has an valid Character Name
    When the Enter key is pressed
    Then the Create Character Dialog should be closed
    And a new Character should be created

  Scenario: Esc key cancels Character Creation
    When the Esc key is pressed
    Then the Create Character Dialog should be closed
