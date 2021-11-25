@PostAlpha
Feature: Pin Symbols in Scene
  Walter wants to pin a tracked symbol to a scene so he can note it for use later.

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created a theme named "Growing Up"
    And I have created a symbol named "Ring" in the "Growing Up" theme

  Rule: Can Pin a Symbol to a Scene

    Scenario: Pin a Symbol that has not been mentioned yet
      Given I am tracking symbols in the "Big Battle" scene
      When I pin the "Ring" symbol from the "Growing Up" theme to the "Big Battle" scene
      Then the "Ring" symbol for the "Growing Up" theme should be tracked in the "Big Battle" scene
      And the "Ring" symbol from the "Growing Up" theme should be pinned to the "Big Battle" scene

    Scenario: Pin a Symbol that has been mentioned already
      Given I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      When I pin the "Ring" symbol from the "Growing Up" theme to the "Big Battle" scene
      Then the "Ring" symbol from the "Growing Up" theme should be pinned to the "Big Battle" scene

    Scenario: Create a new symbol to be pinned
      Given I am tracking symbols in the "Big Battle" scene
      When I create a new symbol named "Cube" in the "Growing Up" theme to be pinned in the "Big Battle" scene
      Then the "Cube" symbol for the "Growing Up" theme should be tracked in the "Big Battle" scene
      And the "Cube" symbol from the "Growing Up" theme should be pinned to the "Big Battle" scene

    Scenario: Create a new theme and symbol to be pinned
      Given I am tracking symbols in the "Big Battle" scene
      When I create a new theme named "Transformation" and a new symbol named "Cube" to be pinned in the "Big Battle" scene
      Then the "Cube" symbol for the "Transformation" theme should be tracked in the "Big Battle" scene
      And the "Cube" symbol from the "Transformation" theme should be pinned to the "Big Battle" scene

  Rule: Pinned Symbols are not removed when the last mention is removed

    Scenario: Remove last mention of pinned symbol
      Given I am tracking symbols in the "Big Battle" scene
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have pinned the "Ring" symbol from the "Growing Up" theme to the "Big Battle" scene
      When I remove the "Ring" mention from the "Big Battle" scene's prose
      Then the "Ring" mention should not be in the "Big Battle" scene's prose
      But the "Ring" symbol for the "Growing Up" theme should be tracked in the "Big Battle" scene

    Scenario: Unpin symbol with no remaining mentions
      Given I am tracking symbols in the "Big Battle" scene
      And I have pinned the "Ring" symbol from the "Growing Up" theme to the "Big Battle" scene
      When I unpin the "Ring" symbol from the "Growing Up" theme from the "Big Battle" scene
      Then the "Ring" symbol from the "Growing Up" theme should not be tracked in the "Big Battle" scene

    Scenario: Unpin symbol with remaining mentions
      Given I am tracking symbols in the "Big Battle" scene
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have pinned the "Ring" symbol from the "Growing Up" theme to the "Big Battle" scene
      When I unpin the "Ring" symbol from the "Growing Up" theme from the "Big Battle" scene
      Then the "Ring" symbol for the "Growing Up" theme should be tracked in the "Big Battle" scene
      But the "Ring" symbol from the "Growing Up" theme should not be pinned to the "Big Battle" scene