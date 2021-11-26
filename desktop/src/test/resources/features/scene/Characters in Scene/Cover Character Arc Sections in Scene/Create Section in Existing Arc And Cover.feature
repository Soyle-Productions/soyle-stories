@PostAlpha
Feature: Create Section in Existing Arc And Cover

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created a character named "Bob"
    And I have included the character "Bob" in the "Big Battle" scene
    And I have created the following character arcs for the character "Bob"
      | Growing Up | Transformation | Becoming a Leader |
    And I have covered the following character arc sections in the "Big Battle" scene for the "Bob" character
      | :arc:             | :section:  |
      | Growing Up        | Moral Need |
      | Growing Up        | Desire     |
      | Transformation    | Opponent   |
      | Becoming a Leader | Plan       |

  Scenario: List Available Character Arc Section Templates
    Given I am covering character arc sections for the character "Bob" in the "Big Battle" scene
    When I create a new "Drive" arc section in the "Bob" character's "Transformation" arc to cover in the "Big Battle" scene
    Then the "Big Battle" scene should cover the "Drive" section from the "Bob" character's "Transformation" character arc