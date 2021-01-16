Feature: Cover Existing Sections

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created a character named "Bob"
    And I have included the character "Bob" in the "Big Battle" scene
    And I have created the following character arcs for the character "Bob"
      | Growing Up | Transformation | Becoming a Leader |
    And I have created 3 character arcs for the character "Bob"
    And I have covered the following character arc sections in the "Big Battle" scene for the "Bob" character
      | :arc:             | :section:  |
      | Growing Up        | Moral Need |
      | Growing Up        | Desire     |
      | Transformation    | Opponent   |
      | Becoming a Leader | Plan       |

  Scenario: Indicate intent to cover character arc sections in scene
    When I want to cover character arc sections for the character "Bob" in the "Big Battle" scene
    Then all of the "Bob" character's arcs and all their sections should be listed to cover in the "Big Battle" scene
    And all of the "Bob" character's arc sections that are covered in the "Big Battle" scene should indicate they have been covered
    And all of the "Bob" character's arc sections that are not covered in the "Big Battle" scene should not indicate they have been covered

  Scenario: Specify Character Arc Sections to cover in Scene
    Given I am covering character arc sections for the character "Bob" in the "Big Battle" scene
    When I cover the "Desire" section from the "Bob" character's "Transformation" character arc in the "Big Battle" scene
    Then the "Big Battle" scene should cover the "Desire" section from the "Bob" character's "Transformation" character arc

  Scenario: Remove Covered Sections
    When I uncover the "Desire" section from the "Bob" character's "Growing Up" character arc in the "Big Battle" scene
    Then the "Big Battle" scene should not cover the "Desire" section from the "Bob" character's "Growing Up" character arc