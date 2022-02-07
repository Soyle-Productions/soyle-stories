Feature: Replace Mention of Removed Story Element
  Walter wants to replace a mention of a removed story element
  so that the prose of his scene still makes sense

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Rule: Can only replace elements with like-kinded elements

    Scenario: No other Characters
      Given I have created a character named "Bob"
      And I have created the following locations
        | Bomb Shelter | Bay Bridge | Golden Gate Bridge | Hobo Den |
      And I have created the following themes and symbols
        | Growing Up | Transformation | Becoming a Leader |
        | Flower     | Butterfly      | Gold              |
      And I have mentioned the character "Bob" in the "Big Battle" scene's prose
      And I have removed the character "Bob" from the story
      And I am editing the "Big Battle" scene's prose
      When I investigate the "Bob" mention in the "Big Battle" scene's prose
      Then I should not see any listed elements with which to replace "Bob" in the "Big Battle" scene's prose

    Scenario: Replace Removed Mentioned Character
      Given I have created a character named "Bob"
      And I have created the following characters
        | Brooke | Billy Bob | Frank |
      And I have mentioned the character "Bob" in the "Big Battle" scene's prose
      And I have removed the character "Bob" from the story
      And I am editing the "Big Battle" scene's prose
      When I select "Frank" to replace the "Bob" mention in the "Big Battle" scene's prose
      Then the "Bob" mention in the "Big Battle" scene's prose should have been replaced with "Frank"

    Scenario: No other Locations
      Given I have created a location named "Home"
      And I have created the following characters
        | Bob | Brooke | Billy Bob | Frank |
      And I have created the following themes and symbols
        | Growing Up | Transformation | Becoming a Leader |
        | Flower     | Butterfly      | Gold              |
      And I have mentioned the location "Home" in the "Big Battle" scene's prose
      And I have removed the location "Home" from the story
      And I am editing the "Big Battle" scene's prose
      When I investigate the "Home" mention in the "Big Battle" scene's prose
      Then I should not see any listed elements with which to replace "Home" in the "Big Battle" scene's prose

    Scenario: Replace Removed Mentioned Location
      Given I have created a location named "Home"
      And I have created the following locations
        | Bomb Shelter | Bay Bridge | Golden Gate Bridge | Hobo Den |
      And I have mentioned the location "Home" in the "Big Battle" scene's prose
      And I have removed the location "Home" from the story
      And I am editing the "Big Battle" scene's prose
      When I select "Bomb Shelter" to replace the "Home" mention in the "Big Battle" scene's prose
      Then the "Home" mention in the "Big Battle" scene's prose should have been replaced with "Bomb Shelter"

    Scenario: No other Symbols
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have created the following characters
        | Bob | Brooke | Billy Bob | Frank |
      And I have created the following locations
        | Bomb Shelter | Bay Bridge | Golden Gate Bridge | Hobo Den |
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have removed the "Ring" symbol from the "Growing Up" theme
      And I am editing the "Big Battle" scene's prose
      When I investigate the "Ring" mention in the "Big Battle" scene's prose
      Then I should not see any listed elements with which to replace "Ring" in the "Big Battle" scene's prose

    Scenario: Replace Removed Mentioned Symbol
      Given I have created the following themes and symbols
        | Growing Up | Transformation | Becoming a Leader |
        | Flower     | Butterfly      | Gold              |
      And I have mentioned the "Flower" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have removed the "Flower" symbol from the "Growing Up" theme
      And I am editing the "Big Battle" scene's prose
      When I select "Butterfly" to replace the "Flower" mention in the "Big Battle" scene's prose
      Then the "Flower" mention in the "Big Battle" scene's prose should have been replaced with "Butterfly"

  Rule: Elements in the Scene should be Suggested First

    Scenario: Characters in the Scene Should be Suggested First
      Given I have created the following characters
        | Bob | Brooke | Billy Bob | Frank | Alice | George |
      And I have included the following characters in the "Big Battle" scene
        | Frank | Alice |
      And I have mentioned the character "Bob" in the "Big Battle" scene's prose
      And I have removed the character "Bob" from the story
      And I am editing the "Big Battle" scene's prose
      When I investigate the "Bob" mention in the "Big Battle" scene's prose
      Then the suggested elements with which to replace "Bob" in the "Big Battle" scene's prose should be as follows
        | Frank | Alice | Brooke | Billy Bob | George |

    Scenario: Locations used in the Scene Should be Suggested First
      Given I have created the following locations
        | Bomb Shelter | Bay Bridge | Golden Gate Bridge | Hobo Den | Las Vegas | Home |
      And I have used the following locations in the "Big Battle" scene
        | Bomb Shelter | Las Vegas |
      And I have mentioned the location "Home" in the "Big Battle" scene's prose
      And I have removed the location "Home" from the story
      And I am editing the "Big Battle" scene's prose
      When I investigate the "Home" mention in the "Big Battle" scene's prose
      Then the suggested elements with which to replace "Home" in the "Big Battle" scene's prose should be as follows
        | Bomb Shelter | Las Vegas | Bay Bridge | Golden Gate Bridge | Hobo Den |

    Scenario: Symbols previously mentioned should be suggested first
      Given I have created the following themes and symbols
        | Growing Up | Transformation | Becoming a Leader |
        | Flower     | Butterfly      | Gold              |
        | Mountain   | Volcano        | River             |
      And I have mentioned the following symbols in the "Big Battle" scene's prose
        | :theme:           | :symbol: |
        | Growing Up        | Flower   |
        | Transformation    | Volcano  |
        | Becoming a Leader | Gold     |
      And I have removed the "Flower" symbol from the "Growing Up" theme
      And I am editing the "Big Battle" scene's prose
      When I investigate the "Flower" mention in the "Big Battle" scene's prose
      Then the suggested elements with which to replace "Flower" in the "Big Battle" scene's prose should be as follows
        | Volcano | Gold | Butterfly | River | Mountain |

  Rule: Should have option to create a new story element to replace removed element

    Scenario Outline: Story Element can be replaced by New Story Element
      Given I have created a <element> named <name>
      And I have mentioned the <name> <element> in the "Big Battle" scene's prose
      And I have removed the <name> <element> from the story
      And I am editing the "Big Battle" scene's prose
      When I investigate the <name> mention in the "Big Battle" scene's prose
      Then I should be able to create a new <element> to replace the <name> mention in the "Big Battle" scene's prose

      Examples:
        | element   | name   |
        | character | "Bob"  |
        | location  | "Home" |

    Scenario: Symbol can be replaced by New Symbol
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have removed the "Ring" symbol from the "Growing Up" theme
      And I am editing the "Big Battle" scene's prose
      When I investigate the "Ring" mention in the "Big Battle" scene's prose
      Then I should be able to create a new symbol to replace the "Ring" mention in the "Big Battle" scene's prose

  Rule: Can replace with a new story element

    Scenario Outline: Create New Story Element to Replace Removed Mentioned Story Element
      Given I have created a <element> named <name>
      And I have mentioned the <name> <element> in the "Big Battle" scene's prose
      And I have removed the <name> <element> from the story
      And I am editing the "Big Battle" scene's prose
      And I am investigating the <name> mention in the "Big Battle" scene's prose
      When I create a <element> named <replacement name> to replace the <name> mention in the "Big Battle" scene's prose
      Then a <element> named <replacement name> should have been created
      And the <name> mention in the "Big Battle" scene's prose should have been replaced with <replacement name>

      Examples:
        | element   | name   | replacement name |
        | character | "Bob"  | "Frank"          |
        | location  | "Home" | "Work"           |

    Scenario: Create new symbol to replace removed mentioned symbol
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have removed the "Ring" symbol from the "Growing Up" theme
      And I am editing the "Big Battle" scene's prose
      When I create symbol named "Flower" in the "Growing Up" theme to replace the "Ring" mention in the "Big Battle" scene's prose
      Then a symbol named "Flower" should have been created in the "Growing Up" theme
      And the "Ring" mention in the "Big Battle" scene's prose should have been replaced with "Flower"

    Scenario: Create new symbol and theme to replace removed mentioned symbol
      Given I have created a theme named "Growing Up"
      And I have created a symbol named "Ring" in the "Growing Up" theme
      And I have mentioned the "Ring" symbol from the "Growing Up" theme in the "Big Battle" scene's prose
      And I have deleted the "Growing Up" theme
      And I am editing the "Big Battle" scene's prose
      When I create a symbol named "Flower" and a theme named "Transformation" to replace the "Ring" mention in the "Big Battle" scene's prose
      Then a theme named "Transformation" should have been created
      And a symbol named "Flower" should have been created in the "Transformation" theme
      And the "Ring" mention in the "Big Battle" scene's prose should have been replaced with "Flower"