@storyevent 
Feature: Story Event List Tool
  As a user working on a project
  I want to see the list of story events I've created
  So that I can prevent duplicates and remove unneeded story events

  Background:
    Given A project has been opened

	@create-new-story-event
  Scenario: Show special empty message when empty
    When The Story Event List Tool is opened
    Then The Story Event List Tool should show a special empty message
    
	@create-new-story-event
  Scenario: Open Story Event creation dialog when empty
    Given The Story Event List Tool has been opened
    When User clicks the center create new story event button
    Then The create new Story Event dialog should be open
    
	@create-new-story-event
  Scenario: Open Story Event creation dialog when populated
    Given A Story Event has been created
    And The Story Event List Tool has been opened
    When User clicks the bottom create new Story Event button
    Then The create new Story Event dialog should be open
    
	@create-new-story-event
  Scenario Outline: Open Story Event creation dialog with relative Story Event
    Given A Story Event has been created
    And The Story Event List Tool has been opened
	And the Story Event right-click menu has been opened in the Story Event List Tool
    When the <option> Story Event right-click menu option is selected in the Story Event List Tool
    Then The create new Story Event dialog should be open
	
	Examples:
	  | option |
	  | "Insert New Story Event Before" |
	  | "Insert New Story Event After"  |
    
	@create-new-story-event
  Scenario: Update when new Story Event created
    Given The Story Event List Tool has been opened
    And The Story Event List Tool tab has been selected
    When A new Story Event is created without a relative Story Event
    Then The Story Event List Tool should show the new Story Event
	And the new Story Event should be at the end of the Story Event List Tool
    
	@create-new-story-event
  Scenario: Update when new Story Event created before relative Story Event
    Given The Story Event List Tool has been opened
    And The Story Event List Tool tab has been selected
	And a Story Event has been created
    When A new Story Event is created before a relative Story Event
    Then The Story Event List Tool should show the new Story Event
	And the new Story Event should be listed before the relative Story Event in the Story Event List Tool
	
	@create-new-story-event
  Scenario: Update when new Story Event created after relative Story Event
    Given The Story Event List Tool has been opened
    And The Story Event List Tool tab has been selected
	And 2 Story Events have been created
    When A new Story Event is created after the first Story Event
    Then The Story Event List Tool should show the new Story Event
	And the new Story Event should be listed after the first Story Event in the Story Event List Tool
