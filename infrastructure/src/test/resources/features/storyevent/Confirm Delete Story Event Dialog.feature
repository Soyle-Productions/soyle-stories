Feature: Delete Story Event Dialog

  Background:
    Given A project has been opened
    And A Story Event has been created

  Scenario: Confirm Delete Dialog
    Given the delete Story Event dialog has been opened
    When the user clicks the confirm delete Story Event dialog delete button
    Then the delete Story Event dialog should be closed

  Scenario: Cancel Delete Dialog
    Given the delete Story Event dialog has been opened
    When the user clicks the confirm delete Story Event dialog cancel button
    Then the delete Story Event dialog should be closed