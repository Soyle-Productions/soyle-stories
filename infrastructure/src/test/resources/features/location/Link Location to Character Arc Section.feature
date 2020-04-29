Feature: Link Location to Character Arc Section

  Background:
    Given A project has been opened
    And a Character has been created
    And a Character Arc has been created

  Scenario: No Locations available
    Given no Locations have been created
    When the Base Story Structure Tool is opened
    Then the Character Arc Section Location dropdown in the Base Story Structure Tool should be disabled