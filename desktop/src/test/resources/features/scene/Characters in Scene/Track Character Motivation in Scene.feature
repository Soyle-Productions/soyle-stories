Feature: Track Character Motivation in Scene

  Background:
    Given I have started a project

  Scenario: Set Character Motivation in Scene
    Given I have created a scene named "Big Battle"
    And I have created a character named "Bob"
    And I have explicitly included the "Bob" character in the "Big Battle" scene
    When I set the "Bob" character's motivation to "Get dat bread" in the "Big Battle" scene
    Then the "Bob" character's motivation in the "Big Battle" scene should be "Get dat bread"

  Scenario: Set Character Motivation in Previous Scene
    Given I have created the following scenes
      | Big Battle | Small Conflict |
    And I have created a character named "Bob"
    And I have explicitly included the "Bob" character in the "Big Battle" scene
    And I have explicitly included the "Bob" character in the "Small Conflict" scene
    When I set the "Bob" character's motivation to "Get dat bread" in the "Big Battle" scene
    Then the "Bob" character's inherited motivation in the "Small Conflict" scene should be "Get dat bread"

  Scenario: Override Character Motivation from Previous Scene
    Given I have created the following scenes
      | Big Battle | Small Conflict |
    And I have created a character named "Bob"
    And I have explicitly included the "Bob" character in the "Big Battle" scene
    And I have explicitly included the "Bob" character in the "Small Conflict" scene
    And I have set the "Bob" character's motivation to "Get dat bread" in the "Big Battle" scene
    When I set the "Bob" character's motivation to "Give away bread" in the "Small Conflict" scene
    Then the "Bob" character's inherited motivation in the "Small Conflict" scene should be "Get dat bread"
    And the "Bob" character's motivation in the "Small Conflict" scene should be "Give away bread"

  Scenario: Delete Scene with Dependent Scene
    Given I have created the following scenes
      | Big Battle | Small Conflict |
    And I have created a character named "Bob"
    And I have set the following motivations in the following scenes for the following characters
      |     | Big Battle | Small Conflict |
      | Bob | motivation | inherit        |
    When I delete the "Big Battle" scene
    Then the "Small Conflict" scene should not have a motivation for the "Bob" character anymore

  Scenario: Delete Scene with Dependent Scene and Back-Up Scene
    Given I have created the following scenes
      | Giant War | Big Battle | Small Conflict |
    And I have created a character named "Bob"
    And I have set the following motivations in the following scenes for the following characters
      |     | Giant War   | Big Battle  | Small Conflict |
      | Bob | motivation1 | motivation2 | inherit        |
    When I delete the "Big Battle" scene
    Then the "Small Conflict" scene should have "motivation1" as the "Bob" character's inherited motivation

  Rule: Should Confirm Before Unknowingly Modifying a Character's Motivation

    Background:
      Given  I have created the following scenes
        | Giant War | Big Battle | Small Conflict |
      And I have created a character named "Bob"
      And I have involved the "Bob" character in the following story events
        | Big Battle | Small Conflict |

    Scenario: Delete Scene with Dependent Scene
      Given I have set the "Bob" character's motivation to "Get dat bread" in the "Big Battle" scene
      And I am deleting the "Big Battle" scene
      When I show the ramifications of deleting the "Big Battle" scene
      Then the following should be listed as ramifications of deleting the "Big Battle" scene
        | Bob will no longer have the motivation of "Get dat bread" in the Small Conflict scene |

    Scenario: Delete Scene with Dependent Scene and Back-Up Scene
      Given I have involved the "Bob" character in the "Giant War" story event
      And I have set the following motivations in the following scenes for the following characters
        |     | Giant War   | Big Battle  | Small Conflict |
        | Bob | motivation1 | motivation2 | inherit        |
      And I am deleting the "Big Battle" scene
      When I show the ramifications of deleting the "Big Battle" scene
      Then the following should be listed as ramifications of deleting the "Big Battle" scene
        | Bob will have the motivation of "motivation1" in the Small Conflict scene inherited from the Giant War scene |