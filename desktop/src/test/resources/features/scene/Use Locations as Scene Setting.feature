Feature: Use Locations as Scene Setting
  
  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created a location named "Home"

  Scenario: Use a Location as a Scene Setting
    When I use the "Home" location as a setting for the "Big Battle" scene
    Then the "Home" location should be a setting for the "Big Battle" scene

  Scenario: Stop using a location as a scene setting
    Given I have used the "Home" location as a setting for the "Big Battle" scene
    When I stop using the "Home" location as a setting for the "Big Battle" scene
    Then the "Home" location should not be a setting for the "Big Battle" scene