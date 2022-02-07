Feature: Track Character Desire in Scene

  Background:
    Given I have started a project
    * I have created a scene named "Big Battle"
    * I have created a character named "Bob"
    * I have explicitly included the "Bob" character in the "Big Battle" scene

  Scenario: Character Initially Added
    Then the "Bob" character should not have a desire in the "Big Battle" scene

  Scenario: Set Character's Desire
    When I set the "Bob" character's desire to "Get dat bread" in the "Big Battle" scene
    Then the "Bob" character's desire in the "Big Battle" scene should be "Get dat bread"