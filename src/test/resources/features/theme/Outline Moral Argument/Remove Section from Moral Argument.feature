Feature: Remove Section from Moral Argument

  Background:
    Given a project has been started
    And a theme named "Growing Up" has been created
    And a character named "Bob" has been created
    And the character "Bob" has been included in the "Growing Up" theme as a major character

  Scenario: List Existing Moral Argument Sections
    When the user indicates that they want to remove a section from the moral argument for "Bob" in the "Growing Up" theme
    Then all the sections in "Bob"s moral argument for the "Growing Up" theme should be listed
    And the sections with a type that is required in the moral argument should be marked
    And the sections with a type that is able to be removed should be marked

  Scenario: Remove an non-required section from a moral argument
    Given the user has indicated they want to remove a section from the moral argument for "Bob" in the "Growing Up" theme
    When a section with a type that is not required is selected to be removed from the moral argument for "Bob" in the "Growing Up" theme
    Then the selected section should be removed from the moral argument for "Bob" in the "Growing Up" theme