Feature: Edit Prose
  Walter wants to edit his scene's prose so that he can write that section of his story

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I am editing the "Big Battle" scene's prose

  Scenario: Enter text
    When I enter the following text into the "Big Battle" scene's prose
      | I'm a funky monkey from funky town\nand that's fun |
    Then the "Big Battle" scene's prose should have the following text
      | I'm a funky monkey from funky town\nand that's fun |

  Scenario Outline: Delete Text Next to a Mentioned Story Element
    Given I have created a <element> named <name>
    And I have mentioned the <name> <element> in the "Big Battle" scene's prose
    When I press the <key> key on the <direction> of the <name> mention in the "Big Battle" scene's prose
    Then the <name> mention should not be in the "Big Battle" scene's prose
    And the text previously covered by the <name> mention in the "Big Battle" scene's prose should be removed

    Examples:
      | element   | name   | direction | key       |
      | character | "Bob"  | right     | backspace |
      | character | "Bob"  | left      | delete    |
      | location  | "Home" | right     | backspace |
      | location  | "Home" | left      | delete    |