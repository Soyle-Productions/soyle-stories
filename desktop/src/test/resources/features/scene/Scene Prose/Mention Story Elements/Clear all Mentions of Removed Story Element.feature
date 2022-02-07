Feature: Clear all Mentions of Removed Story Element
  Walter wants to clear all mention of a removed story element
  because it's no longer relevant now that the story element has been removed from the story

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Rule: Should have the option to clear a mention without a backing element

    Scenario Outline: Story Element Mention can be Cleared
      Given I have created a <element> named <name>
      And I have mentioned the <name> <element> in the "Big Battle" scene's prose
      And I have removed the <name> <element> from the story
      And I am editing the "Big Battle" scene's prose
      When I investigate the <name> mention in the "Big Battle" scene's prose
      Then I should be able to clear the <name> mention in the "Big Battle" scene's prose

      Examples:
        | element   | name   |
        | character | "Bob"  |
        | location  | "Home" |

    @PostAlpha
    Scenario: Symbol Mention can be Cleared
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have removed the "Ring" symbol from the "Growing Up" theme
      And I am editing the "Big Battle" scene's prose
      When I investigate the "Ring" mention in the "Big Battle" scene's prose
      Then I should be able to clear the "Ring" mention in the "Big Battle" scene's prose

  Rule: Should remove the mention from the text when cleared

    Scenario Outline: Clear Story Element Mention
      Given I have created a <element> named <name>
      And I have mentioned the <name> <element> in the "Big Battle" scene's prose
      And I have removed the <name> <element> from the story
      And I am editing the "Big Battle" scene's prose
      And I am investigating the <name> mention in the "Big Battle" scene's prose
      When I clear the <name> mention from the "Big Battle" scene's prose
      Then the <name> mention should not be in the "Big Battle" scene's prose
      But the "Big Battle" scene's prose should still contain text for <name>

      Examples:
        | element   | name   |
        | character | "Bob"  |
        | location  | "Home" |

    @PostAlpha
    Scenario: Clear Symbol Mention
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have removed the "Ring" symbol from the "Growing Up" theme
      And I am editing the "Big Battle" scene's prose
      And I am investigating the "Ring" mention in the "Big Battle" scene's prose
      When I clear the "Ring" mention from the "Big Battle" scene's prose
      Then the "Ring" mention should not be in the "Big Battle" scene's prose
      But the "Big Battle" scene's prose should still contain text for "Ring"