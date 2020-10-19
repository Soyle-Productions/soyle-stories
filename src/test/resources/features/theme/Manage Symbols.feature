Feature: Manage Symbols
  Describes the basic CRUD actions for symbols

  Background:
    Given a project has been started
    And a theme named "Growing Up" has been created

  Scenario: Create New Symbol
    When a symbol is created with the name "Ring" in the "Growing Up" theme
    Then a symbol named "Ring" should have been created in the "Growing Up" theme

  Scenario: Rename Symbol
    Given a symbol named "Ring" has been created in the "Growing Up" theme
    When the "Ring" symbol in the "Growing Up" theme is renamed with the name "Cube"
    Then the symbol originally named "Ring" in the "Growing Up" theme should have been renamed to "Cube"

  Scenario: Rename Symbol Used as Symbolic Item for Opposition Value
    Given a symbol named "Ring" has been created in the "Growing Up" theme
    And a value web named "Love" has been created in the "Growing Up" theme
    And the "Ring" symbol in the "Growing Up" theme has been added as a symbolic item to the opposition value in the "Love" value web
    When the "Ring" symbol in the "Growing Up" theme is renamed with the name "Cube"
    Then the symbol originally named "Ring" in the "Growing Up" theme should have been renamed to "Cube"
    And all symbolic items for the symbol originally named "Ring" in the "Growing Up" theme should have been renamed to "Cube"

  Scenario: Delete Symbol
    Given a symbol named "Ring" has been created in the "Growing Up" theme
    When the "Ring" symbol in the "Growing Up" theme is deleted
    Then the "Ring" symbol in the "Growing Up" theme should have been deleted

  Scenario: Delete Symbol Used as Symbolic Item for Opposition Value
    Given a symbol named "Ring" has been created in the "Growing Up" theme
    And a value web named "Love" has been created in the "Growing Up" theme
    And the "Ring" symbol in the "Growing Up" theme has been added as a symbolic item to the opposition value in the "Love" value web
    When the "Ring" symbol in the "Growing Up" theme is deleted
    Then the "Ring" symbol in the "Growing Up" theme should have been deleted
    And all symbolic items for the "Ring" symbol in the "Growing Up" theme should have been removed from all opposition values