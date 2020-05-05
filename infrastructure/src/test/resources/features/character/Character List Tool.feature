Feature: Character List Tool
  As a user working on a project
  I want to see the list of Characters I've created
  So that I can prevent duplicates and remove unneeded Characters

  Background:
    Given A project has been opened

  Scenario: Show special empty message when empty
    When The Character List Tool is opened
    Then The Character List Tool should show a special empty message

  Scenario Outline: Character List Tool shows correct number of Characters
    Given <number> Characters have been created
    When The Character List Tool is opened
    Then The Character List Tool should show all <number> Characters

    Examples:
      | number |
      | 1      |
      | 2      |
      | 3      |
      | 4      |
      | 5      |

  Scenario: Update when new Characters created
    Given The Character List Tool has been opened
    When A new Character is created
    Then The Character List Tool should show the new Character

  Scenario Outline: Update when Characters are deleted
    Given The Character List Tool has been opened
    And <number> Characters have been created
    When A Character is deleted
    Then The Character List Tool should not show the deleted Character

    Examples:
      | number |
      | 2      |

  Scenario: Populated Character List
    Given The Character List Tool has been opened
    And A Character has been created
    And the Character right-click menu is open
    When the user clicks the Character List Tool right-click menu delete button
    Then the Confirm Delete Character Dialog should be opened
    And the Confirm Delete Character Dialog should show the Character name

  Scenario: Populated Character List + button
    Given The Character List Tool has been opened
    And A Character has been created
    And a Character has been selected
    When the user clicks the Character List Tool delete button
    Then the Confirm Delete Character Dialog should be opened
    And the Confirm Delete Character Dialog should show the Character name

  Scenario: Renaming inside Character List Tool creates input box
    Given the Character right-click menu is open
    When the user clicks the Character List Tool right-click menu Rename button
    Then the Character's name should be replaced by an input box
    And the Character rename input box should contain the Character's name

  Scenario: Enter New Name in Input Box
    Given the Character rename input box is visible
    And the user has entered a valid name
    When The user presses the Enter key
    Then the Character rename input box should be replaced by the Character name
    And the Character name should be the new name

  Scenario: Esc Drop Down Menu Exit
    Given the Character rename input box is visible
    When The user presses the Esc key
    Then the Character rename input box should be replaced by the Character name
    And the Character name should be the original name

  Scenario: Click Away after Rename
    Given the Character rename input box is visible
    And the user has entered a valid name
    When The user clicks away from the input box
    Then the Character rename input box should be replaced by the Character name
    And the Character name should be the new name

  Scenario: Click Away without Rename
    Given the Character rename input box is visible
    When The user clicks away from the input box
    Then the Character rename input box should be replaced by the Character name
    And the Character name should be the original name
