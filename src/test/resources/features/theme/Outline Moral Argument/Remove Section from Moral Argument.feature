Feature: Remove Section from Moral Argument

  Background:
    Given a project has been started
    And a theme named "Growing Up" has been created
    And a character named "Bob" has been created
    And Bob has been included in "Growing Up" as a major character
    And "Moral Decision" has been added to Bob's "Growing Up" moral argument

  Scenario: List Existing Moral Argument Sections
    When the user wants to remove a section from Bob's "Growing Up" moral argument
    Then all the sections in Bob's "Growing Up" moral argument should be listed
    And the optional sections in Bob's "Growing Up" moral argument should be shown to be able to be removed
    But the required sections in Bob's "Growing Up" moral argument should not be shown to be able to be removed

  Scenario: Remove a non-required section from a moral argument
    Given the user has indicated they want to remove a section from Bob's "Growing Up" moral argument
    When the "Moral Decision" section in Bob's "Growing Up" moral argument is removed
    Then the "Moral Decision" section should be removed from Bob's "Growing Up" moral argument