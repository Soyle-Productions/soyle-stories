Feature: Delete Story Event Dialog

  Background:
    Given A project has been opened
    And A Story Event has been created

  Scenario: Populated Story Event List
    Given The Story Event List Tool has been opened
    And the Story Event right-click menu is open
    When the user clicks the Story Event list tool right-click menu delete button
    Then the confirm delete Story Event dialog should be opened
    And the confirm delete Story Event dialog should show the Story Event name

  Scenario: Populated Story Event List + button
    Given The Story Event List Tool has been opened
    And a Story Event has been selected
    When the user clicks the Story Event list tool delete button
    Then the confirm delete Story Event dialog should be opened
    And the confirm delete Story Event dialog should show the Story Event name

  Scenario: Confirm Delete Dialog
    Given the delete Story Event dialog has been opened
    When the user clicks the confirm delete Story Event dialog delete button
    Then the delete Story Event dialog should be closed

  Scenario: Cancel Delete Dialog
    Given the delete Story Event dialog has been opened
    When the user clicks the confirm delete Story Event dialog cancel button
    Then the delete Story Event dialog should be closed