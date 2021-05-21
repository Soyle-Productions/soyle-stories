Feature: Manage Name Variants
  I want to use multiple names for my character
  so that I can talk about the same character in a scene with different names

  Background:
    Given I have started a project
    And I have created a character named "Bob"

  Scenario: Create Name Variant
    Given I am creating a name variant for the "Bob" character
    When I create a name variant of "Bobby" for the "Bob" character
    Then I should not be creating a name variant for the "Bob" character
    And the "Bob" character should have a name variant of "Bobby"

  Scenario: Rename Variant
    Given I have created a name variant of "Bobby" for the "Bob" character
    And I am renaming the "Bobby" name variant for the "Bob" character
    When I rename the name variant of "Bobby" for the "Bob" character to "Robert"
    Then I should not be renaming a name variant for the "Bob" character
    And the "Bob" character should not have a name variant of "Bobby"
    And the "Bob" character should have a name variant of "Robert"

  Scenario: Delete Variant
    Given I have created a name variant of "Bobby" for the "Bob" character
    When I remove the "Bobby" name variant for the "Bob" character
    Then the "Bob" character should not have a name variant of "Bobby"

  Rule: A character name variant cannot be the same as the character Display Name

    Scenario: Create Name Variant with Same Name as Display Name
      Given I am creating a name variant for the "Bob" character
      When I create a name variant of "Bob" for the "Bob" character
      Then I should still be creating a name variant for the "Bob" character
      And the "Bob" character should not have a name variant of "Bob"

    Scenario: Rename Variant to Same Name as Display Name
      Given I have created a name variant of "Bobby" for the "Bob" character
      And I am renaming the "Bobby" name variant for the "Bob" character
      When I rename the name variant of "Bobby" for the "Bob" character to "Bob"
      Then I should still be renaming the name variant "Bobby" for the "Bob" character
      And the "Bob" character should not have a name variant of "Bob"
      And the "Bob" character should have a name variant of "Bobby"

  Rule: A character name variant cannot be the same as any other variant for the character

    Scenario: Create Name Variant with Same Name as Other Variant
      Given I have created a name variant of "Bobby" for the "Bob" character
      And I am creating a name variant for the "Bob" character
      When I create a name variant of "Bobby" for the "Bob" character
      Then I should still be creating a name variant for the "Bob" character
      And the "Bob" character should have only one name variant of "Bobby"

    Scenario: Rename Variant to Same Name as Other Variant
      Given I have created a name variant of "Bobby" for the "Bob" character
      And I have created a name variant of "Robert" for the "Bob" character
      And I am renaming the "Robert" name variant for the "Bob" character
      When I rename the name variant of "Robert" for the "Bob" character to "Bobby"
      Then I should still be renaming the name variant "Robert" for the "Bob" character
      And the "Bob" character should have only one name variant of "Bobby"
      And the "Bob" character should have a name variant of "Robert"
