Feature: Read Scene Prose

  Background:
    Given a project has been started

  Scenario: Get a Scene's Prose
    Given a scene named "Big Battle" has been created
    And the "Big Battle" scene has had 5 paragraphs entered as prose
    When the user wants to read the "Big Battle" scene's prose
    Then all 5 paragraphs of the "Big Battle" scene's prose should be displayed

  Scenario: Characters mentioned
    Given a scene named "Big Battle" has been created
    And a character named "Bob" has been created
    And the "Big Battle" scene has mentioned the character Bob
    When the user wants to read the "Big Battle" scene's prose
    Then the "Big Battle" scene's prose should show Bob's name as a reference

  Scenario: Location mentioned
    Given a scene named "Big Battle" has been created
    And a location named "Home" has been created
    And the "Big Battle" scene has mentioned the location Home
    When the user wants to read the "Big Battle" scene's prose
    Then the "Big Battle" scene's prose should show Home's name as a reference