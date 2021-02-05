Feature: Remove Character from Story

  Background:
    Given a Character called "Bob" has been created

  Scenario: Remove From Theme
    Given a theme called "Growing Up" has been created
    And the character "Bob" has been included in the "Growing Up" theme
    When the character "Bob" is removed from the story
    Then the character "Bob" should not be included in the "Growing Up" theme