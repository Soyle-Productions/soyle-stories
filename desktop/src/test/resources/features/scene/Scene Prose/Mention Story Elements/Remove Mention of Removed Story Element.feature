Feature: Remove Mention of Removed Story Element
  Walter wants to remove the mention of a removed story element from a scene's prose
  because it's no longer relevant now that the story element has been removed from the story

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Rule: Should have the option to remove a mention without a backing element

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

    Scenario: Symbol Mention can be Removed
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have removed the "Ring" symbol from the "Growing Up" theme
      And I am editing the "Big Battle" scene's prose
      When I investigate the "Ring" mention in the "Big Battle" scene's prose
      Then I should be able to remove the "Ring" mention from the "Big Battle" scene's prose

  Rule: Should Remove the Text when Mention is Removed

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

      Scenario: Remove Symbol Mention
        Given I have created a theme named "Growing Up"
        And I have created a symbol named "Ring" in the "Growing Up" theme
        And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
        And I have removed the "Ring" symbol from the "Growing Up" theme
        And I am editing the "Big Battle" scene's prose
        And I am investigating the "Ring" mention in the "Big Battle" scene's prose
        When I remove the "Ring" mention from the "Big Battle" scene's prose
        Then the "Ring" mention should not be in the "Big Battle" scene's prose
        And the "Big Battle" scene's prose should not contain text for "Ring"