@PostAlpha
Feature: Add Moral Section to Moral Argument

  Background:
    Given I have started a project
    And I have created a theme named "Growing Up"
    And I have created a character named "Bob"
    And I have created a character arc for the character "Bob" in the "Growing Up" theme

  Scenario: List Available Character Arc Section Types to Add to Moral Argument
    Given I am outlining the "Bob" character's "Growing Up" moral argument
    When I request which sections are available to add to the "Bob" character's "Growing Up" moral argument
    Then I should see the following options to add to the "Bob" character's "Growing Up" moral argument
      | :Name:                        | :Usability: |
      | Moral Weakness                | unused      |
      | Immoral Action                | unused      |
      | Drive                         | unused      |
      | Attack by Ally                | unused      |
      | Final Action Against Opponent | unused      |
      | Moral Decision                | unused      |
      | Moral Need                    | used        |
      | Desire                        | used        |
      | Battle                        | used        |
      | Moral Self-Revelation         | used        |

  Scenario: Add Character Arc Section Type to End of Moral Argument
    Given I am outlining the "Bob" character's "Growing Up" moral argument
    And I have requested which sections are available to add to the "Bob" character's "Growing Up" moral argument
    When I choose the "Drive" section type to add to the "Bob" character's "Growing Up" moral argument
    Then the last section of the "Bob" character's "Growing Up" moral argument should be the "Drive" section

  Scenario: Add Character Arc Section Type Between two Sections
    Given I am outlining the "Bob" character's "Growing Up" moral argument
    And I have requested which sections are available to add to the "Bob" character's "Growing Up" moral argument after the "Desire" section
    When I choose the "Drive" section type to add to the "Bob" character's "Growing Up" moral argument
    Then the section after the "Desire" section in the "Bob" character's "Growing Up" moral argument should be the "Drive" section