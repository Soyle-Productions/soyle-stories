Feature: Character Role in Scene
  Characters can be either the inciting character, an opponent to that inciting character (even if they're allies in the
  overall story), or simply be "there" and not take an active role.

  Background:
    Given I have started a project
    * I have created a scene named "Big Battle"
    * I have created a character named "Bob"
    * I have explicitly included the "Bob" character in the "Big Battle" scene

  Scenario: Character Initially Added
    Then the "Bob" character should not have a role in the "Big Battle" scene

  Scenario: Assign New Role
    When I assign the "Bob" character the "Inciting Character" role in the "Big Battle" scene
    Then the "Bob" character should have the "Inciting Character" role in the "Big Battle" scene

  Rule: A Character can only have one role in a scene

    Scenario: Assign Character a Different Role in a Scene
      Given I have assigned the "Bob" character the "Inciting Character" role in the "Big Battle" scene
      When I assign the "Bob" character the "Opponent" role in the "Big Battle" scene
      Then the "Bob" character should have the "Opponent" role in the "Big Battle" scene
      But the "Bob" character should not have the "Inciting Character" role in the "Big Battle" scene

  Rule: There can be only one inciting character per scene

    Scenario: Assign Other Character as Inciting Character
      Given I have created a character named "Frank"
      And I have explicitly included the "Frank" character in the "Big Battle" scene
      And I have assigned the "Bob" character the "Inciting Character" role in the "Big Battle" scene
      When I assign the "Frank" character the "Inciting Character" role in the "Big Battle" scene
      Then the "Bob" character should not have a role in the "Big Battle" scene
      And the "Frank" character should have the "Inciting Character" role in the "Big Battle" scene
