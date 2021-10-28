Feature: Visualize Story Event Time Scale

  Background:
    Given I have started a project

  Scenario: View Story Point in Timeline
    Given I have created a story event named "Frank Leaves" at time 48
    When I view the "Frank Leaves" story event in the timeline
    Then I should see the "Frank Leaves" story event in the timeline

  Scenario: Create Story Event While Viewing Timeline
    Given I am viewing the project timeline
    When I create a story event named "Frank Dies" at time 8
    Then I should see the "Frank Dies" story event in the timeline

  Scenario: Delete Story Event while Viewing Timeline
    Given I have created a story event named "Frank Dies" at time 8
    And I am viewing the project timeline
    When I delete the "Frank Dies" story event
    Then there should not be a story event in the timeline named "Frank Dies"

  Scenario: Rename Story Event while Viewing Timeline
    Given I have created a story event named "Frank Dies" at time 8
    And I am viewing the project timeline
    When I rename the "Frank Dies" story event to "Frank Lives"
    Then there should not be a story event in the timeline named "Frank Dies"
    But I should see the "Frank Lives" story event in the timeline

  Scenario: Reschedule Story Event while Viewing Timeline
    Given I have created a story event named "Frank Dies" at time 3
    And I am viewing the project timeline
    When I reschedule the "Frank Dies" story event to time 6
    Then the timeline should show the "Frank Dies" story event at time 6

  Scenario: Insert Time Between Two Story Events
    Given I have created the following story events
      | Name      | Time |
      | First one | 2    |
      | Next one  | 3    |
    And I am viewing the project timeline
    When I insert 5 units of time before time unit 3
    Then the "First one" story event should still be at time 2
    But the "Next one" story event should be at time 8

