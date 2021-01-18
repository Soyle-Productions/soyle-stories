Feature: Track Character Motivation in Scene

  Background:
    Given I have started a project

  Scenario: Delete Scene with Dependent Scene
    Given I have created the following scenes
      | Big Battle | SmallConflict |
    And I have created a character named "Bob"
    And I have set the following motivations in the following scenes for the following characters
      |           | Big Battle | Small Conflict |
      | Bob       | motivation | inherit        |
    When I delete the "Big Battle" scene
    Then the "Small Conflict" scene should not have a motivation for the "Bob" character anymore

  Scenario: Delete Scene with Dependent Scene and Back-Up Scene
    Given I have created the following scenes
      | Giant War   | Big Battle  | Small Conflict |
    And I have created a character named "Bob"
    And I have set the following motivations in the following scenes for the following characters
      |           | Giant War   | Big Battle  | Small Conflict |
      | Bob       | motivation1 | motivation2 | inherit        |
    When I delete the "Big Battle" scene
    Then the "Small Conflict" scene should have "motivation1" as the "Bob" character's inherited motivation