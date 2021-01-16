Feature: Read Scene Prose
  Walter wants to read the prose of his scene so he can see what needs to be changed

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Scenario: Get a Scene's Prose
    Given my "Big Battle" scene has 5 paragraphs of text in its prose
    When I read the "Big Battle" scene's prose
    Then I should see all 5 paragraphs of the "Big Battle" scene's prose

  Scenario Outline: Read Story with Mentioned Story Element
    Given I have created a <element> named <name>
    And I have mentioned the <element> <name> in the "Big Battle" scene's prose
    When I read the "Big Battle" scene's prose
    Then I should see the <element> <name> mentioned in the "Big Battle" scene's prose

    Examples:
      | element   | name |
      | character | "Bob"  |
      | location  | "Home" |