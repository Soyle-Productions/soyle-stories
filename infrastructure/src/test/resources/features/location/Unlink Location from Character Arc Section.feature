Feature: Unlink Location from Character Arc Section

  Background:
    Given the Base Story Structure Tool has been opened
    And a Character Arc Section has a linked Location

  Scenario: Selecting Unlink option in Character Arc Section Location Dropdown
    Given the Character Arc Section Location dropdown menu has been opened
    When the Unlink option in Character Arc Section Location Dropdown is selected
    Then the Character Arc Section Location dropdown should show an empty state