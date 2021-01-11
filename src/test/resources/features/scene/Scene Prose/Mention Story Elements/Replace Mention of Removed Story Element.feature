Feature: Replace Mention of Removed Story Element
  Walter wants to replace a mention of a removed story element
  so that the prose of his scene still makes sense

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Rule: Can only replace elements with like-kinded elements

    Scenario: Character cannot be replaced with Locations
      Given I have created a character named "Bob"
      And I have created the following locations
        | Bomb Shelter | Bay Bridge | Golden Gate Bridge | Hobo Den |
      And I have mentioned the character "Bob" in the "Big Battle" scene
      And I have removed the character "Bob" from the story
      And I am editing the "Big Battle" scene's prose
      When I investigate the "Bob" mention in the "Big Battle" scene's prose
      Then I should not see any listed elements with which to replace "Bob" in the "Big Battle" scene's prose

    Scenario: Replace Removed Mentioned Character
      Given I have created a character named "Bob"
      And I have created the following characters
        | Brooke | Billy Bob | Frank |
      And I have mentioned the character "Bob" in the "Big Battle" scene
      And I have removed the character "Bob" from the story
      And I am editing the "Big Battle" scene's prose
      When I select "Frank" to replace the "Bob" mention in the "Big Battle" scene's prose
      Then the "Bob" mention in the "Big Battle" scene's prose should have been replaced with "Frank"
      And the "Frank" mention in the "Big Battle" scene's prose should indicate that it is a character

    Scenario: Location cannot be replaced with Characters
      Given I have created a location named "Home"
      And I have created the following characters
        | Bob | Brooke | Billy Bob | Frank |
      And I have mentioned the location "Home" in the "Big Battle" scene
      And I have removed the location "Home" from the story
      And I am editing the "Big Battle" scene's prose
      When I investigate the "Home" mention in the "Big Battle" scene's prose
      Then I should not see any listed elements with which to replace "Home" in the "Big Battle" scene's prose

    Scenario: Replace Removed Mentioned Location
      Given I have created a location named "Home"
      And I have created the following locations
        | Bomb Shelter | Bay Bridge | Golden Gate Bridge | Hobo Den |
      And I have mentioned the location "Home" in the "Big Battle" scene
      And I have removed the location "Home" from the story
      And I am editing the "Big Battle" scene's prose
      When I select "Bomb Shelter" to replace the "Home" mention in the "Big Battle" scene's prose
      Then the "Home" mention in the "Big Battle" scene's prose should have been replaced with "Bomb Shelter"
      And the "Bomb Shelter" mention in the "Big Battle" scene's prose should indicate that it is a location

  Rule: Elements in the Scene should be Suggested First

    Scenario: Characters in the Scene Should be Suggested First
      Given I have created the following characters
        | Bob | Brooke | Billy Bob | Frank | Alice | George |
      And I have included the following characters in the "Big Battle" scene
        | Frank | Alice |
      And I have mentioned the character "Bob" in the "Big Battle" scene
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
      And I have mentioned the location "Home" in the "Big Battle" scene
      And I have removed the location "Home" from the story
      And I am editing the "Big Battle" scene's prose
      When I investigate the "Home" mention in the "Big Battle" scene's prose
      Then the suggested elements with which to replace "Home" in the "Big Battle" scene's prose should be as follows
        | Bomb Shelter | Las Vegas | Bay Bridge | Golden Gate Bridge | Hobo Den |

  Rule: Can replace with a new story element

    Scenario Outline: Story Element can be replaced by New Story Element
      Given I have created a <element> named <name>
      And I have mentioned the <element> <name> in the "Big Battle" scene
      And I have removed the <element> <name> from the story
      And I am editing the "Big Battle" scene's prose
      When I investigate the <name> mention in the "Big Battle" scene's prose
      Then I should be able to create a new <element> to replace the <name> mention in the "Big Battle" scene's prose

      Examples:
        | element   | name   |
        | character | "Bob"  |
        | location  | "Home" |

    Scenario Outline: Create New Story Element to Replace Removed Mentioned Story Element
      Given I have created a <element> named <name>
      And I have mentioned the <element> <name> in the "Big Battle" scene
      And I have removed the <element> <name> from the story
      And I am editing the "Big Battle" scene's prose
      And I am investigating the <name> mention in the "Big Battle" scene's prose
      When I create a <element> named <replacement name> to replace the <name> mention in the "Big Battle" scene's prose
      Then a <element> named <replacement name> should have been created
      And the <name> mention in the "Big Battle" scene's prose should have been replaced with <replacement name>
      And the <replacement name> mention in the "Big Battle" scene's prose should indicate that it is a <element>

      Examples:
        | element   | name   | replacement name |
        | character | "Bob"  | "Frank"          |
        | location  | "Home" | "Work"           |