@storyevent 
Feature: Story Event Details Tool
  As a user working on a project
  I want to set the details of the story events I've created
  So that I can link relevant elements to story events 

  Background:
    Given A project has been opened
    And A Story Event has been created

    @link-location-to-story-event
  Scenario: No Locations available
    Given no Locations have been created 
    When the Story Event Details Tool is opened 
    Then the Story Events Details Tool Location dropdown in the should be disabled

  @link-location-to-story-event
  Scenario: Many Locations available to select
    Given at least one Location has been created
    And the Story Event Details Tool has been opened
    When the Location Section Story Event dropdown is clicked
    Then all Locations should be listed in the Story Events Details Tool Location dropdown menu

  @link-location-to-story-event
  Scenario: Selecting a Location
    Given at least one Location has been created
    And the Story Event Details Tool has been opened
    And the LStory Events Details Tool Location dropdown menu has been opened
    When a Location in the Story Events Details Tool Location dropdown menu is selected
    Then the Story Events Details Tool Location dropdown should show the selected Location name
    And the Story Events Details Tool Location dropdown menu should be closed

  @link-location-to-story-event
  Scenario: Closing the menu without selecting
    Given at least one Location has been created
    And the Story Event Details Tool has been opened
    And the Story Events Details Tool Location dropdown menu has been opened
    When the user clicks outside the Story Events Details Tool Location dropdown menu
    Then the Story Events Details Tool Location dropdown menu should be closed

    @add-character-to-story-event
  Scenario: No Character available 
    Given no Characters have been created 
    When the Story Event Details Tool is opened 
    Then the Story Event Details Character Selection dropdown should be disabled

  @add-character-to-story-event
  Scenario: Many Characters available to select
    Given at least one Character has been created
    And the Story Event Details Tool has been opened
    When the Story Event Details Character Selection dropdown is clicked
    Then all Characters should be listed in the Story Event Details Character Selection dropdown menu

  @add-character-to-story-event
  Scenario: Selecting a Character
    Given at least one Character has been created
    And the Story Event Details Tool has been opened
    And the Story Event Details Character Selection dropdown menu has been opened
    When a Character in the Story Event Details Character Selection dropdown menu is selected
    Then the selected Character should be shown in place of the Story Event Details Character Selection dropdown
    And the Story Event Details Character Selection dropdown should be below the list of included characters

  @add-character-to-story-event
  Scenario: Closing the menu without selecting
    Given at least one Character has been created
    And the Story Event Details Tool has been opened
    And the Story Event Details Character Selection dropdown menu has been opened
    When the user clicks outside the Story Event Details Character Selection dropdown menu
    Then the Story Event Details Character Selection dropdown menu should be closed
