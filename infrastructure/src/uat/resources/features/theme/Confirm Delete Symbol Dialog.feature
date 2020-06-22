@theme
Feature: Confirm Delete Symbol Dialog

  Background:
    Given A project has been opened
    And a symbol has been created

  @delete-symbol @set-dialog-preferences @new
  Scenario: Delete Symbol without Checking Do Not Show Box
    Given the Confirm Delete Symbol Dialog has been opened
    When the Confirm Delete Symbol Dialog "Delete" button is selected
    Then the Confirm Delete Symbol Dialog should be closed
    And the symbol should be deleted

  @delete-symbol @set-dialog-preferences @new
  Scenario: Delete Symbol after Checking Do Not Show Box
    Given the Confirm Delete Symbol Dialog has been opened
    And the Confirm Delete Symbol Dialog do not show again check-box has been checked
    When the Confirm Delete Symbol Dialog "Delete" button is selected
    Then the Confirm Delete Symbol Dialog should be closed
    And the symbol should be deleted
    Then the Confirm Delete Symbol Dialog should not open the next time a symbol is deleted

  @delete-symbol @new
  Scenario: Cancel Symbol Deletion
    Given the Confirm Delete Symbol Dialog has been opened
    When the Confirm Delete Symbol Dialog "Cancel" button is selected
    Then the Confirm Delete Symbol Dialog should be closed
    But the symbol should not be deleted

  @delete-symbol @get-dialog-preferences @new
  Scenario: Delete Symbol after Do Not Show checked
    Given the Confirm Delete Symbol Dialog has been requested to not be shown
    When the Confirm Delete Symbol Dialog is opened
    Then the Confirm Delete Symbol Dialog should not be open
    And the symbol should be deleted