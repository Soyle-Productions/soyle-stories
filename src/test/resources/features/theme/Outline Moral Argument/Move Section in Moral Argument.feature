Feature: Move Section in Moral Argument

  Background:
    Given a project has been started
    And a theme named "Growing Up" has been created
    And a character named "Bob" has been created
    And the character "Bob" has been included in the "Growing Up" theme as a major character

  Scenario: List Available Character Arc Section Types to Move in Moral Argument
    When the user indicates that they want to move a section in the moral argument for "Bob" in the "Growing Up" theme
    Then all the used moral argument section types should be listed

  Scenario: Move Character Arc Section Type in Moral Argument
    Given the user has indicated they want to move a section in the moral argument for "Bob" in the "Growing Up" theme
    When a used moral argument section type is selected to be moved for "Bob" in the "Growing Up" theme
    Then the existing section with that type should be moved to the new position in the moral argument for "Bob" in the "Growing Up" theme


  
  