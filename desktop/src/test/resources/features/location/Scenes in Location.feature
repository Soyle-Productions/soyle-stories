Feature: Scenes in Location
  I want to see what events happen in a given location

  Background:
    Given I have started a project
    And I have created a location named "Work"
    And I have created a scene named "Big Battle"

  Scenario: List Available Scenes to Host
    Given I am looking at the "Work" location's details
    When I request a list of scenes to host in the "Work" location
    Then the "Big Battle" scene should be listed to be hosted in the "Work" location

  Scenario: Add Scene to Location
    Given I am looking at the "Work" location's details
    When I add the "Big Battle" scene to the "Work" location
    Then the "Big Battle" scene should take place at the "Work" location
    And the "Work" location should be a setting for the "Big Battle" scene

  Scenario: Rename a Scene that Takes Place in Location
    Given I am looking at the "Work" location's details
    And I have added the "Big Battle" scene to the "Work" location
    When I rename the "Big Battle" scene to "Small Conflict"
    Then the "Work" location should not host a scene named "Big Battle"
    And the "Small Conflict" scene should take place at the "Work" location
    And the "Work" location should be a setting for the "Small Conflict" scene

  Scenario: Delete a Scene that Take Place in Location
    Given I am looking at the "Work" location's details
    And I have added the "Big Battle" scene to the "Work" location
    When I delete the "Big Battle" scene
    Then the "Work" location should not host a scene named "Big Battle"

  Rule: Location cannot host a scene more than once

      Scenario: List Available Scenes to Host
        Given I am looking at the "Work" location's details
        And I have added the "Big Battle" scene to the "Work" location
        When I request a list of scenes to host in the "Work" location
        Then the "Big Battle" scene should not be listed to be hosted in the "Work" location
