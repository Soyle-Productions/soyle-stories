Feature: Track Character Motivation in Scene

  Background:
    Given a project has been started

  Scenario: Delete Scene with Dependent Scene
    Given the following scenes with motivations for characters
      |           | Big Battle | Small Conflict |
      | Bob       | motivation | inherit        |
    When "Big Battle" is deleted
    Then "Small Conflict" should not have a motivation for Bob anymore

  Scenario: Delete Scene with Dependent Scene and Back-Up Scene
    Given the following scenes with motivations for characters
      |           | Giant War   | Big Battle  | Small Conflict |
      | Bob       | motivation1 | motivation2 | inherit        |
    When "Big Battle" is deleted
    Then "Small Conflict" should have "motivation1" as Bob's inherited motivation