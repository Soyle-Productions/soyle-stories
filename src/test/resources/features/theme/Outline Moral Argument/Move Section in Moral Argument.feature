Feature: Move Section in Moral Argument

  Background:
    Given a project has been started
    And a theme named "Growing Up" has been created
    And a character named "Bob" has been created
    And Bob has been included in "Growing Up" as a major character

  Scenario: List Available Character Arc Section Types to Move in Moral Argument
    When the user indicates they want to move one of Bob's "Growing Up" moral argument sections
    Then all of Bob's "Growing Up" moral argument sections should be listed

  Scenario: Move Character Arc Section Type in Moral Argument
    Given the user has indicated they want to move one of Bob's "Growing Up" moral argument sections
    When the 4th section in Bob's "Growing Up" moral argument is moved above the 2nd section
    Then the section originally in the 4th position in Bob's "Growing Up" moral argument should be in the 2nd position


  
  