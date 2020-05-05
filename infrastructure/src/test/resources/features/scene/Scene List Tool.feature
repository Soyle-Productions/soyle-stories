Feature: Scene List Tool
  As a user working on a project
  I want to see the list of scenes I've created
  So that I can prevent duplicates and remove unneeded scenes

  Background:
    Given A project has been opened

  Scenario: Show special empty message when empty
    When The Scene List Tool is opened
    Then The Scene List Tool should show a special empty message

  Scenario Outline: Scene List Tool shows correct number of scenes
    Given <number> Scenes have been created
    When The Scene List Tool is opened
    Then The Scene List Tool should show all <number> scenes

    Examples:
      | number |
      | 1      |
      | 2      |
      | 3      |
      | 4      |
      | 5      |

  Scenario: Update when new Scenes created
    Given The Scene List Tool has been opened
    When A new Scene is created
    Then The Scene List Tool should show the new Scene

  Scenario Outline: Update when Scenes are deleted
    Given The Scene List Tool has been opened
    And <number> Scenes have been created
    When A Scene is deleted
    Then The Scene List Tool should not show the deleted Scene

    Examples:
      | number |
      | 2      |

  Scenario: Populated Scene List
    Given The Scene List Tool has been opened
    And A Scene has been created
    And the Scene right-click menu is open
    When the user clicks the Scene List Tool right-click menu delete button
    Then the Confirm Delete Scene Dialog should be opened
    And the cConfirm Delete Scene Dialog should show the location name

  Scenario: Populated Scene List + button
    Given The Scene List Tool has been opened
    And A Scene has been created
    And a Scene has been selected
    When the user clicks the Scene List Tool delete button
    Then the Confirm Delete Location Dialog should be opened
    And the Confirm Delete Location Dialog should show the Scene name

  Scenario: Renaming inside Scene List Tool creates input box
    Given the Scene right-click menu is open
    When the user clicks the Scene List Tool right-click menu Rename button
    Then the Scene's name should be replaced by an input box
    And the Scene rename input box should contain the Scene's name

  Scenario: Enter New Name in Input Box
    Given the Scene rename input box is visible
    And the user has entered a valid name
    When The user presses the Enter key
    Then the Scene rename input box should be replaced by the Scene name
    And the Scene name should be the new name

  Scenario: Esc Drop Down Menu Exit
    Given the Scene rename input box is visible
    When The user presses the Esc key
    Then the Scene rename input box should be replaced by the Scene name
    And the Scene name should be the original name

  Scenario: Click Away after Rename
    Given the Scene rename input box is visible
    And the user has entered a valid name
    When The user clicks away from the input box
    Then the Scene rename input box should be replaced by the Scene name
    And the Scene name should be the new name

  Scenario: Click Away without Rename
    Given the Scene rename input box is visible
    When The user clicks away from the input box
    Then the Scene rename input box should be replaced by the Scene name
    And the Scene name should be the original name
