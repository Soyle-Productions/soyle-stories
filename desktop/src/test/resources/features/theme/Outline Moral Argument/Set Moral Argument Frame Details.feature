Feature: Set Moral Argument Frame Details

  Background:
    Given I have started a project
    And I have created a theme named "Growing Up"

  Scenario: Update Moral Problem
    When the moral problem for the "Growing Up" theme is changed to "Is it ok to eat cake?"
    Then the moral problem for the "Growing Up" theme should be "Is it ok to eat cake?"

  Scenario: Update Theme Line
    When the theme line for the "Growing Up" theme is changed to "It's always ok to eat cake, except when it isn't"
    Then the theme line for the "Growing Up" theme should be "It's always ok to eat cake, except when it isn't"

  Scenario: Update Thematic Revelation
    When the thematic revelation for the "Growing Up" theme is changed to "We're all able to eat cake"
    Then the thematic revelation for the "Growing Up" theme should be "We're all able to eat cake"
