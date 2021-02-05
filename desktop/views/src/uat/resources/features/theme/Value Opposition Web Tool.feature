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

  @rename-value-web
  Scenario: Rename Value Web
    Given the Value Opposition Web Tool has been opened
    And a value web has been created for the theme open in the Value Opposition Web Tool
    And the value web in the Value Opposition Web Tool has been selected
    When the value web name is selected
    Then the value web name should be a text box
    And the value web name text box should be focused
    And the text in the value web name text box should be selected

  @rename-value-web
  Scenario: Cancel Rename Value Web
    Given the Value Opposition Web Tool has been opened
    And a value web has been created for the theme open in the Value Opposition Web Tool
    And the value web in the Value Opposition Web Tool has been selected
    And the value web is being renamed
    When the value web rename is cancelled by Pressing Escape
    Then the value web name should not be a text box

  @rename-value-web
  Scenario: Commit Rename Value Web
    Given the Value Opposition Web Tool has been opened
    And a value web has been created for the theme open in the Value Opposition Web Tool
    And the value web in the Value Opposition Web Tool has been selected
    And the value web is being renamed
    And a valid value web name has been entered in the value web rename text box
    When the value web rename is committed by Pressing Enter
    Then the value web name should not be a text box
    And the value web should be renamed

  @rename-value-web
  Scenario: Fail to Rename Value Web
    Given the Value Opposition Web Tool has been opened
    And a value web has been created for the theme open in the Value Opposition Web Tool
    And the value web in the Value Opposition Web Tool has been selected
    And the value web is being renamed
    And an invalid value web name has been entered in the value web rename text box
    When the value web rename is committed by Pressing Enter
    Then the value web rename text box should show an error message
    But the value web should not be renamed

  @delete-value-web
  Scenario: Delete Value Web
    Given the Value Opposition Web Tool has been opened
    And a value web has been created for the theme open in the Value Opposition Web Tool
    And the value web in the Value Opposition Web Tool has been selected
    When the value web menu button "Delete" button is selected in the Value Opposition Web Tool
    Then the Confirm Delete Value Web Dialog should be open

  @delete-value-web
  Scenario: React to Value Web Deleted
    Given the Value Opposition Web Tool has been opened for the theme "Growing Up"
    And a value web called "Justice" has been created for the "Growing Up" theme
    When the "Justice" value web is deleted
    Then the "Growing Up" Value Opposition Web Tool should not list the "Justice" value web

  @delete-value-web
  Scenario: React to Selected Value Web Deleted
    Given the Value Opposition Web Tool has been opened for the theme "Growing Up"
    And a value web called "Justice" has been created for the "Growing Up" theme
    And the "Justice" value web has been selected in the "Growing Up" Value Opposition Web Tool
    When the "Justice" value web is deleted
    Then the "Growing Up" Value Opposition Web Tool should not list the "Justice" value web
    And the "Growing Up" Value Opposition Web Tool should have no value web selected

  @create-value-web
  Scenario: Create Value Web
    Given the Value Opposition Web Tool has been opened
    When the Value Opposition Web Tool Create Value Web button is selected
    Then the Create Value Web Dialog should be open

  @create-value-web
  Scenario: React to Value Web Created
    Given the Value Opposition Web Tool has been opened
    When a value web is created for the theme open in the Value Opposition Web Tool
    Then the Value Opposition Web Tool should show the created value web

  @list-oppositions-in-value-web
  Scenario: Select Value Web After All Value Oppositions Removed from Value Web
    Given the Value Opposition Web Tool has been opened for the theme "Growing Up"
    And a value web called "Justice" has been created for the "Growing Up" theme
    And all value oppositions have been removed from the "Justice" value web
    When the "Justice" value web is selected in the "Growing Up" Value Opposition Web Tool
    Then the "Growing Up" Value Opposition Web Tool Opposition Web should show a special empty message

  @list-oppositions-in-value-web
  Scenario: Select Value Web with Value Oppositions Created for Value Web
    Given the Value Opposition Web Tool has been opened
    And a value web has been created for the theme open in the Value Opposition Web Tool
    And 5 value oppositions have been created for the value web
    When a value web in the Value Opposition Web Tool is selected
    Then the Value Opposition Web Tool Opposition Web should list all 5 value oppositions

  @add-opposition-to-value-web
  Scenario: Add Value Opposition to Value Web
    Given the Value Opposition Web Tool has been opened
    And a value web has been created for the theme open in the Value Opposition Web Tool
    And the value web in the Value Opposition Web Tool has been selected
    When the Value Opposition Web Tool Create Value Opposition button is selected
    Then a new value opposition should be listed in the Value Opposition Web Tool Opposition Web
    And the value opposition name should be a text box
    And the value opposition name text box should be focused
    And the text in the value opposition name text box should be selected

  @remove-value-opposition
  Scenario: Remove Value Opposition from Value Web
    Given the Value Opposition Web Tool has been opened for the theme "Growing Up"
    And a value web called "Justice" has been created for the "Growing Up" theme
    And a value opposition has been created for the "Justice" value web
    And the "Justice" value web has been selected in the "Growing Up" Value Opposition Web Tool
    When the "Remove" button is selected on the first value opposition in the "Justice" value web
    Then the "Growing Up" Value Opposition Web Tool Opposition Web should show a special empty message

  @rename-value-opposition
  Scenario: Rename Value Opposition
    Given the Value Opposition Web Tool has been opened
    And a value web has been created for the theme open in the Value Opposition Web Tool
    And a value opposition has been created for the value web
    And the value web in the Value Opposition Web Tool has been selected
    When the value opposition name is selected
    Then the value opposition name should be a text box
    And the value opposition name text box should be focused
    And the text in the value opposition name text box should be selected

  @rename-value-opposition
  Scenario Outline: Cancel Rename Value Opposition
    Given the Value Opposition Web Tool has been opened
    And a value web has been created for the theme open in the Value Opposition Web Tool
    And a value opposition has been created for the value web
    And the value web in the Value Opposition Web Tool has been selected
    And the value opposition is being renamed
    When the value opposition rename is cancelled by <action>
    Then the value opposition name should not be a text box

    Examples:
      | action          |
      | Pressing Escape |
      | Clicking Away   |

  @rename-value-opposition
  Scenario Outline: Commit Rename Value Opposition
    Given the Value Opposition Web Tool has been opened
    And a value web has been created for the theme open in the Value Opposition Web Tool
    And a value opposition has been created for the value web
    And the value web in the Value Opposition Web Tool has been selected
    And the value opposition is being renamed
    And a valid value opposition name has been entered in the value opposition rename text box
    When the value opposition rename is committed by <action>
    Then the value opposition name should not be a text box
    And the value opposition should be renamed

    Examples:
      | action         |
      | Pressing Enter |
      | Clicking Away  |

  @rename-value-opposition
  Scenario Outline: Fail to Rename Value Opposition
    Given the Value Opposition Web Tool has been opened
    And a value web has been created for the theme open in the Value Opposition Web Tool
    And a value opposition has been created for the value web
    And the value web in the Value Opposition Web Tool has been selected
    And the value opposition is being renamed
    And an invalid value opposition name has been entered in the value opposition rename text box
    When the value opposition rename is committed by <action>
    Then the value opposition <error message location> should show an error message
    But the value opposition should not be renamed

    Examples:
      | action         | error message location |
      | Pressing Enter | rename text box        |
      | Clicking Away  | name                   |

  @add-symbol-to-value-web
  Scenario: Add Symbol to Value Opposition
    Given the Value Opposition Web Tool has been opened
    And a value web has been created for the theme open in the Value Opposition Web Tool
    And a value opposition has been created for the value web
    And the value web in the Value Opposition Web Tool has been selected
    When the value opposition "Add Symbol" button is selected
    Then the Add Symbol to Opposition Dialog should be open

  @delete-character
  Scenario: Remove Character
    Given a theme called "Growing Up" has been created
    And a value web called "Justice" has been created for the "Growing Up" theme
    And a Character called "Bob" has been created
    And the character "Bob" has been symbolically added to the "Growing Up" theme's "Justice" value web's first opposition
    When the character "Bob" is removed from the story
    Then the symbolic item "Bob" should not in the "Growing Up" theme's "Justice" value web's first opposition

  @delete-location
  Scenario: Remove Location
    Given a theme called "Growing Up" has been created
    And a value web called "Justice" has been created for the "Growing Up" theme
    And a location called "Home" has been created
    And the location "Home" has been symbolically added to the "Growing Up" theme's "Justice" value web's first opposition
    When the location "Home" is removed from the story
    Then the symbolic item "Home" should not in the "Growing Up" theme's "Justice" value web's first opposition

  @delete-symbol
  Scenario: Remove Symbol
    Given a theme called "Growing Up" has been created
    And a value web called "Justice" has been created for the "Growing Up" theme
    And a symbol called "The Ring" has been created for the "Growing Up" theme
    And the symbol "The Ring" has been symbolically added to the "Growing Up" theme's "Justice" value web's first opposition
    When the symbol "The Ring" is removed from the story
    Then the symbolic item "The Ring" should not in the "Growing Up" theme's "Justice" value web's first opposition

  @remove-symbolic-item
  Scenario: Remove Symbolic Item
    Given a theme called "Growing Up" has been created
    And a value web called "Justice" has been created for the "Growing Up" theme
    And a Character called "Bob" has been created
    And the character "Bob" has been symbolically added to the "Growing Up" theme's "Justice" value web's first opposition
    When the symbolic item "Bob" is removed from the "Growing Up" theme's "Justice" value web's first opposition
    Then the symbolic item "Bob" should not in the "Growing Up" theme's "Justice" value web's first opposition

  @rename-character
  Scenario: Rename Character
    Given a theme called "Growing Up" has been created
    And a value web called "Justice" has been created for the "Growing Up" theme
    And a Character called "Bob" has been created
    And the character "Bob" has been symbolically added to the "Growing Up" theme's "Justice" value web's first opposition
    When the character "Bob" is renamed to "Frank"
    Then the symbolic item "Bob" in the "Growing Up" theme's "Justice" value web's first opposition should be named "Frank"

  @rename-location
  Scenario: Rename Location
    Given a theme called "Growing Up" has been created
    And a value web called "Justice" has been created for the "Growing Up" theme
    And a location called "Home" has been created
    And the location "Home" has been symbolically added to the "Growing Up" theme's "Justice" value web's first opposition
    When the location "Home" is renamed to "Work"
    Then the symbolic item "Home" in the "Growing Up" theme's "Justice" value web's first opposition should be named "Work"

  @rename-symbol
  Scenario: Rename Symbol
    Given a theme called "Growing Up" has been created
    And a value web called "Justice" has been created for the "Growing Up" theme
    And a symbol called "The Ring" has been created for the "Growing Up" theme
    And the symbol "The Ring" has been symbolically added to the "Growing Up" theme's "Justice" value web's first opposition
    When the symbol "The Ring" is renamed to "A Ring"
    Then the symbolic item "The Ring" in the "Growing Up" theme's "Justice" value web's first opposition should be named "A Ring"