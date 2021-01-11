Feature: Clear Mention of Removed Story Element
  Walter wants to clear the mention from the text of a removed story element
  because it's no longer relevant now that the story element has been removed from the story

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Scenario Outline: Story Element Mention can be Cleared
    Given I have created a <element> named <name>
    And I have mentioned the <element> <name> in the "Big Battle" scene
    And I have removed the <element> <name> from the story
    And I am editing the "Big Battle" scene's prose
    When I investigate the <name> mention in the "Big Battle" scene's prose
    Then I should be able to clear the <name> mention in the "Big Battle" scene's prose

    Examples:
      | element   | name   |
      | character | "Bob"  |
      | location  | "Home" |

  Scenario Outline: Clear Story Element Mention
    Given I have created a <element> named <name>
    And I have mentioned the <element> <name> in the "Big Battle" scene
    And I have removed the <element> <name> from the story
    And I am editing the "Big Battle" scene's prose
    And I am investigating the <name> mention in the "Big Battle" scene's prose
    When I clear the <name> mention in the "Big Battle" scene's prose
    Then the <name> mention should not be in the "Big Battle" scene's prose
    But the text previously covered by the <name> mention in the "Big Battle" scene's prose should not be removed

    Examples:
      | element   | name   |
      | character | "Bob"  |
      | location  | "Home" |