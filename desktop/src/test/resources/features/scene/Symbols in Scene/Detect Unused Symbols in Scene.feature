Feature: Detect Unused Symbols in Scene
  Walter wants to be informed about a pinned symbol in a scene that has not been used
  so he remembers to mention that symbol within the scene's prose.

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created a theme named "Growing Up"
    And I have created a symbol named "Ring" in the "Growing Up" theme

  Rule: Only when tracking symbols should a check for unused symbols be performed

    Scenario: Remove a Mentioned, Pinned Symbol without Tracking Symbols
      Given I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have pinned the "Ring" symbol from the "Growing Up" theme to the "Big Battle" scene
      When I remove the "Ring" mention from the "Big Battle" scene's prose
      Then the "Big Battle" scene should not indicate that it has an issue

    Scenario: Remove a Mentioned, Pinned Symbol and then Track Symbols
      Given I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have pinned the "Ring" symbol from the "Growing Up" theme to the "Big Battle" scene
      And I have removed the "Ring" mention from the "Big Battle" scene's prose
      When I track symbols in the "Big Battle" scene
      Then the "Big Battle" scene should indicate that it has an issue
      And the "Ring" symbol from the "Growing Up" theme pinned to the "Big Battle" scene should indicate it is unused

    Scenario: Remove a Mentioned, Pinned Symbol while Tracking Symbols
      Given I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have pinned the "Ring" symbol from the "Growing Up" theme to the "Big Battle" scene
      And I am tracking symbols in the "Big Battle" scene
      When I remove the "Ring" mention from the "Big Battle" scene's prose
      Then the "Big Battle" scene should indicate that it has an issue
      And the "Ring" symbol from the "Growing Up" theme pinned to the "Big Battle" scene should indicate it is unused

  Rule: Can resolve the issue by mentioning the symbol in the scene

    Scenario: Mention a Pinned Symbol and Then Track Symbols
      Given I have pinned the "Ring" symbol from the "Growing Up" theme to the "Big Battle" scene
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      When I track symbols in the "Big Battle" scene
      Then the "Big Battle" scene should not indicate that it has an issue
      And the "Ring" symbol from the "Growing Up" theme pinned to the "Big Battle" scene should not indicate it is unused

    Scenario: Mention a Pinned Symbol While Tracking Symbols
      Given I have pinned the "Ring" symbol from the "Growing Up" theme to the "Big Battle" scene
      And I am tracking symbols in the "Big Battle" scene
      When I mention the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      Then the "Big Battle" scene should not indicate that it has an issue
      And the "Ring" symbol from the "Growing Up" theme pinned to the "Big Battle" scene should not indicate it is unused

  Rule: Can resolve the issue by unpinning the symbol from the scene

    Scenario: Unpin a Pinned Symbol While Tracking Symbols
      Given I have pinned the "Ring" symbol from the "Growing Up" theme to the "Big Battle" scene
      And I am tracking symbols in the "Big Battle" scene
      When I unpin the "Ring" symbol from the "Growing Up" theme from the "Big Battle" scene
      Then the "Big Battle" scene should not indicate that it has an issue