Feature: Find Story Elements with Matching Text

  Background:
    Given a project has been started

  Scenario: List matches based on closeness and alphabet order
    Given the following characters have been created
      | Bob | Brooke | Billy Bob | Frank |
    And the following locations have been created
      | Bomb Shelter | Bay Bridge | Golden Gate Bridge | Hobo Den |
    And a scene named "Big Battle" has been created
    And the user has wanted to edit the "Big Battle" scene
    When the user requests the story elements matching "Bo" for the "Big Battle" scene
    Then the following story elements should be listed for the "Big Battle" scene and marked as their element type
      | Element Name | Element Type |
      | Bob          | character    |
      | Bomb Shelter | location     |
      | Billy Bob    | character    |
      | Hobo Den     | location     |

  Scenario: No matches found
    Given a scene named "Big Battle" has been created
    And the user has wanted to edit the "Big Battle" scene
    When the user requests the story elements matching "T" for the "Big Battle" scene
    Then no story elements should be listed for the "Big Battle" scene
