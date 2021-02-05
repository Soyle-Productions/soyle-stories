@excluded
Feature: Confirm Delete Story Event Dialog

  Background:
    Given A project has been opened
    And A Story Event has been created

  Scenario: Confirm Delete Story Event
    Given the Confirm Delete Story Event Dialog has been opened
    When the user clicks the Confirm Delete Story Event Dialog delete button
    Then the Confirm Delete Story Event Dialog should be closed

  Scenario: Cancel Delete Story Event
    Given the Confirm Delete Story Event Dialog has been opened
    When the user clicks the Confirm Delete Story Event Dialog cancel button
    Then the Confirm Delete Story Event Dialog should be closed