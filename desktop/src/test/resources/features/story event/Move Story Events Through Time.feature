Feature: Move Story Events Through Time

  Background:
    Given I have started a project

  Scenario: Change a Story Event's Time
    Given I have created a story event named "Something happens" at time 3
    When I move the "Something happens" story event to time 6
    Then the "Something happens" story event should happen at time 6

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

  Scenario: Insert Time Between Two Story Events
    Given I have created the following story events
      | Name      | Time |
      | First one | 2    |
      | Next one  | 3    |
    When I insert 5 units of time before time unit 3
    Then the "First one" story event should still be at time 2
    But the "Next one" story event should be at time 8