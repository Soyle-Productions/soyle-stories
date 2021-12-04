Feature: Cover Story Event in Scene

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Scenario: Uncover Story Event
    When I uncover the "Big Battle" story event
    Then the "Big Battle" story event should not be covered by a scene

  Scenario: Remove a Scene Covering a Story Event
    When I delete the "Big Battle" scene
    Then the "Big Battle" story event should not be covered by a scene

  Rule: Story Events May Only be Covered by a Single Scene

      Example: Cover Story Event with New Scene
        Given I have created a scene named "Large Conflict"
        When I cover the "Big Battle" story event in the "Large Conflict" scene
        Then the "Big Battle" story event should be covered by the "Large Conflict" scene
        And the "Big Battle" scene should not cover a story event