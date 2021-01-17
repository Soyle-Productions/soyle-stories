Feature: Remove Mention of Removed Story Element
  Walter wants to remove the mention of a removed story element from a scene's prose
  because it's no longer relevant now that the story element has been removed from the story

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Scenario Outline: Story Element Mention can be Removed
    Given I have created a <element> named <name>
    And I have mentioned the <element> <name> in the "Big Battle" scene's prose
    And I have removed the <element> <name> from the story
    And I am editing the "Big Battle" scene's prose
    When I investigate the <name> mention in the "Big Battle" scene's prose
    Then I should be able to remove the <name> mention from the "Big Battle" scene's prose

    Examples:
      | element   | name   |
      | character | "Bob"  |
      | location  | "Home" |

  Scenario Outline: Remove Story Element Mention
    Given I have created a <element> named <name>
    And I have mentioned the <element> <name> in the "Big Battle" scene's prose
    And I have removed the <element> <name> from the story
    And I am editing the "Big Battle" scene's prose
    And I am investigating the <name> mention in the "Big Battle" scene's prose
    When I remove the <name> mention from the "Big Battle" scene's prose
    Then the <name> mention should not be in the "Big Battle" scene's prose
    And the "Big Battle" scene's prose should not contain text for <name>

    Examples:
      | element   | name   |
      | character | "Bob"  |
      | location  | "Home" |