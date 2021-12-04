Feature: Scene Outline

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Scenario: Outline with Initial Story Event
    When I view the "Big Battle" scene outline
    Then the "Big Battle" story event should be listed in the "Big Battle" scene outline

  Scenario: Add Another Story Event to Scene Outline
    Given I have created a story event named "Something happens"
    And I am viewing the "Big Battle" scene outline
    When I add the "Something happens" story event to the "Big Battle" scene outline
    Then the "Something happens" story event should be listed in the "Big Battle" scene outline
    And the "Big Battle" scene outline should not list a story event named "Big Battle"

  Scenario: Remove Story Event from Scene Outline
    Given I am viewing the "Big Battle" scene outline
    When I remove the "Big Battle" story event from the "Big Battle" scene outline
    Then the "Big Battle" story event should not be listed in the "Big Battle" scene outline

  Scenario: Delete a Covered Story Event
    Given I am viewing the "Big Battle" scene outline
    When I delete the "Big Battle" story event
    Then the "Big Battle" scene outline should not list a story event named "Big Battle"