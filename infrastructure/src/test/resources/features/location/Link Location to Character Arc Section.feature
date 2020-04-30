Feature: Link Location to Character Arc Section

  Background:
    Given A project has been opened
    And a Character has been created
    And a Character Arc has been created

  Scenario: No Locations available
    Given no Locations have been created
    When the Base Story Structure Tool is opened
    Then the Character Arc Section Location dropdown in the Base Story Structure Tool should be disabled

  Scenario: Many Locations available
    Given at least one Location has been created
    When the Base Story Structure Tool is opened
    Then the Character Arc Section Location dropdown in the Base Story Structure Tool should not be disabled

  Scenario: Many Locations available to select
    Given at least one Location has been created
    And the Base Story Structure Tool has been opened
    When the Character Arc Section Location dropdown is clicked
    Then all Locations should be listed in the Character Arc Section Location dropdown menu