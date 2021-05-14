Feature: Manage Character Arcs
  
  Background:
    Given I have started a project
    And I have created a character named "Bob"

  Scenario: Create a Character Arc
    Given I am creating a character arc for the "Bob" character
    When I create a character arc named "Growing Up" for the "Bob" character
    Then I should not be creating a character arc
    And a character arc named "Growing Up" should have been created for the "Bob" character
    And a theme named "Growing Up" should have been created
    And the "Bob" character should be a major character in the "Growing Up" theme

  Scenario: Promote a Character in a Theme
    Given I have created a theme named "Growing Up"
    And I have included the "Bob" character in the "Growing Up" theme
    When I promote the "Bob" character to a major character in the "Growing Up" theme
    Then a character arc named "Growing Up" should have been created for the "Bob" character

  Scenario: Delete a Character Arc
    Given I have created a character arc named "Growing Up" for the "Bob" character
    When I delete the "Growing Up" character arc for the "Bob" character
    Then the "Bob" character should not have a "Growing Up" character arc
    And the "Bob" character should be a minor character in the "Growing Up" theme

### -- NO WAY TO DEMOTE CHARACTERS YET --
#  Scenario: Demote a Character in a Theme
#    Given I have created a theme named "Growing Up"
#    And I have included the "Bob" character in the "Growing Up" theme
#    And I have promoted the "Bob" character to a major character in the "Growing Up" theme
#    When I demote the "Bob" character to a minor character in the "Growing Up" theme
#    Then the "Bob" character should not have a "Growing Up" character arc
