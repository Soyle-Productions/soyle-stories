Feature: Redescribe Location

  Background:
    Given A project has been opened
    And A location has been created
    And The Location List Tool has been opened

  Scenario: Open Location Details Tool through Location List Tool
    Given the location right-click menu is open
    When the user clicks the location list tool right-click menu open button
    Then the Location Details Tool should be open

  Scenario Outline: Location Details Tool is reopened after editing the description
    Given the Location Details Tool has been opened
    And the target Location has a description of <set description>
    And the user has entered <changed description> into the description field
    When the user closes the Location Details Tool
    And the Location Details Tool is reopened with the same Location
    Then the description field text should be <changed description>

    Examples:
    | set description | changed description |
    | "blah"          | "new description"   |
    | "blah"          | "something else"    |
    | "blah"          | "man, idk."         |
