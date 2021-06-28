Feature: Manage Characters
  As a user working on a project
  I want to manage the characters I've created
  So that I can prevent duplicates and remove unneeded Characters

  Background:
    Given I have started a project

  Scenario: Create a New Character
    Given I am creating a character
    When I create a character named "Bob"
    Then I should not be creating a character
    And a character named "Bob" should have been created

  Scenario: Rename a Character
    Given I have created a character named "Bob"
    When I rename the "Bob" character to "Frank"
    Then the character formerly named "Bob" should have the name "Frank"

  Rule: A Character Display Name cannot be Blank

    Scenario: Create a New Character with Blank Name
      Given I am creating a character
      When I create a character without a name
      Then I should still be creating a character
      And a new character should not have been created

    Scenario: Rename a Character to Blank Name
      Given I have created a character named "Bob"
      When I rename the "Bob" character to ""
      Then I should still be renaming the "Bob" character
      And the "Bob" character should still have the display name of "Bob"

  Rule: A Character Deletion Must be Confirmed

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