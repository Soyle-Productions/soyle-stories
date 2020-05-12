Feature: Scene List Tool
  As a user working on a project
  I want to see the list of story events I've created
  So that I can prevent duplicates and remove unneeded story events

  Background:
    Given A project has been opened

  Scenario: Show special empty message when empty
    When The Story Event List Tool is opened
    Then The Story Event List Tool should show a special empty message

  Scenario: Update when new Story Event created
    Given The Story Event List Tool has been opened
    And The Story Event List Tool tab has been selected
    When A new Story Event is created
    Then The Story Event List Tool should show the new Story Event
    
  Scenario: Open Story Event creation dialog when empty
    Given The Story Event List Tool has been opened
    When User clicks the center create new story event button
    Then The create new Story Event dialog should be open
    
  Scenario: Open Story Event creation dialog when populated
    Given A Story Event has been created
    And The Story Event List Tool has been opened
    When User clicks the bottom create new Story Event button
    Then The create new Story Event dialog should be open
    
  
