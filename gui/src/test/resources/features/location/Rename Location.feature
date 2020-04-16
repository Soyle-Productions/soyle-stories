Feature: Rename Location

  Background:
    Given A project has been opened
    And A location has been created
    And The Location List Tool has been opened

  Scenario: Renaming inside Location List Tool creates input box
    Given the location right-click menu is open
    When the user clicks the location list tool right-click menu Rename button
    Then the location's name should be replaced by an input box
    And the location rename input box should contain the location's name

  Scenario: Enter New Name in Input Box
    Given the location rename input box is visible
    And the user has entered a valid name
    When The user presses the Enter key
    Then the location rename input box should be replaced by the location name
    And the location name should be the new name

  Scenario: Esc Drop Down Menu Exit
    Given the location rename input box is visible
    When The user presses the Esc key
    Then the location rename input box should be replaced by the location name
    And the location name should be the original name
