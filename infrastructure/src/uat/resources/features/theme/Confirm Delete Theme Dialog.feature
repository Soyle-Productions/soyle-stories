@theme
Feature: Confirm Delete Theme Dialog

  Background:
    Given A project has been opened
    And a Theme has been created

  @delete-theme @set-dialog-preferences
  Scenario: Delete Theme without Checking Do Not Show Box
    Given the Confirm Delete Theme Dialog has been opened
    When the Confirm Delete Theme Dialog "Delete" button is selected
    Then the Confirm Delete Theme Dialog should be closed
    And the Theme should be deleted

  @delete-theme @set-dialog-preferences
  Scenario: Delete Theme after Checking Do Not Show Box
    Given the Confirm Delete Theme Dialog has been opened
    And the Confirm Delete Theme Dialog do not show again check-box has been checked
    When the Confirm Delete Theme Dialog "Delete" button is selected
    Then the Confirm Delete Theme Dialog should be closed
    And the Theme should be deleted
    Then the Confirm Delete Theme Dialog should not open the next time a Theme is deleted

  @delete-theme
  Scenario: Cancel Theme Deletion
    Given the Confirm Delete Theme Dialog has been opened
    When the Confirm Delete Theme Dialog "Cancel" button is selected
    Then the Confirm Delete Theme Dialog should be closed
    But the Theme should not be deleted

  @delete-theme @get-dialog-preferences
  Scenario: Delete Theme after Do Not Show checked
    Given the Confirm Delete Theme Dialog has been requested to not be shown
    When the Confirm Delete Theme Dialog is opened
    Then the Confirm Delete Theme Dialog should not be open
    And the Theme should be deleted