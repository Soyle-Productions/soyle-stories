@PostAlpha
Feature: Cover Existing Sections

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

  Scenario: Can Cover New Sections from Character Arcs
    When I request which arc sections for the "Bob" character can be covered in the "Big Battle" scene
    Then all of the "Bob" character's arc sections that have not yet been covered in the "Big Battle" scene should be listed

  Scenario: Can Uncover Sections Already Covered
    When I request which arc sections for the "Bob" character can be uncovered in the "Big Battle" scene
    Then all of the "Bob" character's arc sections that have been covered in the "Big Battle" scene should be listed

  Scenario: Specify Character Arc Sections to cover in Scene
    Given I have requested which arc sections for the "Bob" character can be covered in the "Big Battle" scene
    When I cover the "Desire" section from the "Bob" character's "Transformation" character arc in the "Big Battle" scene
    Then the "Big Battle" scene should cover the "Desire" section from the "Bob" character's "Transformation" character arc

  Scenario: Remove Covered Sections
    Given I have requested which arc sections for the "Bob" character can be uncovered in the "Big Battle" scene
    When I uncover the "Desire" section from the "Bob" character's "Growing Up" character arc in the "Big Battle" scene
    Then the "Big Battle" scene should not cover the "Desire" section from the "Bob" character's "Growing Up" character arc