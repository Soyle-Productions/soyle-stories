@scene @reorder-scene
Feature: Reorder Scene Ramifications

  Background:
    Given 2 Scenes have been created
    And 1 Characters have been created

  Scenario Outline: No Characters included in Scene
    When the <sceneId> Reorder Scene Ramifications Tool is opened to move to index <index>
    Then the <sceneId> Reorder Scene Ramifications Tool should display an ok message

    Examples:
      | sceneId   | index |
      | "scene 1" | 2     |
      | "scene 2" | 0     |

  Scenario Outline: No motivations set in Scene
    Given 4 Characters have been created
    And all Characters have been included in all Scenes
    When the <sceneId> Reorder Scene Ramifications Tool is opened to move to index <index>
    Then the <sceneId> Reorder Scene Ramifications Tool should display an ok message

    Examples:
      | sceneId   | index |
      | "scene 1" | 2     |
      | "scene 2" | 0     |

  Scenario: Back-to-back scenes with set motivations
    Given the following Scenes
      | character   | scene 1 | scene 2 |
      | character A | value 1 | value 2 |
    When the "scene 1" Reorder Scene Ramifications Tool is opened to move to index 2
    Then the "scene 1" Reorder Scene Ramifications Tool should display an ok message

  Scenario: Motivation not set in scene currently following this scene
    Given the following Scenes
      | character   | scene 1 | scene 2 |
      | character A | value 1 | inherit |
    When the "scene 1" Reorder Scene Ramifications Tool is opened to move to index 2
    Then the scene "scene 2" should be listed in the "scene 1" Reorder Scene Ramifications Tool
    And the character "character A" should be listed for "scene 2" in the "scene 1" Reorder Scene Ramifications Tool
    And the "scene 1" Reorder Scene Ramifications Current Motivation field for "character A" in "scene 2" should show "value 1"
    And the "scene 1" Reorder Scene Ramifications Changed Motivation field for "character A" in "scene 2" should be empty

  Scenario: Motivation not set in scene now following this scene
    Given the following Scenes
      | character   | scene 1 | scene 2 |
      | character A | inherit | value 2 |
    When the "scene 2" Reorder Scene Ramifications Tool is opened to move to index 0
    Then the scene "scene 1" should be listed in the "scene 2" Reorder Scene Ramifications Tool
    And the character "character A" should be listed for "scene 1" in the "scene 2" Reorder Scene Ramifications Tool
    And the "scene 2" Reorder Scene Ramifications Current Motivation field for "character A" in "scene 1" should be empty
    And the "scene 2" Reorder Scene Ramifications Changed Motivation field for "character A" in "scene 1" should show "value 2"

  Scenario: Move behind scene with motivation set
    Given the following Scenes
      | character   | scene 1 | scene 2 |
      | character A | inherit | value 2 |
    When the "scene 1" Reorder Scene Ramifications Tool is opened to move to index 2
    Then the scene "scene 1" should be listed in the "scene 1" Reorder Scene Ramifications Tool
    And the character "character A" should be listed for "scene 1" in the "scene 1" Reorder Scene Ramifications Tool
    And the "scene 1" Reorder Scene Ramifications Current Motivation field for "character A" in "scene 1" should be empty
    And the "scene 1" Reorder Scene Ramifications Changed Motivation field for "character A" in "scene 1" should show "value 2"

  Scenario: Scene with motivation set moved in front of this scene
    Given the following Scenes
      | character   | scene 1 | scene 2 |
      | character A | value 1 | inherit |
    When the "scene 2" Reorder Scene Ramifications Tool is opened to move to index 0
    Then the scene "scene 2" should be listed in the "scene 2" Reorder Scene Ramifications Tool
    And the character "character A" should be listed for "scene 2" in the "scene 2" Reorder Scene Ramifications Tool
    And the "scene 2" Reorder Scene Ramifications Current Motivation field for "character A" in "scene 2" should show "value 1"
    And the "scene 2" Reorder Scene Ramifications Changed Motivation field for "character A" in "scene 2" should be empty

  @delete-scene
  Scenario: React to Scene Deleted
    Given the following Scenes
      | character   | scene 1 | scene 2 | scene 3 |
      | character A | value1a | inherit | inherit |
    And the "scene 1" Reorder Scene Ramifications Tool has been opened to move to index 2
    When "scene 2" is deleted
    Then the scene "scene 2" should not be listed in the "scene 1" Reorder Scene Ramifications Tool

  @delete-character
  Scenario: React to Character Deleted
    Given the following Scenes
      | character   | scene 1 | scene 2 |
      | character A | value1a | inherit |
      | character B | value2a | inherit |
    And the "scene 1" Reorder Scene Ramifications Tool has been opened to move to index 2
    When the Character "character A" is deleted
    Then the character "character A" should not be listed in the "scene 1" Reorder Scene Ramifications Tool

  @delete-character
  Scenario: React to Last Affected Character in Scene Removed
    Given the following Scenes
      | character   | scene 1 | scene 2 | scene 3 |
      | character A | value1a | inherit | inherit |
      | character B | value1a | -       | inherit |
    And the "scene 1" Reorder Scene Ramifications Tool has been opened to move to index 2
    When the Character "character A" is deleted
    Then the scene "scene 2" should not be listed in the "scene 1" Reorder Scene Ramifications Tool

  @delete-scene
  Scenario: React to Last Affected Scene Removed
    Given the following Scenes
      | character   | scene 1 | scene 2 |
      | character A | value1  | inherit |
    And the "scene 1" Reorder Scene Ramifications Tool has been opened to move to index 2
    When the Character "character A" is deleted
    Then the "scene 1" Reorder Scene Ramifications Tool should display an ok message

  @set-character-motivation-in-scene
  Scenario: React to Character Motivation being cleared
    Given the following Scenes
      | character   | scene 1 | scene 2 | scene 3 |
      | character A | value1  | value2  | inherit |
    And the "scene 1" Reorder Scene Ramifications Tool has been opened to move to index 2
    When the Character Motivation for "character A" is cleared in "scene 2"
    Then the scene "scene 3" should not be listed in the "scene 1" Reorder Scene Ramifications Tool

  @set-character-motivation-in-scene
  Scenario: React to Character Motivation being set
    Given the following Scenes
      | character   | scene 1 | scene 2 | scene 3 |
      | character A | value1  | inherit | inherit |
    And the "scene 1" Reorder Scene Ramifications Tool has been opened to move to index 2
    When the Character Motivation for "character A" is set in "scene 2"
    Then the "scene 1" Reorder Scene Ramifications Current Motivation field for "character A" in "scene 3" should show "new value"
    And the "scene 1" Reorder Scene Ramifications Changed Motivation field for "character A" in "scene 3" should show "value1"

  Scenario: Commit Reorder Scene
    Given the "scene 1" Reorder Scene Ramifications Tool has been opened to move to index 2
    When the "scene 1" Reorder Scene Ramifications Tool "Reorder" button is selected
    Then the Scene should be reordered
    And the "scene 1" Reorder Scene Ramifications Tool should be closed

  Scenario: Cancel Reorder Scene
    Given the "scene 1" Reorder Scene Ramifications Tool has been opened to move to index 2
    When the "scene 1" Reorder Scene Ramifications Tool "Cancel" button is selected
    And the "scene 1" Reorder Scene Ramifications Tool should be closed
    But the Scene should not be reordered
