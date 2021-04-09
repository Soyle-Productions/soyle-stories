Feature: Track Character Desire in Scene

  Background:
    Given I have started a project
    * I have created a scene named "Big Battle"
    * I have created a character named "Bob"

  Rule: Characters do not have a Desire when First Added

    Scenario: Include Character
      When I include the "Bob" character in the "Big Battle" scene
      Then the "Bob" character should not have a desire in the "Big Battle" scene

  Rule: A Character's Desire can be Updated

    Scenario: Update Character's Desire
      Given I have included the "Bob" character in the "Big Battle" scene
      When I set the "Bob" character's desire to "Get dat bread" in the "Big Battle" scene
      Then the "Bob" character's desire in the "Big Battle" scene should be "Get dat bread"

    Scenario: Check a Character's Desire after Set
      Given I have included the "Bob" character in the "Big Battle" scene
      And I have set the "Bob" character's desire to "Get dat bread" in the "Big Battle" scene
      When I check the "Bob" character's desire in the "Big Battle" scene
      Then the "Bob" character's desire in the "Big Battle" scene should be "Get dat bread"