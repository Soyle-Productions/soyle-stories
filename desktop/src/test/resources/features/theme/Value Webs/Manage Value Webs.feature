@PostAlpha
Feature: Manage Value Webs
  Describes the basic CRUD actions for value webs

  Background:
    Given I have started a project
    And I have created a theme named "Growing Up"

  Scenario: Create New Value Web
    When a value web is created with the name "Love" in the "Growing Up" theme
    Then the "Growing Up" theme should have a value web named "Love"

  Scenario: Rename Value Web
    Given I have created a value web named "Love" in the "Growing Up" theme
    When the "Love" value web in the "Growing Up" theme is renamed to "Greed"
    Then the "Growing Up" theme should not have a value web named "Love"
    And the "Growing Up" theme should have a value web named "Greed"

  Scenario: Delete Value Web
    Given I have created a value web named "Love" in the "Growing Up" theme
    When the "Love" value web in the "Growing Up" theme is deleted
    Then the "Growing Up" theme should not have a value web named "Love"