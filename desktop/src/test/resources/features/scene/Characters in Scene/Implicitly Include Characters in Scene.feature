Feature: Implicitly Include Characters in Scene
#  Characters are implicitly included in a scene if a covered story event involves that character

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created a character named "Bob"

  # Implicitly Include Character

  Scenario: Involve a Character in a Covered Story Event
    When I involve the "Bob" character in the "Big Battle" story event
    Then the "Bob" character should be implicitly included in the "Big Battle" scene
    But the "Bob" character should not be explicitly included in the "Big Battle" scene

  Scenario: Cover Story Event with Involved Character
    Given I have created a story event named "Something Happens"
    And I have involved the "Bob" character in the "Something Happens" story event
    When I add the "Something Happens" story event to the "Big Battle" scene outline
    Then the "Bob" character should be implicitly included in the "Big Battle" scene
    But the "Bob" character should not be explicitly included in the "Big Battle" scene