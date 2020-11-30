Feature: Manage Scenes
  Describes the basic CRUD actions for scenes

  Background:
    Given a project has been started

  Scenario: Create New Scene
    When a scene is created with the name "Big Battle"
    Then a scene named "Big Battle" should have been created
    And prose for "Big Battle" should have been created

  Scenario: Rename Scene
    Given a scene named "Small Conflict" has been created
    When "Small Conflict" is renamed with the name "Big Battle"
    Then the scene originally named "Small Conflict" should have been renamed to "Big Battle"

  Scenario: Delay Delete Scene
    Given a scene named "Big Battle" has been created
    When the user wants to delete "Big Battle"
    Then a delete scene confirmation message should be shown
    But the "Big Battle" scene should not have been deleted

  Scenario: Immediately Delete Scene
    Given a scene named "Big Battle" has been created
    And the user has requested that a delete scene confirmation message not be shown
    When the user wants to delete "Big Battle"
    Then the "Big Battle" scene should have been deleted

  Scenario: Confirm Delete Scene
    Given a scene named "Big Battle" has been created
    And the user wanted to delete "Big Battle"
    When the user confirms they want to delete "Big Battle"
    Then the "Big Battle" scene should have been deleted

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
    Then "Small Conflict" should have "motivation1" as Bob's motivation