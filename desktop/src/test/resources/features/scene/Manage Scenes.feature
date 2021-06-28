Feature: Manage Scenes
  Describes the basic CRUD actions for scenes

  Background:
    Given I have started a project

  Scenario: Create New Scene
    When I create a scene named "Big Battle"
    Then a scene named "Big Battle" should have been created

  Scenario: Rename Scene
    Given I have created a scene named "Small Conflict"
    When I rename the "Small Conflict" scene to "Big Battle"
    Then the scene originally named "Small Conflict" should have been renamed to "Big Battle"

  Scenario: Delay Delete Scene
    Given I have created a scene named "Big Battle"
    When I want to delete the "Big Battle" scene
    Then I should be prompted to confirm deleting the "Big Battle" scene
    But the "Big Battle" scene should not have been deleted

  Scenario: Immediately Delete Scene
    Given I have created a scene named "Big Battle"
    And I have requested to not be prompted to confirm deleting a scene
    When I want to delete the "Big Battle" scene
    Then the "Big Battle" scene should have been deleted

  Scenario: Confirm Delete Scene
    Given I have created a scene named "Big Battle"
    And I am deleting the "Big Battle" scene
    When I confirm I want to delete the "Big Battle" scene
    Then the "Big Battle" scene should have been deleted