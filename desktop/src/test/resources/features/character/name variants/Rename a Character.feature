Feature: Rename a Character

  Background:
    Given I have started a project
    And I have created a character named "Bob"

  Scenario: Rename a Character
    When I rename the "Bob" character to "Frank"
    Then the character formerly named "Bob" should have the name "Frank"

  Scenario: Rename Variant
    Given I have created a name variant of "Bobby" for the "Bob" character
    And I am renaming the "Bobby" name variant for the "Bob" character
    When I rename the name variant of "Bobby" for the "Bob" character to "Robert"
    Then I should not be renaming a name variant for the "Bob" character
    And the "Bob" character should not have a name variant of "Bobby"
    And the "Bob" character should have a name variant of "Robert"

  Rule: A Character Display Name cannot be Blank

    Scenario: Rename a Character to Blank Name
      When I rename the "Bob" character to ""
      Then I should still be renaming the "Bob" character
      And the "Bob" character should still have the display name of "Bob"


  Rule: A character name variant cannot be the same as the character Display Name

    Scenario: Rename Variant to Same Name as Display Name
      Given I have created a name variant of "Bobby" for the "Bob" character
      And I am renaming the "Bobby" name variant for the "Bob" character
      When I rename the name variant of "Bobby" for the "Bob" character to "Bob"
      Then I should still be renaming the name variant "Bobby" for the "Bob" character
      And the "Bob" character should not have a name variant of "Bob"
      And the "Bob" character should have a name variant of "Bobby"

  Rule: A character name variant cannot be the same as any other variant for the character

    Scenario: Rename Variant to Same Name as Other Variant
      Given I have created a name variant of "Bobby" for the "Bob" character
      And I have created a name variant of "Robert" for the "Bob" character
      And I am renaming the "Robert" name variant for the "Bob" character
      When I rename the name variant of "Robert" for the "Bob" character to "Bobby"
      Then I should still be renaming the name variant "Robert" for the "Bob" character
      And the "Bob" character should have only one name variant of "Bobby"
      And the "Bob" character should have a name variant of "Robert"
