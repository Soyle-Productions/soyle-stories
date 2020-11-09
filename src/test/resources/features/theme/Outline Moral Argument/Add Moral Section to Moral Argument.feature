Feature: Add Moral Section to Moral Argument

  Background:
    Given a project has been started
    And a theme named "Growing Up" has been created
    And a character named "Bob" has been created
    And Bob has been included in "Growing Up" as a major character

  Scenario: List Available Character Arc Section Types to Add to Moral Argument
    When the user wants to add a new section to Bob's "Growing Up" moral argument
    Then all the moral argument section types should be listed for "Bob"s moral argument in the "Growing Up" theme

  Scenario: Add Character Arc Section Type to End of Moral Argument
    Given the user has indicated they want to add a new section to Bob's "Growing Up" moral argument
    When an unused moral argument section type is selected to be added for "Bob" in the "Growing Up" theme
    Then a new section should have been added to "Bob"s character arc in the "Growing Up" theme with that type
    And the new section should be at the end of "Bob"s moral argument in the "Growing Up" theme

  Scenario: Add Character Arc Section Type Between two Sections
    Given the user has indicated they want to add a new section to Bob's "Growing Up" moral argument
    When a moral argument section type is selected for "Bob" in the "Growing Up" theme between two existing sections
    Then a new section should have been added to "Bob"s character arc in the "Growing Up" theme with that type
    And the new section should be between the two pre-existing moral argument sections