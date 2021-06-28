Feature: Include Characters in Scene

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created a character named "Bob"

  Scenario: Include a Character in Scene
    When I include the "Bob" character in the "Big Battle" scene
    Then the "Bob" character should be in the "Big Battle" scene

  Scenario: Stop including a Character in a scene
    Given I have included the "Bob" character in the "Big Battle" scene
    When I remove the "Bob" character from the "Big Battle" scene
    Then the "Bob" character should not be in the "Big Battle" scene

  Scenario: Delete Character used in scene
    Given I have included the "Bob" character in the "Big Battle" scene
    And I am including characters in the "Big Battle" scene
    When I remove the "Bob" character from the story
    Then the "Big Battle" scene should not have a character named "Bob"

  Scenario: Rename Character used in scene
    Given I have included the "Bob" character in the "Big Battle" scene
    And I am including characters in the "Big Battle" scene
    When I rename the "Bob" character to "Frank"
    Then the "Big Battle" scene should not have a character named "Bob"
    And the "Frank" character should be in the "Big Battle" scene