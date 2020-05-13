@storyevent 
Feature: Story Event Details Tool
  As a user working on a project
  I want to set the details of the story events I've created
  So that I can link relevant elements to story events 

Background:
    Given A project has been opened
    And A Story Event has been created
    And The Story Event List Tool has been opened

  Scenario: Open Story Event Details Tool through Story Event List Tool
    Given the Story Event right-click menu is open
    When the user clicks the story event list tool right-click menu open button
    Then the Story Event Details Tool should be open

@link-story-event-to-character-arc
Feature: Link Story Event to Character Arc Section
	
  Scenario: No Character Arcs available 
    Given no Character Arcs have been created 
    When the Story Event Details Tool is opened 
    Then the Character Arc Section dropdown in the Story Events Details Tool should be disabled
	
  Scenario: Many Character Arcs available to select
    Given at least one Location has been created
    And the Base Story Structure Tool has been opened
    When the Character Arc Section Location dropdown is clicked
    Then all Locations should be listed in the Character Arc Section Location dropdown menu

  Scenario: Many Character Arcs available to select
    Given at least one Location has been created
    And the Base Story Structure Tool has been opened
    When the Character Arc Section Location dropdown is clicked
    Then all Locations should be listed in the Character Arc Section Location dropdown menu

  Scenario: Selecting a Location
    Given at least one Location has been created
    And the Base Story Structure Tool has been opened
    And the Character Arc Section Location dropdown menu has been opened
    When a Location in the Character Arc Section Location dropdown menu is selected
    Then the Character Arc Section Location dropdown should show the selected Location name
    And the Character Arc Section Location dropdown menu should be closed

  Scenario: Closing the menu without selecting
    Given at least one Location has been created
    And the Base Story Structure Tool has been opened
    And the Character Arc Section Location dropdown menu has been opened
    When the user clicks outside the Character Arc Section Location dropdown menu
    Then the Character Arc Section Location dropdown menu should be closed

Feature: Link Location to Character Arc Section

  Background:
    Given A project has been opened
    And a Character has been created
    And a Character Arc has been created

  Scenario: No Locations available
    Given no Locations have been created
    When the Base Story Structure Tool is opened
    Then the Character Arc Section Location dropdown in the Base Story Structure Tool should be disabled

  Scenario: Many Locations available
    Given at least one Location has been created
    When the Base Story Structure Tool is opened
    Then the Character Arc Section Location dropdown in the Base Story Structure Tool should not be disabled

  Scenario: Many Locations available to select
    Given at least one Location has been created
    And the Base Story Structure Tool has been opened
    When the Character Arc Section Location dropdown is clicked
    Then all Locations should be listed in the Character Arc Section Location dropdown menu

  Scenario: Selecting a Location
    Given at least one Location has been created
    And the Base Story Structure Tool has been opened
    And the Character Arc Section Location dropdown menu has been opened
    When a Location in the Character Arc Section Location dropdown menu is selected
    Then the Character Arc Section Location dropdown should show the selected Location name
    And the Character Arc Section Location dropdown menu should be closed

  Scenario: Closing the menu without selecting
    Given at least one Location has been created
    And the Base Story Structure Tool has been opened
    And the Character Arc Section Location dropdown menu has been opened
    When the user clicks outside the Character Arc Section Location dropdown menu
    Then the Character Arc Section Location dropdown menu should be closed

Feature: Link Location to Character Arc Section

  Background:
    Given A project has been opened
    And a Character has been created
    And a Character Arc has been created

  Scenario: No Locations available
    Given no Locations have been created
    When the Base Story Structure Tool is opened
    Then the Character Arc Section Location dropdown in the Base Story Structure Tool should be disabled

  Scenario: Many Locations available
    Given at least one Location has been created
    When the Base Story Structure Tool is opened
    Then the Character Arc Section Location dropdown in the Base Story Structure Tool should not be disabled

  Scenario: Many Locations available to select
    Given at least one Location has been created
    And the Base Story Structure Tool has been opened
    When the Character Arc Section Location dropdown is clicked
    Then all Locations should be listed in the Character Arc Section Location dropdown menu

  Scenario: Selecting a Location
    Given at least one Location has been created
    And the Base Story Structure Tool has been opened
    And the Character Arc Section Location dropdown menu has been opened
    When a Location in the Character Arc Section Location dropdown menu is selected
    Then the Character Arc Section Location dropdown should show the selected Location name
    And the Character Arc Section Location dropdown menu should be closed

  Scenario: Closing the menu without selecting
    Given at least one Location has been created
    And the Base Story Structure Tool has been opened
    And the Character Arc Section Location dropdown menu has been opened
    When the user clicks outside the Character Arc Section Location dropdown menu
    Then the Character Arc Section Location dropdown menu should be closed

Feature: Rename Location

  Background:
    Given A project has been opened
    And A location has been created
    And The Location List Tool has been opened

  Scenario: Renaming inside Location List Tool creates input box
    Given the location right-click menu is open
    When the user clicks the location list tool right-click menu Rename button
    Then the location's name should be replaced by an input box
    And the location rename input box should contain the location's name

  Scenario: Enter New Name in Input Box
    Given the location rename input box is visible
    And the user has entered a valid Location name
    When The user presses the Enter key
    Then the location rename input box should be replaced by the location name
    And the location name should be the new name

  Scenario: Esc Drop Down Menu Exit
    Given the location rename input box is visible
    When The user presses the Esc key
    Then the location rename input box should be replaced by the location name
    And the location name should be the original name


Feature: Unlink Location from Character Arc Section

  Background:
    Given the Base Story Structure Tool has been opened
    And a Character Arc Section has a linked Location

  Scenario: Selecting Unlink option in Character Arc Section Location Dropdown
    Given the Character Arc Section Location dropdown menu has been opened
    When the selected Location in in Character Arc Section Location Dropdown is deselected
    Then the Character Arc Section Location dropdown should show an empty state