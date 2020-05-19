@storyevent
Feature: Create Story Event Dialog

  Background:
    Given A project has been opened
    And the Create Story Event Dialog has been opened

	@create-new-story-event
  Scenario: Invalid name with Enter key
    Given the Create Story Event Dialog Name input has an invalid Story Event Name
    When The user presses the Enter key
    Then an error message should be displayed in the Create Story Event Dialog

	@create-new-story-event
  Scenario: Enter key creates a Story Event
    Given the Create Story Event Dialog Name input has a valid Story Event Name
    When The user presses the Enter key
    Then the Create Story Event Dialog should be closed
    And a new Story Event should be created

	@create-new-story-event
  Scenario: Esc key cancels Story Event Creation
    When The user presses the Esc key
    Then the Create Story Event Dialog should be closed
