Feature: Indicate Issue with Mention

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Rule: Should not detect issues if not actively editing the scene

    Scenario Outline: Remove a Mentioned Story Element without Reading the Scene
      Given I have created a <element> named <name>
      And I have mentioned the <element> <name> in the "Big Battle" scene's prose
      When I remove the <element> <name> from the story
      Then the "Big Battle" scene should not indicate that it has an issue

      Examples:
        | element   | name   |
        | character | "Bob"  |
        | location  | "Home" |

    Scenario: Remove a Mentioned Symbol without Reading the Scene
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      When I remove the "Ring" symbol from the "Growing Up" theme
      Then the "Big Battle" scene should not indicate that it has an issue

    Scenario: Remove a Theme containing a Mentioned Symbol without Reading the Scene
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      When I delete the "Growing Up" theme
      Then the "Big Battle" scene should not indicate that it has an issue

    Scenario: Remove a Character's Alternative Name without Reading the Scene
      Given I have created a character named "Bob"
      And I have created a name variant of "Bobby" for the "Bob" character
      And I have mentioned the "Bobby" name variant for the character "Bob" in the "Big Battle" scene's prose
      When I remove the "Bobby" name variant for the "Bob" character
      Then the "Big Battle" scene should not indicate that it has an issue

    Scenario: Remove a Character with a mentioned Alternative Name without Reading the Scene
      Given I have created a character named "Bob"
      And I have created a name variant of "Bobby" for the "Bob" character
      And I have mentioned the "Bobby" name variant for the character "Bob" in the "Big Battle" scene's prose
      When I remove the character "Bob" from the story
      Then the "Big Battle" scene should not indicate that it has an issue

  Rule: Should detect issues once scene is edited

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

    Scenario: Remove a Mentioned Symbol and then Read the Scene
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have removed the "Ring" symbol from the "Growing Up" theme
      When I edit the "Big Battle" scene's prose
      Then the "Big Battle" scene should indicate that it has an issue

    Scenario: Remove a Theme containing a Mentioned Symbol and then Read the Scene
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have deleted the "Growing Up" theme
      When I edit the "Big Battle" scene's prose
      Then the "Big Battle" scene should indicate that it has an issue

    Scenario: Remove a Character's Alternative Name with and then Read the Scene
      Given I have created a character named "Bob"
      And I have created a name variant of "Bobby" for the "Bob" character
      And I have mentioned the "Bobby" name variant for the character "Bob" in the "Big Battle" scene's prose
      And I have removed the "Bobby" name variant for the character "Bob"
      When I edit the "Big Battle" scene's prose
      Then the "Big Battle" scene should indicate that it has an issue

    Scenario: Remove a Character with a mentioned Alternative Name and then Read the Scene
      Given I have created a character named "Bob"
      And I have created a name variant of "Bobby" for the "Bob" character
      And I have mentioned the "Bobby" name variant for the character "Bob" in the "Big Battle" scene's prose
      And I have removed the character "Bob" from the story
      When I edit the "Big Battle" scene's prose
      Then the "Big Battle" scene should indicate that it has an issue

  Rule: Should detect issues while the scene is being edited

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

    Scenario: Remove a Mentioned Symbol While Reading Scene Prose
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I am editing the "Big Battle" scene's prose
      When I remove the "Ring" symbol from the "Growing Up" theme
      Then the "Big Battle" scene should indicate that it has an issue

    Scenario: Remove a Theme containing a Mentioned Symbol While Reading Scene Prose
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I am editing the "Big Battle" scene's prose
      When I delete the "Growing Up" theme
      Then the "Big Battle" scene should indicate that it has an issue

    Scenario: Remove a Character's Alternative Name While Reading Scene Prose
      Given I have created a character named "Bob"
      And I have created a name variant of "Bobby" for the "Bob" character
      And I have mentioned the "Bobby" name variant for the character "Bob" in the "Big Battle" scene's prose
      And I am editing the "Big Battle" scene's prose
      When I remove the "Bobby" name variant for the "Bob" character
      Then the "Big Battle" scene should indicate that it has an issue

    Scenario: Remove a Character with a mentioned Alternative Name While Reading Scene Prose
      Given I have created a character named "Bob"
      And I have created a name variant of "Bobby" for the "Bob" character
      And I have mentioned the "Bobby" name variant for the character "Bob" in the "Big Battle" scene's prose
      And I am editing the "Big Battle" scene's prose
      When I remove the character "Bob" from the story
      Then the "Big Battle" scene should indicate that it has an issue