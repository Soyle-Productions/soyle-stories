Feature: Visualize Story Event Time Scale

  Background:
    Given I have started a project

  Scenario: View the Story Event Timeline Before Any have been Created
    When I view the story event timeline for my story
    Then there should not be any story events shown in the timeline for my story

  Scenario: View the Story Event Timeline After Creation
    Given I have created a story event named "Something Happens"
    When I view the story event timeline for my story
    Then I should see the "Something Happens" story event in the timeline for my story

  Scenario: View Specific Story Event in Timeline
    Given I have created a story event named "Frank Leaves" at time 48
    When I view the "Frank Leaves" story event in the timeline for my story
    Then I should see the "Frank Leaves" story event in the timeline for my story
    And the "Frank Leaves" story event should be focused in the timeline for my story

  Rule: Story Event Timeline should Update to Reflect Changes

    Background:
      Given I am viewing the story event timeline for my story

    Scenario: Create Story Event While Viewing Timeline
      When I create a story event named "Frank Dies" at time 8
      Then I should see the "Frank Dies" story event in the timeline for my story

    Scenario: Delete Story Event while Viewing Timeline
      Given I have created a story event named "Frank Dies" at time 8
      When I remove the "Frank Dies" story event from the story
      Then I should not see a story event named "Frank Dies" in the timeline for my story

    Scenario: Rename Story Event while Viewing Timeline
      Given I have created a story event named "Frank Dies" at time 8
      When I rename the "Frank Dies" story event to "Frank Lives"
      Then I should not see a story event named "Frank Dies" in the timeline for my story
      But I should see the "Frank Lives" story event in the timeline for my story

    Scenario: Reschedule Story Event while Viewing Timeline
      Given I have created a story event named "Frank Dies" at time 3
      When I move the "Frank Dies" story event to time 6
      Then I should see the "Frank Dies" story event at time 6 in the timeline for my story

