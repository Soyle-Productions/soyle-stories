Feature: Create New Character

  Background:
    Given I have started a project

  Scenario: Create a New Character
    When I create a character named "Bob"
    Then a character named "Bob" should have been created

  Scenario: Create a New Character with Blank Name
    When I create a character without a name
    Then I should still be creating a character
    And a new character should not have been created