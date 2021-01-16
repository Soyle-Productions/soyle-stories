Feature: Manage Themes
  Describes the basic CRUD actions for themes

  Background:
    Given I have started a project

  Scenario: Create New Theme
    When I create a theme named "Growing Up"
    Then a theme named "Growing Up" should have been created

  Scenario: Rename Theme
    Given I have created a theme named "Transformation"
    When I rename the "Transformation" theme to "Growing Up"
    Then the theme originally named "Transformation" should have been renamed to "Growing Up"

  Scenario: Rename Theme with Major Characters
    Given I have created a theme named "Transformation"
    And I have created a theme named "Becoming a Ruler"
    And I have created the following characters
      | Bob | Frank | Alice |
    And I have created a character arc for the "Bob" character in the "Transformation" theme
    And I have created a character arc for the "Frank" character in the "Transformation" theme
    And I have created a character arc for the "Alice" character in the "Becoming a Ruler" theme
    When I rename the "Transformation" theme to "Growing Up"
    Then the "Bob" character's character arc for the "Growing Up" theme should have been renamed to "Growing Up"
    And the "Frank" character's character arc for the "Growing Up" theme should have been renamed to "Growing Up"
    But the "Alice" character's character arc for the "Becoming a Ruler" theme should not have been renamed to "Growing Up"

  Scenario: Delete Theme
    Given I have created a theme named "Growing Up"
    When the "Growing Up" theme is deleted
    Then the "Growing Up" theme should have been deleted

  Scenario: Delete Theme with Major Characters
    Given I have created a theme named "Growing Up"
    And I have created a theme named "Becoming a Ruler"
    And the following characters have been created
      | "Bob" | "Frank" | "Alice" |
    And the following characters have been included as major characters in the "Growing Up" theme
      | "Bob" | "Frank" |
    And the following characters have been included as major characters in the "Becoming a Ruler" theme
      | "Alice" |
    When the "Growing Up" theme is deleted
    Then all the character arcs in the theme named "Growing Up" should have been deleted
    But all the character arcs in the theme named "Becoming a Ruler" should not have been deleted