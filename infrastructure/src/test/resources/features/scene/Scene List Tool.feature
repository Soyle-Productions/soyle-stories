@scene
Feature: Scene List Tool
  As a user working on a project
  I want to see the list of scenes I've created
  So that I can prevent duplicates and remove unneeded scenes

  Background:
    Given A project has been opened

  @create-scene
  Scenario: Show special empty message when empty
    When The Scene List Tool is opened
    Then The Scene List Tool should show a special empty message
	
  @create-scene
  Scenario: Open Scene creation dialog when empty
    Given the Scene List Tool has been opened
    When the center Create New Scene button is selected
    Then the Create Scene Dialog should be open
	
  @create-scene
  Scenario: Open Scene creation dialog when populated
    Given a Scene has been created
    And the Scene List Tool has been opened
    When the bottom Create New Scene button is selected
    Then the Create Scene Dialog should be open

  @create-scene
  Scenario Outline: Open Scene creation dialog with relative Scene
    Given a Scene has been created
    And the Scene List Tool has been opened
    And the Scene List Tool right-click menu has been opened
    When the Scene List Tool right-click menu <option> option is selected
    Then the Create Scene Dialog should be open

    Examples:
      | option                    |
      | "Insert New Scene Before" |
      | "Insert New Scene After"  |

  @create-scene
  Scenario: Update when new Scene created
    Given the Scene List Tool has been opened
    And the Scene List Tool tab has been selected
    When a new Scene is created without a relative Scene
    Then the Scene List Tool should show the new Scene
    And the new Scene should be at the end of the Scene List Tool

  @create-scene
  Scenario: Update when new Scene created before relative Scene
    Given the Scene List Tool has been opened
    And the Scene List Tool tab has been selected
    And a Scene has been created
    When a new Scene is created before a relative Scene
    Then the Scene List Tool should show the new Scene
    And the new Scene should be listed before the relative Scene in the Scene List Tool

  @create-scene
  Scenario: Update when new Scene created after relative Scene
    Given the Scene List Tool has been opened
    And the Scene List Tool tab has been selected
    And 2 Scenes have been created
    When a new Scene is created after the first Scene
    Then the Scene List Tool should show the new Scene
    And the new Scene should be listed after the first Scene in the Scene List Tool

  @list-scenes
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

  @delete-scene
  Scenario: Update when Scenes are deleted
    Given The Scene List Tool has been opened
    And 2 Scenes have been created
    When A Scene is deleted
    Then The Scene List Tool should not show the deleted Scene

  @delete-scene
  Scenario: Populated Scene List
    Given The Scene List Tool has been opened
    And A Scene has been created
    And the Scene right-click menu has been opened
    When the Scene List Tool right-click menu "Delete" option is selected
    Then the Confirm Delete Scene Dialog should be opened
    And the Confirm Delete Scene Dialog should show the Scene name

  @delete-scene
  Scenario: Populated Scene List + button
    Given The Scene List Tool has been opened
    And A Scene has been created
    And a Scene has been selected
    When the user clicks the Scene List Tool delete button
    Then the Confirm Delete Scene Dialog should be opened
    And the Confirm Delete Scene Dialog should show the Scene name

  @rename-scene
  Scenario: Renaming inside Scene List Tool creates input box
    Given the Scene right-click menu has been opened
    When the Scene List Tool right-click menu "Rename" option is selected
    Then the Scene's name should be replaced by an input box
    And the Scene rename input box should contain the Scene's name

  @rename-scene
  Scenario: Enter New Name in Input Box
    Given the Scene rename input box is visible
    And the user has entered a valid Scene name
    When The user presses the Enter key
    Then the Scene rename input box should be replaced by the Scene name
    And the Scene name should be the new name

  @rename-scene
  Scenario: Esc Drop Down Menu Exit
    Given the Scene rename input box is visible
    When The user presses the Esc key
    Then the Scene rename input box should be replaced by the Scene name
    And the Scene name should be the original name

  @rename-scene
  Scenario: Click Away after Rename
    Given the Scene rename input box is visible
    And the user has entered a valid Scene name
    When The user clicks away from the input box
    Then the Scene rename input box should be replaced by the Scene name
    And the Scene name should be the new name

  @rename-scene
  Scenario: Click Away without Rename
    Given the Scene rename input box is visible
    When The user clicks away from the input box
    Then the Scene rename input box should be replaced by the Scene name
    And the Scene name should be the original name
	
  @reorder-scene
  Scenario: Drag Scene to reorder
    Given the Scene List Tool has been opened
	  And 3 Scenes have been created
	 When a Scene is dragged to a new position in the Scene List Tool
	 Then the Confirm Reorder Scene Dialog should be shown
	
  @reorder-scene @excluded
  Scenario: Reorder Scene without confirmation
    Given the Scene List Tool has been opened
	  And 3 Scenes have been created
	  And the user has requested to not be shown the Confirm Reorder Scene Dialog
	 When a Scene is dragged to a new position in the Scene List Tool
	 Then the Scene should be reordered
	 
  @reorder-scene
  Scenario: Update when reordered
	Given the Scene List Tool has been opened
	  And 3 Scenes have been created
	 When a Scene is reordered
	 Then the Scene should be in its new position
	  And all Scenes in the Scene List Tool should be numbered to match the list order