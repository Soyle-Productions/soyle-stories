@theme @create-symbol
Feature: Create Symbol Dialog

  Background:
    Given A project has been opened

  Scenario: Open Dialog with Theme
    Given a Theme has been created
    When the Create Symbol Dialog is opened with a Theme
    Then the Create Symbol Dialog Theme field should not be visible

  @create-theme @list-themes
  Scenario: Open Dialog without Theme
    Given 5 Themes have been created
    When the Create Symbol Dialog is opened without a Theme
    Then the Create Symbol Dialog Theme field should be visible
    And the Create Symbol Dialog Theme field should be a dropdown
    And the Create Symbol Dialog Theme field should list all themes
    And the Create Symbol Dialog Theme field label should say "Select Existing Theme"
    And the Create Symbol Dialog Theme toggle button should say "Create New Theme"

  @create-theme @list-themes
  Scenario: Open Dialog without Theme Before Any Themes Created
    When the Create Symbol Dialog is opened without a Theme
    Then the Create Symbol Dialog Theme field should be visible
    And the Create Symbol Dialog Theme field should be a text field
    And the Create Symbol Dialog Theme field label should say "Create New Theme"
    And the Create Symbol Dialog Theme toggle button should say "Select Existing Theme"
    But the Create Symbol Dialog Theme toggle button should be disabled

  Scenario: Create Symbol with Valid Name
    Given the Create Symbol Dialog has been opened
    And a valid Symbol Name has been entered into the Create Symbol Dialog Name field
    When the Enter key is pressed
    Then the Create Symbol Dialog should be closed
    And a new Symbol should be created with the supplied name

  Scenario: Create Symbol with Invalid Name
    Given the Create Symbol Dialog has been opened
    And an invalid Symbol Name has been entered into the Create Symbol Dialog Name field
    When the Enter key is pressed
    Then the Create Symbol Dialog should not be closed
    And the Create Symbol Dialog should show an error message for the Symbol Name field
    But a new Symbol should not be created

  Scenario: Cancel Symbol Creation
    Given the Create Symbol Dialog has been opened
    When the Esc key is pressed
    Then the Create Symbol Dialog should be closed
    But a new Symbol should not be created

  @create-theme
  Scenario: Create Symbol and Theme with Valid Theme Name
    Given the Create Symbol Dialog has been opened without a Theme
    And a valid Symbol Name has been entered into the Create Symbol Dialog Name field
    And a valid Theme Name has been entered into the Create Symbol Dialog Theme field
    When the Enter key is pressed
    Then the Create Symbol Dialog should be closed
    And a new Theme should be created with the supplied name
    And a new Symbol should be created with the supplied name in the new Theme

  @create-theme
  Scenario: Create Symbol and Theme with Invalid Theme Name
    Given the Create Symbol Dialog has been opened without a Theme
    And a valid Symbol Name has been entered into the Create Symbol Dialog Name field
    And an invalid Theme Name has been entered into the Create Symbol Dialog Theme field
    When the Enter key is pressed
    Then the Create Symbol Dialog should not be closed
    And the Create Symbol Dialog should show an error message for the Theme Name field
    But a new Theme should not be created
    And a new Symbol should not be created

  Scenario: Create Symbol with Theme Selected
    Given 5 Themes have been created
    And the Create Symbol Dialog has been opened without a Theme
    And a valid Symbol Name has been entered into the Create Symbol Dialog Name field
    And a Theme has been selected in the Create Symbol Dialog Theme field
    When the Enter key is pressed
    Then the Create Symbol Dialog should be closed
    And a new Symbol should be created with the supplied name in the selected Theme

  @create-theme
  Scenario: React to New Theme Created
    Given the Create Symbol Dialog has been opened without a Theme
    When a theme is created
    Then the new Theme should be listed in the Create Symbol Dialog Theme list

  @delete-theme
  Scenario: React to Theme Deleted
    Given a Theme has been created
    And the Create Symbol Dialog has been opened without a Theme
    When a theme is deleted
    Then the deleted Theme should not be listed in the Create Symbol Dialog Theme list

  @rename-theme
  Scenario: React to Theme Renamed
    Given a Theme has been created
    And the Create Symbol Dialog has been opened without a Theme
    When a theme is renamed
    Then the renamed Theme should show the new name in the Create Symbol Dialog Theme list