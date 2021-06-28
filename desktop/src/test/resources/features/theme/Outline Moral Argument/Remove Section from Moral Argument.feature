Feature: Remove Section from Moral Argument

  Background:
    Given I have started a project
    And I have created a theme named "Growing Up"
    And I have created a character named "Bob"
    And I have created a character arc for the character "Bob" in the "Growing Up" theme
    And the "Moral Decision" section has been added to the "Bob" character's "Growing Up" moral argument

  Scenario: List Existing Moral Argument Sections
    When I want to remove a section from the "Bob" character's "Growing Up" moral argument
    Then all of the "Bob" character's "Growing Up" moral argument sections should be listed
    And the optional sections in the "Bob" character's "Growing Up" moral argument should indicate they can be removed
    But the required sections in the "Bob" character's "Growing Up" moral argument should indicate they can not be removed

  Scenario: Remove a non-required section from a moral argument
    Given I am removing a section from the "Bob" character's "Growing Up" moral argument
    When I remove the "Moral Decision" section from the "Bob" character's "Growing Up" moral argument
    Then the "Moral Decision" section should be removed from the "Bob" character's "Growing Up" moral argument