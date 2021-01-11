Feature: Indicate Issue with Mention

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Scenario Outline: Remove a Mentioned Story Element without Reading the Scene
    Given I have created a <element> named <name>
    And I have mentioned the <element> <name> in the "Big Battle" scene's prose
    When I remove the <element> <name> from the story
    Then the "Big Battle" scene should not indicate that it has an issue

    Examples:
      | element   | name   |
      | character | "Bob"  |
      | location  | "Home" |

  Scenario Outline: Remove a Mentioned Story Element and then Read the Scene
    Given I have created a <element> named <name>
    And I have mentioned the <element> <name> in the "Big Battle" scene's prose
    And I have removed the <element> <name> from the story
    When I edit the "Big Battle" scene's prose
    Then the <name> mention in the "Big Battle" scene's prose should indicate that it was removed
    And the "Big Battle" scene should indicate that it has an issue

    Examples:
      | element   | name   |
      | character | "Bob"  |
      | location  | "Home" |

  Scenario Outline: Remove a Mentioned Story Element While Reading Scene Prose
    Given I have created a <element> named <name>
    And I have mentioned the <element> <name> in the "Big Battle" scene's prose
    And I am editing the "Big Battle" scene's prose
    When I remove the <element> <name> from the story
    Then the <name> mention in the "Big Battle" scene's prose should indicate that it was removed
    And the "Big Battle" scene should indicate that it has an issue

    Examples:
      | element   | name   |
      | character | "Bob"  |
      | location  | "Home" |