Feature: Delete Location

  Background:
    Given A project has been opened
    And A location has been created

  Scenario: Populated Location List
    Given The Location List Tool has been opened
    And the location right-click menu is open
    When the user clicks the location list tool right-click menu delete button
    Then the confirm delete location dialog should be opened

  Scenario: Populated Location List + button
    Given The Location List Tool has been opened
    And a location has been selected
    When the user clicks the location list tool delete button
    Then the confirm delete location dialog should be opened

  Scenario: Confirm Delete Dialog
    Given the delete location dialog has been opened
    When the user clicks the confirm delete location dialog delete button
    Then the delete location dialog should be closed

  Scenario: Cancel Delete Dialog
    Given the delete location dialog has been opened
    When the user clicks the confirm delete location dialog cancel button
    Then the delete location dialog should be closed
