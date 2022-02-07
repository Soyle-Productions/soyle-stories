Feature: Remove a Character Name Variant

  Background:
    Given I have started a project
    And I have created a character named "Bob"

  Scenario: Delete Variant
    Given I have created a name variant of "Bobby" for the "Bob" character
    When I remove the "Bobby" name variant for the "Bob" character
    Then the "Bob" character should not have a name variant of "Bobby"