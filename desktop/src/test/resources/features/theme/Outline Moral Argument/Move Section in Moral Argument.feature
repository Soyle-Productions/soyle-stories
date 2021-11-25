@PostAlpha
Feature: Move Section in Moral Argument

  Background:
    Given a project has been started
    And I have created a theme named "Growing Up"
    And I have created a character named "Bob"
    And I have created a character arc for the character "Bob" in the "Growing Up" theme
    And I am outlining the "Bob" character's "Growing Up" moral argument

  Scenario: Choose Position First
    When I choose the 2nd position to move one of the "Bob" character's "Growing Up" moral argument sections
    Then all of the "Bob" character's "Growing Up" moral argument sections should be listed to be moved

  Scenario: Move to Chosen Position
    Given I have chosen the 2nd position to move one of the "Bob" character's "Growing Up" moral argument sections
    When I choose to move the "Moral Self-Revelation" section of the "Bob" character's "Growing Up" moral argument
    Then the order of the sections in the "Bob" character's "Growing Up" moral argument should be as follows
      | Moral Need | Moral Self-Revelation | Desire | Battle |

  Scenario: Move Section by First Choosing Listed Section
    When I move the "Moral Self-Revelation" section to the 2nd position of the "Bob" character's "Growing Up" moral argument
    Then the order of the sections in the "Bob" character's "Growing Up" moral argument should be as follows
      | Moral Need | Moral Self-Revelation | Desire | Battle |

