Feature: List All Story Events in Story

  Background:
    Given I have started a project

  Scenario: List the Story Events Before Any have been Created
    When I list all the story events in my story
    Then there should not be any story events listed in my story
    But I should be prompted to create my first story event in my story

  Scenario: List the Story Events After Creation
    Given I have created a story event named "Something Happens"
    When I list all the story events in my story
    Then the "Something Happens" story event should be listed in my story

  Rule: Story Events should be ordered by time

    Scenario: List the Story Events After Many have been Created
      Given I have created the following story events
        | Name              | Time |
        | Another thing     | 3    |
        | Something else    | 4    |
        | Something happens | 3    |
        | The first one     | 2    |
      When I list all the story events in my story
      Then all the following story events should be listed in my story in the following order
        | Name              | Time |
        | The first one     | 2    |
        | Another thing     | 3    |
        | Something happens | 3    |
        | Something else    | 4    |

  Rule: Story Event List should Update to Reflect Changes

    Background:
      Given I have listed all the story events in my story

    Scenario: Update List when New Story Event is Created
      When I create a story event named "Something Happens"
      Then the "Something Happens" story event should be listed in my story

    Scenario: Update List when Story Event is Renamed
      Given I have created a story event named "Something Happens"
      When I rename the "Something Happens" story event to "Something Different Happens"
      Then the "Something Different Happens" story event should be listed in my story
      But there should not be a story event named "Something Happens" listed in my story

    Scenario: Update List when Story Event is Moved Through Time
      Given I have created a story event named "Something Happens" at time 3
      And I have created a story event named "Something Else Happens" at time 5
      When I move the "Something Happens" story event to time 6
      Then all the following story events should be listed in my story in the following order
        | Name                   | Time |
        | Something Else Happens | 5    |
        | Something Happens      | 6    |

    Scenario: Update List when Story Event is Removed from Story
      Given I have created a story event named "Something Happens"
      And I have created a story event named "Something Else Happens"
      When I remove the "Something Happens" story event from the story
      Then there should not be a story event named "Something Happens" listed in my story

    Scenario: Update List when last Story Event is Removed from Story
      Given I have created a story event named "Something Happens"
      When I remove the "Something Happens" story event from the story
      Then there should not be a story event named "Something Happens" listed in my story
      And I should be prompted to create my first story event in my story