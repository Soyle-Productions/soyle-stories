Feature: Create a Character Name Variant

  Background:
    Given I have started a project
    And I have created a character named "Bob"

  Scenario: Create Name Variant
    When I create a name variant of "Bobby" for the "Bob" character
    Then the "Bob" character should have a name variant of "Bobby"

  Rule: A character name variant cannot be the same as the character Display Name

    Scenario: Create Name Variant with Same Name as Display Name
      Given I am creating a name variant for the "Bob" character
      When I create a name variant of "Bob" for the "Bob" character
      Then the "Bob" character should not have a name variant of "Bob"
      And I should still be creating a name variant for the "Bob" character

  Rule: A character name variant cannot be the same as any other variant for the character

    Scenario: Create Name Variant with Same Name as Other Variant
      Given I have created a name variant of "Bobby" for the "Bob" character
      And I am creating a name variant for the "Bob" character
      When I create a name variant of "Bobby" for the "Bob" character
      Then I should still be creating a name variant for the "Bob" character
      And the "Bob" character should have only one name variant of "Bobby"