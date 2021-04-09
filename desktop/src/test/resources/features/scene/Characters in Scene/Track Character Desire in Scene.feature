Feature: Track Character Desire in Scene

  Background:
    Given I have started a project
    * I have created a scene named "Big Battle"
    * I have created a character named "Bob"

  Rule: Characters do not have a Desire when First Added

    Scenario: Include Character
      When I include the "Bob" character in the "Big Battle" scene
      Then the "Bob" character should not have a desire in the "Big Battle" scene