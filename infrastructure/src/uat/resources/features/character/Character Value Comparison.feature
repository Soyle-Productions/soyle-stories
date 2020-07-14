Feature: Character Value Comparison
  As an architect author,
  I want to compare the values of my characters
  to make sure they are substantially different from each other

  Scenario: Compare characters in Character Arc
    Given a Character called "Bob" has been created
    And a character arc called "Growing Up" has been created for "Bob"
    When the Character Value Comparison tool is opened for "Bob"s "Growing Up" character arc
    Then the "Growing Up" Character Value Comparison tool should list the character "Bob"

  Scenario: Compare characters in empty Theme
    Given a theme called "Growing Up" has been created
    When the Character Value Comparison tool is opened for the "Growing Up" theme
    Then the "Growing Up" Character Value Comparison tool should list no characters

  Scenario: Only Characters in the Theme are Compared
    Given a theme called "Growing Up" has been created
    And the following characters have been created
      | "Bob" | "Frank" | "Alice" |
    And the following characters have been included in the "Growing Up" theme
      | "Bob" | "Frank" |
    When the Character Value Comparison tool is opened for the "Growing Up" theme
    Then the "Growing Up" Character Value Comparison tool should list the following characters
      | "Bob" | "Frank" |
    But the "Growing Up" Character Value Comparison tool should not list the character "Alice"

  #Rule: Cannot include a character more than once

  Scenario: List Characters Available to Include in Theme
    Given a theme called "Growing Up" has been created
    And the following characters have been created
      | "Bob" | "Frank" | "Alice" |
    And the following characters have been included in the "Growing Up" theme
      | "Bob" | "Frank" |
    And the Character Value Comparison tool has been opened for the "Growing Up" theme
    When the author indicates they want to include a character
    Then the character "Alice" should be available to include in the "Growing Up" theme
    But the following characters should not be available to include in the "Growing Up" theme
      | "Bob" | "Frank" |

  Scenario: Include Character in Theme
    Given a theme called "Growing Up" has been created
    And a Character called "Bob" has been created
    And the Character Value Comparison tool has been opened for the "Growing Up" theme
    And the author has indicated they want to include a character
    When the character "Bob" is included in the "Growing Up" theme
    Then the "Growing Up" Character Value Comparison tool should list the character "Bob"

  Scenario: Create Character to Include
    Given a theme called "Growing Up" has been created
    And the Character Value Comparison tool has been opened for the "Growing Up" theme
    And the author has indicated they want to create a character to include in the "Growing Up" theme
    When a Character called "Bob" is created for the "Growing Up" theme
    Then the "Growing Up" Character Value Comparison tool should list the character "Bob"

  Scenario: Remove Extraneous Character
    Given a theme called "Growing Up" has been created
    And a Character called "Bob" has been created
    And the character "Bob" has been included in the "Growing Up" theme
    And the Character Value Comparison tool has been opened for the "Growing Up" theme
    When the character "Bob" is removed from the "Growing Up" theme
    Then the "Growing Up" Character Value Comparison tool should not list the character "Bob"

  Scenario: Set Character Archetype
    Given a theme called "Growing Up" has been created
    And a Character called "Bob" has been created
    And the character "Bob" has been included in the "Growing Up" theme
    And the Character Value Comparison tool has been opened for the "Growing Up" theme
    When "Trickster" is set as "Bob"s archetype in the "Growing Up" theme
    Then the "Growing Up" Character Value Comparison tool should display "Trickster" as "Bob"s archetype

  Scenario: List Opposition Values Available to Add to Character
    Given a theme called "Growing Up" has been created
    And a Character called "Bob" has been created
    And the character "Bob" has been included in the "Growing Up" theme
    And the Character Value Comparison tool has been opened for the "Growing Up" theme
    And the following value webs have been created for the "Growing Up" theme
      | "Justice" | "Greed" | "Love" |
    #TODO

    And the following characters have been created
      | "Bob" | "Frank" | "Alice" |
    And the following characters have been included in the "Growing Up" theme
      | "Bob" | "Frank" |
    And the Character Value Comparison tool has been opened for the "Growing Up" theme
    When the author indicates they want to include a character
    Then the character "Alice" should be available to include in the "Growing Up" theme
    But the following characters should not be available to include in the "Growing Up" theme
      | "Bob" | "Frank" |