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

  Scenario: Delay Delete Character
    Given I have created a character named "Bob"
    When I want to delete the "Bob" character
    Then I should be prompted to confirm deleting the "Bob" character
    But the "Bob" character should not have been deleted

#  Scenario: Immediately Delete Character
#    Given I have created a character named "Bob"
#    And I have requested to not be prompted to confirm deleting a character
#    When I want to delete the "Bob" character
#    Then the "Bob" character should have been deleted

  Scenario: Confirm Delete Character
    Given I have created a character named "Bob"
    And I am deleting the "Bob" character
    When I confirm I want to delete the "Bob" character
    Then the "Bob" character should have been deleted