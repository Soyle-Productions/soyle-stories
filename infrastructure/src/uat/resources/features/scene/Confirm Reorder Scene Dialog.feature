@scene @reorder-scene
Feature: Confirm Reorder Scene Dialog

  Background:
    Given 2 Scenes have been created
    And the Confirm Reorder Scene Dialog has been opened

  Scenario Outline: Cancel the change
    When the Confirm Reorder Scene Dialog <button> button is selected
    Then the Confirm Reorder Scene Dialog should be closed
    And the Scene should not be reordered

    Examples:
      | button   |
      | "Cancel" |
      | "close"  |

  Scenario Outline: Toggle show dialog
    When the Confirm Reorder Scene Dialog do not show again check-box is checked
    And the Confirm Reorder Scene Dialog <button> button is selected
    Then the Confirm Reorder Scene Dialog should not open the next time a Scene is reordered

    Examples:
      | button    |
      | "Reorder" |
      | "Show Ramifications" |

  Scenario: Reorder without showing ramifications
    When the Confirm Reorder Scene Dialog "Reorder" button is selected
    Then the Scene should be reordered

  Scenario: Show ramifications
    When the Confirm Reorder Scene Dialog "Show Ramifications" button is selected
    Then the Scene should not be reordered
    And the Confirm Reorder Scene Dialog should be closed
    And the Reorder Scene Ramifications Tool should be open