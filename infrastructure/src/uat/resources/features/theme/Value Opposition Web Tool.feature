@theme
Feature: Value Opposition Web Tool

  Background:
    Given A project has been opened
    And a Theme has been created

  @list-value-webs-in-theme
  Scenario: Open Tool Before Any Value Webs Created in Theme
    When the Value Opposition Web Tool is opened
    Then the Value Opposition Web Tool should show a special empty message

  @list-value-webs-in-theme
  Scenario: Open Tool with Value Webs Created in Theme
    Given 5 value webs have been created in the theme open in the Value Opposition Web Tool
    When the Value Opposition Web Tool is opened
    Then the Value Opposition Web Tool should list all 5 value webs

  @new @excluded
  Scenario: Right-Click on Value Web

  @rename-value-web @new @excluded
  Scenario: Rename Value Web

  @rename-value-web @new @excluded
  Scenario: Cancel Rename Value Web

  @rename-value-web @new @excluded
  Scenario: Commit Rename Value Web

  @rename-value-web @new @excluded
  Scenario: Fail to Rename Value Web

  @delete-value-web @new @excluded
  Scenario: Delete Value Web

  @create-value-web @new
  Scenario: Create Value Web
    When the Value Opposition Web Tool Create Value Web button is selected
    Then the Create Value Web Dialog should be open

  @create-value-web @new
  Scenario: React to Value Web Created
    When a value web is created for the theme open in the Value Opposition Web Tool
    Then the Value Opposition Web Tool should show the created value web

  @list-oppositions-in-value-web @new
  Scenario: Select Value Web Before Any Value Oppositions Created for Value Web
    Given a value web has been created for the theme open in the Value Opposition Web Tool
    When a value web in the Value Opposition Web Tool is selected
    Then the Value Opposition Web Tool Opposition Web should show a special empty message

  @list-oppositions-in-value-web @new
  Scenario: Select Value Web with Value Oppositions Created for Value Web
    Given a value web has been created for the theme open in the Value Opposition Web Tool
    And 5 value oppositions have been created for the value web
    When a value web in the Value Opposition Web Tool is selected
    Then the Value Opposition Web Tool Opposition Web should list all 5 value oppositions

  @add-opposition-to-value-web @new
  Scenario: Add Value Opposition to Value Web
    Given a value web has been created for the theme open in the Value Opposition Web Tool
    And the value web in the Value Opposition Web Tool has been selected
    When the Value Opposition Web Tool Create Value Opposition button is selected
    Then a new value opposition should be listed in the Value Opposition Web Tool Opposition Web
    And the value opposition name should be a text box
    And the value opposition name text box should be focused
    And the text in the value opposition name text box should be selected

  @remove-value-opposition @new @excluded
  Scenario: Remove Value Opposition from Value Web

  @rename-value-opposition @new
  Scenario: Rename Value Opposition
    Given a value web has been created for the theme open in the Value Opposition Web Tool
    And a value opposition has been created for the value web
    And the value web in the Value Opposition Web Tool has been selected
    When the value opposition name is selected
    Then the value opposition name should be a text box
    And the value opposition name text box should be focused
    And the text in the value opposition name text box should be selected

  @rename-value-opposition @new
  Scenario Outline: Cancel Rename Value Opposition
    Given a value web has been created for the theme open in the Value Opposition Web Tool
    And a value opposition has been created for the value web
    And the value web in the Value Opposition Web Tool has been selected
    And the value opposition is being renamed
    When the value opposition rename is cancelled by <action>
    Then then value opposition name should not be a text box

    Examples:
      | action          |
      | Pressing Escape |
      | Clicking Away   |

  @rename-value-opposition @new
  Scenario Outline: Commit Rename Value Opposition
    Given a value web has been created for the theme open in the Value Opposition Web Tool
    And a value opposition has been created for the value web
    And the value web in the Value Opposition Web Tool has been selected
    And the value opposition is being renamed
    And a valid value opposition name has been entered in the value opposition rename text box
    When the value opposition rename is committed by <action>
    Then then value opposition name should not be a text box
    And the value opposition should be renamed

    Examples:
      | action         |
      | Pressing Enter |
      | Clicking Away  |

  @rename-value-opposition @new
  Scenario Outline: Fail to Rename Value Opposition
    Given a value web has been created for the theme open in the Value Opposition Web Tool
    And a value opposition has been created for the value web
    And the value web in the Value Opposition Web Tool has been selected
    And the value opposition is being renamed
    And an invalid value opposition name has been entered in the value opposition rename text box
    When the value opposition rename is committed by <action>
    Then then value opposition rename text box should show an error message
    But the value opposition should not be renamed

    Examples:
      | action         |
      | Pressing Enter |
      | Clicking Away  |

  @add-symbol-to-value-web @new @excluded
  Scenario: Add Symbol to Value Opposition

  @add-symbol-to-value-web @new @excluded
  Scenario: React to Symbol Added to Value Opposition

  @delete-symbol @new @excluded
  Scenario: React to Symbol Deleted

  @rename-symbol @new @excluded
  Scenario: React to Symbol Renamed