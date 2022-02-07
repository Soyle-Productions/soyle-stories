Feature: Include Characters in Scene
#  Users will start by selecting a character to include, or creating a new character,
#  Then they will be prompted to select from the covered story events in the scene to involve the character in any of them
#  They also have the option of creating a new story event along with selecting from existing story events

#  Additionally, if a character is implicitly included in the scene via covered story events, setting the character's
#  desire, motivation, or role will explicitly include them in the scene.

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created a character named "Bob"

  # Select a Character

  Scenario: Character Not Yet Involved in Covered Story Event
    When I attempt to include a character in the "Big Battle" scene
    Then the "Bob" character should be listed to include in the "Big Battle" scene

  Scenario: Character has been Involved in Covered Story Event
    Given I have involved the "Bob" character in the "Big Battle" story event
    When I attempt to include a character in the "Big Battle" scene
    Then no characters should be listed to include in the "Big Battle" scene

  Scenario: Select a Character to Include in Scene
    Given I am attempting to include a character in the "Big Battle" scene
    When I choose the "Bob" character to include in the "Big Battle" scene
    Then the "Bob" character should be explicitly included in the "Big Battle" scene
    And I should be prompted to involve the "Bob" character in story events covered by the "Big Battle" scene

  # Create a Character

  Scenario: Create a New Character to Include in Scene
    Given I am attempting to include a character in the "Big Battle" scene
    And I have chosen to create a new character to include in the "Big Battle" scene
    When I create a character named "Frank" to include in the "Big Battle" scene
    Then the "Frank" character should be explicitly included in the "Big Battle" scene
    And I should be prompted to involve the "Frank" character in story events covered by the "Big Battle" scene

  # Select Story Event(s)

  Scenario: Character Included without Any Covered Story Events in Scene
    Given I have removed the "Big Battle" story event from the story
    And I am attempting to include a character in the "Big Battle" scene
    When I choose the "Bob" character to include in the "Big Battle" scene
    Then no story events should be listed to involve the "Bob" character from the "Big Battle" scene

  Scenario: Character Included with Covered Story Events in Scene
    Given I have created and covered the following story events in the "Big Battle" scene
      | Something happens      |
      | Something else happens |
    And I am attempting to include a character in the "Big Battle" scene
    When I choose the "Bob" character to include in the "Big Battle" scene
    Then the following story events should be listed to involve the "Bob" character from the "Big Battle" scene
      | Big Battle             |
      | Something happens      |
      | Something else happens |

  Scenario: Select Multiple Story Events to Involve Newly Included Character in Scene
    Given I have created and covered the following story events in the "Big Battle" scene
      | Something happens      |
      | Something else happens |
    And I am attempting to include a character in the "Big Battle" scene
    And I have chosen the "Bob" character to include in the "Big Battle" scene
    When I choose to involve the "Bob" character from the "Big Battle" scene in the following story events
      | Big Battle         |
      | Something happens  |
    Then the "Big Battle" story event should involve the "Bob" character
    And the "Something happens" story event should involve the "Bob" character

  # Create a Story Event

  Scenario: Create and Select Story Events to Involve Newly Included Character in Scene
    Given I am attempting to include a character in the "Big Battle" scene
    And I have chosen the "Bob" character to include in the "Big Battle" scene
    When I choose to involve the "Bob" character from the "Big Battle" scene in the following story events
      |     | Big Battle        |   |
      | new | Something happens | 5 |
    Then a story event named "Something happens" should have been created
    And the "Something happens" story event should happen at time 5
    And the "Something happens" story event should be covered by the "Big Battle" scene
    And the "Bob" character should be involved in the "Something happens" story event
    And the "Bob" character should be involved in the "Big Battle" story event

  # Implicit -> Explicit Inclusion

  Rule: Associating additional data with an involved character should include that character in the scene

    Example: Set the Role of an Involved Character in a Scene
      Given I have involved the "Bob" character in the "Big Battle" story event
      When I assign the "Bob" character the "Inciting Character" role in the "Big Battle" scene
      Then the "Bob" character should be explicitly included in the "Big Battle" scene

    Example: Set the Desire of an Involved Character in a Scene
      Given I have involved the "Bob" character in the "Big Battle" story event
      When I set the "Bob" character's desire to "Get dat bread" in the "Big Battle" scene
      Then the "Bob" character should be explicitly included in the "Big Battle" scene

    Example: Set the Motivation of an Involved Character in a Scene
      Given I have involved the "Bob" character in the "Big Battle" story event
      When I set the "Bob" character's motivation to "Get dat bread" in the "Big Battle" scene
      Then the "Bob" character should be explicitly included in the "Big Battle" scene