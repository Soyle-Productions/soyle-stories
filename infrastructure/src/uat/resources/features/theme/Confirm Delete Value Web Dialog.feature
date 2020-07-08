Feature: Confirm Delete Value Web Dialog

  Background:
    Given A project has been opened
    And a value web has been created

  @delete-value-web @set-dialog-preferences
  Scenario: Delete Value Web without Checking Do Not Show Box
    Given the Confirm Delete Value Web Dialog has been opened
    When the Confirm Delete Value Web Dialog "Delete" button is selected
    Then the Confirm Delete Value Web Dialog should be closed
    And the value web should be deleted

  @delete-value-web @set-dialog-preferences
  Scenario: Delete Value Web after Checking Do Not Show Box
    Given the Confirm Delete Value Web Dialog has been opened
    And the Confirm Delete Value Web Dialog do not show again check-box has been checked
    When the Confirm Delete Value Web Dialog "Delete" button is selected
    Then the Confirm Delete Value Web Dialog should be closed
    And the value web should be deleted
    Then the Confirm Delete Value Web Dialog should not open the next time a value web is deleted

  @delete-value-web
  Scenario: Cancel Value Web Deletion
    Given the Confirm Delete Value Web Dialog has been opened
    When the Confirm Delete Value Web Dialog "Cancel" button is selected
    Then the Confirm Delete Value Web Dialog should be closed
    But the value web should not be deleted

  @delete-value-web @get-dialog-preferences
  Scenario: Delete Value Web after Do Not Show checked
    Given the Confirm Delete Value Web Dialog has been requested to not be shown
    When the Confirm Delete Value Web Dialog is opened
    Then the Confirm Delete Value Web Dialog should not be open
    And the value web should be deleted
    