Feature: Detect Inconsistencies with Scene Settings

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created a location named "Home"

  Rule: Should only detect issues with Scene Setting Locations while Mapping Scene Setting Locations

    Scenario: Delete Location used in Scene without Mapping Scene Settings
      Given I have used the "Home" location as a setting for the "Big Battle" scene
      When I remove the "Home" location from the story
      Then the "Big Battle" scene should not indicate that it has an issue

    Scenario: Delete Location used in Scene and then Map Scene Settings
      Given I have used the "Home" location as a setting for the "Big Battle" scene
      And I have removed the "Home" location from the story
      When I map the "Big Battle" scene's setting locations
      Then the "Big Battle" scene's "Home" setting should indicate that it was removed
      And the "Big Battle" scene should indicate that it has an issue

    Scenario: Delete Location used in Scene while Mapping Scene Settings
      Given I have used the "Home" location as a setting for the "Big Battle" scene
      And I am mapping the "Big Battle" scene's setting locations
      When I remove the "Home" location from the story
      Then the "Big Battle" scene's "Home" setting should indicate that it was removed
      And the "Big Battle" scene should indicate that it has an issue

    Scenario: Remove Deleted Location used in Scene
      Given I have used the "Home" location as a setting for the "Big Battle" scene
      And I am mapping the "Big Battle" scene's setting locations
      And I have removed the "Home" location from the story
      When I stop using "Home" as a setting for the "Big Battle" scene
      Then the "Big Battle" scene should not indicate that it has an issue

    Scenario: Remove Deleted Location used in Scene
      Given I have used the "Home" location as a setting for the "Big Battle" scene
      And I have created a location named "Work"
      And I am mapping the "Big Battle" scene's setting locations
      And I have removed the "Home" location from the story
      When I replace the "Home" setting in the "Big Battle" scene with the "Work" location
      Then the "Big Battle" scene should not indicate that it has an issue