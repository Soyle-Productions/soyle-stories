Feature: Inconsistent Characters in Scene Detection
#  If a character has been included in a scene, but the character has been removed from the story or all of the covered
#  story events, OR the covered story events that involve that character are themselves removed, then the character
#  will no longer be consistent with the rest of the project and the user should be warned about this.

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created a character named "Bob"

  # Detect Inconsistency

  Scenario: Include Character without a Backing Story Event
    When I explicitly include the "Bob" character in the "Big Battle" scene
    Then the "Big Battle" scene should show a warning for the "Bob" character

  Scenario: Remove a Covered Story Event with Involved Character
    Given I have explicitly included the "Bob" character in the "Big Battle" scene
    And I have involved the "Bob" character in the "Big Battle" story event
    When I remove the "Big Battle" story event from the "Big Battle" scene outline
    Then the "Big Battle" scene should show a warning for the "Bob" character

  Scenario: Remove Included Character from Story
    Given I have explicitly included the "Bob" character in the "Big Battle" scene
    When I remove the "Bob" character from the story
    Then the "Big Battle" scene should show a warning for the "Bob" character

  Scenario: Delete Covered Story Event with Involved Character
    Given I have explicitly included the "Bob" character in the "Big Battle" scene
    And I have involved the "Bob" character in the "Big Battle" story event
    When I remove the "Big Battle" story event from the story
    Then the "Big Battle" scene should show a warning for the "Bob" character

  Scenario: Remove a Character from a Covered Story Event
    Given I have explicitly included the "Bob" character in the "Big Battle" scene
    And I have involved the "Bob" character in the "Big Battle" story event
    When I stop involving the "Bob" character in the "Big Battle" story event
    Then the "Big Battle" scene should show a warning for the "Bob" character
