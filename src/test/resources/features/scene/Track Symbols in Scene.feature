Feature: Track Symbols in Scene
  Walter wants a symbol he's mentioned in a scene's prose to be tracked in that scene so he can quickly see which symbols are being used.

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Rule: Mentioning a symbol in prose should automatically use that symbol in the scene

    Scenario: Mention a Symbol
      Given I have created the following themes and symbols
        | Growing Up | Transformation | Becoming a Leader |
        | Flower     | Butterfly      | A Border          |
      And I have requested story elements that match "B" for the "Big Battle" scene
      When I select "Butterfly" from the list of matching story elements for the "Big Battle" scene
      Then I should see "Butterfly" mentioned in the "Big Battle" scene's prose
      And the "Butterfly" symbol for the "Transformation" theme should be tracked in the "Big Battle" scene

  Rule: Removing the last mention of a symbol should automatically remove that symbol from the scene

    Scenario: Delete Mention of Symbol
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      When I remove the "Ring" mention from the "Big Battle" scene's prose
      Then the "Ring" mention should not be in the "Big Battle" scene's prose
      And the "Ring" symbol from the "Growing Up" theme should not be tracked in the "Big Battle" scene

    Scenario: Delete Mention of Symbol with another mention remaining
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose again
      When I remove the "Ring" mention from the "Big Battle" scene's prose
      Then the "Ring" symbol for the "Growing Up" theme should be tracked in the "Big Battle" scene

  Rule: Previously used symbols in the scene should be prioritized when requesting symbols to mention.

    Scenario: Request Symbols to Mention Before Being Used
      Given I have created the following themes and symbols
        | Growing Up | Transformation | Becoming a Leader |
        | Flower     | Butterfly      | A Border          |
      And I am editing the "Big Battle" scene's prose
      When I request story elements that match "B" for the "Big Battle" scene
      Then I should see the following matching story elements for the "Big Battle" scene in this order
        | Element Name | Element Type |
        | Butterfly    | symbol       |
        | A Border     | symbol       |

    Scenario: Request Symbols to Mention After Being Used
      Given I have created the following themes and symbols
        | Growing Up | Transformation | Becoming a Leader |
        | Flower     | Butterfly      | A Border          |
      And I have mentioned the "A Border" symbol from the "Becoming a Leader" theme in the "Big Battle" scene's prose
      And I am editing the "Big Battle" scene's prose
      When I request story elements that match "B" for the "Big Battle" scene
      Then I should see the following matching story elements for the "Big Battle" scene in this order
        | Element Name | Element Type |
        | A Border     | symbol       |
        | Butterfly    | symbol       |

  Rule: Tracked Symbols should be renamed when the symbol is renamed

    Scenario: Rename Tracked Symbol
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      When I rename the symbol "Ring" in the "Growing Up" theme to "Cube"
      Then the "Cube" symbol for the "Growing Up" theme should be tracked in the "Big Battle" scene

  Rule: Deleted Symbols should no longer be tracked

    Scenario: Delete symbol only mentioned once
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      When I remove the "Ring" symbol from the "Growing Up" theme
      Then the "Ring" symbol from the "Growing Up" theme should not be tracked in the "Big Battle" scene

    Scenario: Delete symbol mentioned more than once
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose again
      When I remove the "Ring" symbol from the "Growing Up" theme
      Then the "Ring" symbol for the "Growing Up" theme should not be tracked in the "Big Battle" scene