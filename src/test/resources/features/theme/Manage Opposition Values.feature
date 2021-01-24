Feature: Manage Opposition Values
  Describes the basic CRUD actions for opposition values

  Background:
    Given I have started a project
    And I have created a theme named "Growing Up"
    And a value web named "Love" has been created in the "Growing Up" theme

  Scenario: Create New Opposition Value
    When an opposition value is created in the "Love" value web in the "Growing Up" theme
    Then an opposition value named "Love 2" should have been created in the "Love" value web in the "Growing Up" theme

  Scenario: Rename Opposition Value
    When the first opposition value in the "Love" value web in the "Growing Up" theme is renamed to "Hate"
    Then the first opposition value in the "Love" value web in the "Growing Up" theme should have been renamed to "Hate"

  Scenario: Delete Opposition Value
    When the first opposition value in the "Love" value web in the "Growing Up" theme is deleted
    Then the "Love" value web in the "Growing Up" theme should have no opposition values