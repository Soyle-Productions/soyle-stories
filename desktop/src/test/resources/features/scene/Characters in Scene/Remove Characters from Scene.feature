Feature: Remove Characters from Scene
#  Characters can be removed explicitly or implicitly.  The author can choose to remove any character, but, if the
#  Character is still implicitly included due to still being involved in a covered story event, the character will not
#  be fully removed from the scene yet - only the scene data associated with that character will be removed.


  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created a character named "Bob"
    And I have explicitly included the "Bob" character in the "Big Battle" scene
    And I have involved the "Bob" character in the "Big Battle" story event

  Scenario: Attempt to Remove Character while Still Involved in Story Events
    When I want to remove the "Bob" character from the "Big Battle" scene
    Then I should be warned that the following story events still involve the "Bob" character
      | Big Battle |
    And the "Bob" character should not have been removed from the "Big Battle" story event

  Scenario: Remove Character while Still Involved in Story Events
    Given I am removing the "Bob" character from the "Big Battle" scene
    When I confirm that I want to remove the "Bob" character from the "Big Battle" scene
    Then the "Bob" character should not be explicitly included in the "Big Battle" scene
    But the "Bob" character should still be implicitly included in the "Big Battle" scene
    And the "Bob" character should still be involved in the "Big Battle" story event

  Scenario: Attempt to Remove Character without Being Involved in Story Events
    Given I have stopped involving the "Bob" character in the "Big Battle" story event
    When I want to remove the "Bob" character from the "Big Battle" scene
    Then the "Bob" character should not be explicitly included in the "Big Battle" scene
    And the "Bob" character should not be implicitly included in the "Big Battle" scene

  Rule: Implicitly Included Characters should be automatically removed from a scene when they are no longer backed

    Background:
      Given I have removed the "Bob" character from the "Big Battle" scene

    Scenario: Remove a Covered Story Event with Involved Character
      When I remove the "Big Battle" story event from the "Big Battle" scene outline
      Then the "Bob" character should not be implicitly included in the "Big Battle" scene

    Scenario: Remove Included Character from Story
      When I remove the "Bob" character from the story
      Then the "Bob" character should not be implicitly included in the "Big Battle" scene

    Scenario: Delete Covered Story Event with Involved Character
      When I remove the "Big Battle" story event from the story
      Then the "Bob" character should not be implicitly included in the "Big Battle" scene

    Scenario: Remove a Character from a Covered Story Event
      When I stop involving the "Bob" character in the "Big Battle" story event
      Then the "Bob" character should not be implicitly included in the "Big Battle" scene

  Rule: Should Confirm Before Unknowingly Removing Implicitly Included Character

    Scenario Template: Show Ramifications Before Removing Character from Scene
      Given I have removed the "Bob" character from the "Big Battle" scene
      And I am <Attempted Step>
      When I show the ramifications of <Attempted Step>
      Then the following should be listed as ramifications of <Attempted Step>
        | Bob will be removed from the "Big Battle" scene |
      Examples:
        | Attempted Step |
        | removing the "Big Battle" story event from the "Big Battle" scene outline |
        | removing the "Big Battle" story event from the story |
        | removing the "Bob" character from the story          |
        | removing the "Bob" character from the "Big Battle" story event |

    Scenario Template: Ramifications when Character is Still Involved
      Given I have created a story event named "Something Happens"
      And I have involved the "Bob" character in the "Something Happens" story event
      And I have covered the "Something Happens" story event in the "Big Battle" scene
      And I am <Attempted Step>
      When I show the ramifications of <Attempted Step>
      Then the following should not be listed as ramifications of <Attempted Step>
        | the "Bob" character will be removed from the "Big Battle" scene |
      Examples:
        | Attempted Step |
        | removing the "Big Battle" story event from the "Big Battle" scene outline |
        | removing the "Big Battle" story event from the story |
        | removing the "Bob" character from the "Big Battle" story event |