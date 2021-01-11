Feature: Manage Characters
  As a user working on a project
  I want to manage the characters I've created
  So that I can prevent duplicates and remove unneeded Characters

  Background:
    Given I have started a project
  
  Scenario: Rename a Character
    Given I have created a character named "Bob"
    When I rename the "Bob" character to "Frank"
    Then the character formerly named "Bob" should have the name "Frank"