Feature: Manage Themes
  Describes the basic CRUD actions for themes

  Background:
    Given a project has been started

  Scenario: Create New Theme
    When a theme is created with the name "Growing Up"
    Then a theme named "Growing Up" should have been created

  Scenario: Rename Theme
    Given a theme named "Transformation" has been created
    When the "Transformation" theme is renamed with the name "Growing Up"
    Then the theme originally named "Transformation" should have been renamed to "Growing Up"

  Scenario: Rename Theme with Major Characters
    Given a theme named "Transformation" has been created
    And a theme named "Becoming a Ruler" has been created
    And the following characters have been created
      | "Bob" | "Frank" | "Alice" |
    And the following characters have been included as major characters in the "Transformation" theme
      | "Bob" | "Frank" |
    And the following characters have been included as major characters in the "Becoming a Ruler" theme
      | "Alice" |
    When the "Transformation" theme is renamed with the name "Growing Up"
    Then all the character arcs in the theme originally named "Transformation" should have been renamed to "Growing Up"
    But all the character arcs in the theme named "Becoming a Ruler" should still be named "Becoming a Ruler"

  Scenario: Delete Theme
    Given a theme named "Growing Up" has been created
    When the "Growing Up" theme is deleted
    Then the "Growing Up" theme should have been deleted

  Scenario: Delete Theme with Major Characters
    Given a theme named "Growing Up" has been created
    And a theme named "Becoming a Ruler" has been created
    And the following characters have been created
      | "Bob" | "Frank" | "Alice" |
    And the following characters have been included as major characters in the "Growing Up" theme
      | "Bob" | "Frank" |
    And the following characters have been included as major characters in the "Becoming a Ruler" theme
      | "Alice" |
    When the "Growing Up" theme is deleted
    Then all the character arcs in the theme named "Growing Up" should have been deleted
    But all the character arcs in the theme named "Becoming a Ruler" should not have been deleted