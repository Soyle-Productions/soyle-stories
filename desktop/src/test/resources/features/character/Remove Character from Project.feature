Feature: Remove Character from Project
  Users are prompted to confirm before they remove a character from the story, unless they have elected to always remove the character
  If they choose to view the ramifications, users will see a list of potential changes before confirming or cancelling
  Finally, once the user confirms the removal, the character will be removed from the story

  Background:
    Given I have started a project
    And I have created a character named "Bob"

  Scenario: Request Confirmation Before Removing Character
    When I want to remove the "Bob" character from the story
    Then I should be prompted to confirm removing the "Bob" character from the story

  Scenario: Immediately Remove Character
    Given I have requested not to be prompted to confirm removing characters from the story
    When I want to remove the "Bob" character from the story
    Then the "Bob" character should not be in the project

  Scenario: Confirm Remove Character without Viewing Ramifications
    Given I am removing the "Bob" character from the story
    When I confirm that I want to remove the "Bob" character from the story
    Then the "Bob" character should not be in the project

  Scenario: View Ramifications
    Given I am removing the "Bob" character from the story
    When I show the ramifications of removing the "Bob" character from the story
    Then nothing should be listed as ramifications of removing the "Bob" character from the story

  Scenario: View Ramifications After Including Character in Scene
    Given I have created a scene named "Big Battle"
    And I have explicitly included the "Bob" character in the "Big Battle" scene
    And I am removing the "Bob" character from the story
    When I show the ramifications of removing the "Bob" character from the story
    Then the following should be listed as ramifications of removing the "Bob" character from the story
      | the "Bob" character in the "Big Battle" scene will no longer be in the project |

