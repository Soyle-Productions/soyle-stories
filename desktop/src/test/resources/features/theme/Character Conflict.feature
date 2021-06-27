Feature: Character Conflict

  Background:
    Given I have started a project
    * I have created a character named "Bob"
    * I have created a character arc named "Growing Up" for the "Bob" character
    * I am examining the "Growing Up" theme's central conflict for the "Bob" character

  Scenario: Provide Perspective Character with Psychological Weakness
    When I change the "Bob" character's psychological weakness in the "Growing Up" theme to "laziness"
    Then the "Bob" character's psychological weakness in the "Growing Up" theme should be "laziness"

  Scenario: Provide Perspective Character with Moral Weakness
    When I change the "Bob" character's moral weakness in the "Growing Up" theme to "laziness"
    Then the "Bob" character's moral weakness in the "Growing Up" theme should be "laziness"
