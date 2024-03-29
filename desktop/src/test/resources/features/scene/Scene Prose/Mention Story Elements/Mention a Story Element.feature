Feature: Mention a Story Element
  Walter wants to mention a story element in his scene's prose
  so that when that story element is modified his scene prose will stay consistent with the rest of his story

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Scenario: No Matching Story Elements
    Given I have created the following characters
      | Bob | Brooke | Billy Bob | Frank |
    Given I have created the following locations
      | Bomb Shelter | Bay Bridge | Golden Gate Bridge | Hobo Den |
    Given I have created the following themes and symbols
      | Growing Up | Transformation | Becoming a Leader |
      | Flower     | Butterfly      | Gold              |
    And I am editing the "Big Battle" scene's prose
    When I request story elements that match "P" for the "Big Battle" scene
    Then I should not see any matching story elements for the "Big Battle" scene

  Scenario: Some Matching Story Elements
    Given I have created the following characters
      | Bob | Brooke | Billy Bob | Frank |
    And I have created the following locations
      | Bomb Shelter | Bay Bridge | Golden Gate Bridge | Hobo Den |
    Given I have created the following themes and symbols
      | Growing Up | Transformation | Becoming a Leader |
      | Flower     | Butterfly      | A Border          |
    And I am editing the "Big Battle" scene's prose
    When I request story elements that match "Bo" for the "Big Battle" scene
    Then I should see the following matching story elements for the "Big Battle" scene in this order
      | Element Name | Element Type | Addendum          |
      | Bob          | character    |                   |
      | Bomb Shelter | location     |                   |
      | A Border     | symbol       | Becoming a Leader |
      | Billy Bob    | character    |                   |
      | Hobo Den     | location     |                   |

  Scenario Outline: Mention a Story Element
    Given I have created the following characters
      | Bob | Brooke | Billy Bob | Frank |
    And I have created the following locations
      | Bomb Shelter | Bay Bridge | Golden Gate Bridge | Hobo Den |
    Given I have created the following themes and symbols
      | Growing Up | Transformation | Becoming a Leader |
      | Flower     | Butterfly      | A Border          |
    And I am editing the "Big Battle" scene's prose
    And I have requested story elements that match "B" for the "Big Battle" scene
    When I select "<name>" from the list of matching story elements for the "Big Battle" scene
    Then I should see "<name>" mentioned in the "Big Battle" scene's prose

    Examples:
      | element   | name      |
      | character | Billy Bob |
      | location  | Hobo Den  |
      | symbol    | Butterfly |

  Scenario: Mention and Include a Character
    Given I have created the following characters
      | Bob | Brooke | Billy Bob | Frank |
    And I am editing the "Big Battle" scene's prose
    And I have requested story elements that match "Bo" for the "Big Battle" scene
    When I select "Billy Bob" from the list of matching story elements to include in the "Big Battle" scene
    Then I should see "Billy Bob" mentioned in the "Big Battle" scene's prose
    And the "Billy Bob" character should be included in the "Big Battle" scene

  Scenario: Mention and Use a Location
    Given I have created the following locations
      | Bomb Shelter | Bay Bridge | Golden Gate Bridge | Hobo Den |
    And I have requested story elements that match "B" for the "Big Battle" scene
    When I select "Bay Bridge" from the list of matching story elements to use in the "Big Battle" scene
    Then I should see "Bay Bridge" mentioned in the "Big Battle" scene's prose
    And the "Bay Bridge" location should be used in the "Big Battle" scene

  Rule: All Matching Other Names for a Character Should be Listed

    Scenario: Two Characters with Matching Other Names
      Given I have created the following characters
        | Walter White | Walter White Jr. |
      And I have created the following name variants for the "Walter White" character
        | Walt | Walter | Mr. White |
      And I have created the following name variants for the "Walter White Jr." character
        | Walt | Flynn |
      And I am editing the "Big Battle" scene's prose
      When I request story elements that match "Walt" for the "Big Battle" scene
      Then I should see the following matching characters for the "Big Battle" scene in this order
        | Other Name       | Display Name     |
        | Walt             | Walter White     |
        | Walt             | Walter White Jr. |
        | Walter           | Walter White     |
        | Walter White     |                  |
        | Walter White Jr. |                  |

    Scenario: Mention A Character's Other Name
      Given I have created a character named "Bob"
      And I have created a name variant of "Bobby" for the "Bob" character
      And I am editing the "Big Battle" scene's prose
      And I have requested story elements that match "B" for the "Big Battle" scene
      When I select "Bobby" from the list of matching story elements for the "Big Battle" scene
      Then I should see "Bobby" mentioned in the "Big Battle" scene's prose

  Rule: Renaming a Mentioned Story Element should Modify all Mentions of it

    Scenario Outline: Rename a Mentioned Story Element and then Read the Scene
      Given I have created a <element> named <name>
      And I have mentioned the <element> <name> in the "Big Battle" scene's prose
      And I have renamed the <element> <name> to <new name>
      When I edit the "Big Battle" scene's prose
      Then the <name> mention in the "Big Battle" scene's prose should read <new name>

      Examples:
        | element   | name   | new name |
        | character | "Bob"  | "Frank"  |
        | location  | "Home" | "Work"   |

    Scenario Outline: Rename a Mentioned Story Element While Reading Scene Prose
      Given I have created a <element> named <name>
      And I have mentioned the <element> <name> in the "Big Battle" scene's prose
      And I am editing the "Big Battle" scene's prose
      When I rename the <element> <name> to <new name>
      Then the <name> mention in the "Big Battle" scene's prose should read <new name>

      Examples:
        | element   | name   | new name |
        | character | "Bob"  | "Frank"  |
        | location  | "Home" | "Work"   |

    Scenario: Rename a Mentioned Symbol and then Read the Scene
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have renamed the symbol "Ring" in the "Growing Up" theme to "Cube"
      When I edit the "Big Battle" scene's prose
      Then the "Ring" mention in the "Big Battle" scene's prose should read "Cube"

    Scenario: Renamed a Mentioned Symbol while Reading Scene Prose
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I am editing the "Big Battle" scene's prose
      When I rename the symbol "Ring" in the "Growing Up" theme to "Cube"
      Then the "Ring" mention in the "Big Battle" scene's prose should read "Cube"

    Scenario: Rename a Mentioned Character's Other Name and then Read the Scene
      Given I have created a character named "Bob"
      And I have created a name variant of "Bobby" for the "Bob" character
      And I have mentioned the "Bobby" name variant for the character "Bob" in the "Big Battle" scene's prose
      And I have renamed the name variant of "Bobby" for the "Bob" character to "Robert"
      When I edit the "Big Battle" scene's prose
      Then the "Bobby" mention in the "Big Battle" scene's prose should read "Robert"

    Scenario: Rename a Mentioned Character's Other Name while Reading Scene Prose
      Given I have created a character named "Bob"
      And I have created a name variant of "Bobby" for the "Bob" character
      And I have mentioned the "Bobby" name variant for the character "Bob" in the "Big Battle" scene's prose
      And I am editing the "Big Battle" scene's prose
      When I rename the name variant of "Bobby" for the "Bob" character to "Robert"
      Then the "Bobby" mention in the "Big Battle" scene's prose should read "Robert"

  Rule: Renaming a Character should not modify the Alternative name mentions

    Scenario: Rename a Character Mentioned via an Alternative Name
      Given I have created a character named "Bob"
      And I have created a name variant of "Bobby" for the "Bob" character
      And I have mentioned the "Bobby" name variant for the character "Bob" in the "Big Battle" scene's prose
      And I am editing the "Big Battle" scene's prose
      When I rename the "Bob" character to "Frank"
      Then I should see "Bobby" mentioned in the "Big Battle" scene's prose