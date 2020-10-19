Feature: Manage Value Webs
  Describes the basic CRUD actions for value webs

  Background:
    Given a project has been started
    And a theme named "Growing Up" has been created

  Scenario: Create New Value Web
    When a value web is created with the name "Love" in the "Growing Up" theme
    Then a value web named "Love" should have been created in the "Growing Up" theme

  Scenario: Rename Value Web
    Given a value web named "Love" has been created in the "Growing Up" theme
    When the "Love" value web in the "Growing Up" theme is renamed to "Greed"
    Then the value web originally named "Love" in the "Growing Up" theme should have been renamed to "Greed"

  Scenario: Delete Value Web
    Given a value web named "Love" has been created in the "Growing Up" theme
    When the "Love" value web in the "Growing Up" theme is deleted
    Then the "Love" value web in the "Growing Up" theme should have been deleted