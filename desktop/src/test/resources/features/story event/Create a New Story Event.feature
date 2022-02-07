Feature: Create a New Story Event

  Background:
    Given I have started a project

  Scenario: Create the First Story Event without Specifying Time
    When I create a story event named "Something Happens"
    Then the "Something Happens" story event should happen at time 0

  Scenario: Create another Story Event without Specifying Time
    Given I have created a story event named "Something Happens" at time 3
    When I create a story event named "Something Else Happens"
    Then the "Something Else Happens" story event should happen at time 4

  Scenario: Create a Story Event and Specify when it Happens
    When I create a story event named "Something Happens" at time 6
    Then the "Something Happens" story event should happen at time 6

  Rule: Can Create a Story Event Relative to Another

    Background:
      Given I have created a story event named "Something Happens" at time 3

      Scenario: Create a Story Event Before Another
        When I create a story event named "First Thing" before the "Something Happens" story event
        Then the "First Thing" story event should happen at time 2

      Scenario: Create a Story Event After Another
        When I create a story event named "And then this" after the "Something Happens" story event
        Then the "And then this" story event should happen at time 4

      Scenario: Create a Story Event at the Same Time as Another
        When I create a story event named "Another thing" at the same time as the "Something Happens" story event
        Then the "Another thing" story event should happen at time 3