Feature: Manage Story Events in Project

  Background:
    Given I have started a project

  Scenario: Create a Story Event
    When I create a story event named "Something happens"
    Then a story event named "Something happens" should have been created
    And the "Something happens" story event should be at time 1

  Scenario: Create a Story Event and Specify when it Happens
    When I create a story event named "Something happens" at time 6
    Then a story event named "Something happens" should have been created
    And the "Something happens" story event should be at time 6

  Scenario: Create a Story Event Before Another
    Given I have created a story event named "Something happens" at time 3
    When I create a story event named "The first thing" before the "Something happens" story event
    Then a story event named "The first thing" should have been created
    And the "The first thing" story event should be at time 2

  Scenario: Create a Story Event After Another
    Given I have created a story event named "Something happens" at time 3
    When I create a story event named "And then this" after the "Something happens" story event
    Then a story event named "And then this" should have been created
    And the "And then this" story event should be at time 4

  Scenario: Create a Story Event at the Same Time as Another
    Given I have created a story event named "Something happens" at time 3
    When I create a story event named "Another thing" at the same time as the "Something happens" story event
    Then a story event named "Another thing" should have been created
    And the "Another thing" story event should be at time 3

  Scenario: Rename a Story Event
    Given I have created a story event named "Something happens" at time 3
    When I rename the "Something happens" story event to "Something else"
    Then the "Something else" story event should be at time 3
    And there should not be a story event named "Something happens"

  Scenario: Change a Story Event's Time
    Given I have created a story event named "Something happens" at time 3
    When I change the "Something happens" story event's time to 6
    Then the "Something happens" story event should be at time 6

  Scenario: Increment Multiple Story Event's Times
    Given I have created the following story events
      | Name              | Time |
      | The first one     | 2    |
      | Something happens | 3    |
      | Another thing     | 3    |
      | Something else    | 4    |
    And I have selected the following story events to increment their time
      | The first one |
      | Something happens |
      | Something else |
    When I increment the selected story events' times by 1
    Then the following story events should take place at these times
      | Name              | Time |
      | The first one     | 3    |
      | Another thing     | 3    |
      | Something happens | 4    |
      | Something else    | 5    |

  Scenario: Decrement Multiple Story Event's Times
    Given I have created the following story events
      | Name              | Time |
      | The first one     | 2    |
      | Something happens | 3    |
      | Another thing     | 3    |
      | Something else    | 4    |
    And I have selected the following story events to increment their time
      | The first one |
      | Something happens |
      | Something else |
    When I increment the selected story events' times by -2
    Then the following story events should take place at these times
      | Name              | Time |
      | The first one     | 0    |
      | Something happens | 1    |
      | Something else    | 2    |
      | Another thing     | 3    |

  Scenario: Delete a Story Event
    Given I have created a story event named "Something happens" at time 3
    When I delete the "Something happens" story event
    And there should not be a story event named "Something happens"