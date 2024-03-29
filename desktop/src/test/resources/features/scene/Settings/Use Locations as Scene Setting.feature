Feature: Use Locations as Scene Setting
  
  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created a location named "Home"

  Scenario: Use a Location as a Scene Setting
    When I use the "Home" location as a setting for the "Big Battle" scene
    Then the "Home" location should be a setting for the "Big Battle" scene
    And the "Big Battle" scene should take place at the "Home" location

  Scenario: Stop using a location as a scene setting
    Given I have used the "Home" location as a setting for the "Big Battle" scene
    When I stop using the "Home" location as a setting for the "Big Battle" scene
    Then the "Home" location should not be a setting for the "Big Battle" scene
    And the "Home" location should not host a scene named "Big Battle"

  Scenario: Rename Location used in scene
    Given I have used the "Home" location as a setting for the "Big Battle" scene
    And I am mapping the "Big Battle" scene's setting locations
    When I rename the "Home" location to "Work"
    Then the "Big Battle" scene should not have a setting named "Home"
    And the "Work" location should be a setting for the "Big Battle" scene

  Scenario: Delete Location used in scene
    Given I have used the "Home" location as a setting for the "Big Battle" scene
    And I am mapping the "Big Battle" scene's setting locations
    When I remove the "Home" location from the story
    Then the "Big Battle" scene should still have a setting named "Home"
