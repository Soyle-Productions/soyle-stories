Feature: Remove a Story Event from the Story

  Background:
    Given I have started a project
    And I have created a story event named "Something Happens"

  Scenario: Request to Remove a Story Event
    When I want to remove the "Something Happens" story event from the story
    Then I should be prompted to confirm removing the "Something Happens" story event from the story
    But the "Something Happens" story event should not have been removed from the story

  Scenario: Immediately Remove Story Event
    And I have requested to not be prompted to confirm removing a story event
    When I want to remove the "Something Happens" story event from the story
    Then the "Something Happens" story event should have been removed from the story

  Scenario: Confirm to Remove Story Event
    And I am removing the "Something Happens" story event from the story
    When I confirm I want to remove the "Something Happens" story event from the story
    Then the "Something Happens" story event should have been removed from the story

  Scenario: Show Ramifications of Story Event with no Dependents
    And I am removing the "Something Happens" story event from the story
    When I show the ramifications of removing the "Something Happens" story event from the story
    Then there should be no ramifications for removing the "Something Happens" story event from the story