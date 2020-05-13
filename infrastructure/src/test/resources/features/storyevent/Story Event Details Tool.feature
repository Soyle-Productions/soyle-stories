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

@link-location-to-story-event
Feature: Link Location to Story Event
	
  Scenario: No Location available 
    Given no Locations have been created 
    When the Story Event Details Tool is opened 
    Then the Location Section Story Event dropdown in the Story Events Details Tool should be disabled
	
  Scenario: Many Locations available to select
    Given at least one Location has been created
    And the Story Event Details Tool has been opened
    When the Location Section Story Event dropdown is clicked
    Then all Locations should be listed in the Location Section Story Event dropdown menu


  Scenario: Selecting a Location
    Given at least one Location has been created
    And the Story Event Details Tool has been opened
    And the Location Section Story Event dropdown menu has been opened
    When a Location in the Location Section Story Event dropdown menu is selected
    Then the Location Section Story Event dropdown should show the selected Location name
    And the Location Section Story Event dropdown menu should be closed

  Scenario: Closing the menu without selecting
    Given at least one Location has been created
    And the Story Event Details Tool has been opened
    And the Location Section Story Event dropdown menu has been opened
    When the user clicks outside the Location Section Story Event dropdown menu
    Then the Location Section Story Event dropdown menu should be closed

@link-character-to-story-event
Feature: Link Character to Story Event
	
  Scenario: No Character available 
    Given no Characters have been created 
    When the Story Event Details Tool is opened 
    Then the Character Section Story Event dropdown in the Story Events Details Tool should be disabled
	
  Scenario: Many Characters available to select
    Given at least one Character has been created
    And the Story Event Details Tool has been opened
    When the Character Section Story Event dropdown is clicked
    Then all Characters should be listed in the Character Section Story Event dropdown menu


  Scenario: Selecting a Character
    Given at least one Character has been created
    And the Story Event Details Tool has been opened
    And the Character Section Story Event dropdown menu has been opened
    When a Character in the Character Section Story Event dropdown menu is selected
    Then the Character Section Story Event dropdown should show the selected Character name
    And the Character Section Story Event dropdown menu should be closed

  Scenario: Closing the menu without selecting
    Given at least one Character has been created
    And the Story Event Details Tool has been opened
    And the Character Section Story Event dropdown menu has been opened
    When the user clicks outside the Character Section Story Event dropdown menu
    Then the Character Section Story Event dropdown menu should be closed
