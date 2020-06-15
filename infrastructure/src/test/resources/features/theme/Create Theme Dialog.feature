@theme
Feature: Create Theme Dialog

  Background:
    Given A project has been opened
    And the Create Theme Dialog has been opened

  @create-theme @new
  Scenario: Create Theme with Valid Name
    Given a valid Theme name has been entered in the Create Theme Dialog Name Field
    When the Enter key is pressed
    Then the Create Theme Dialog should be closed
    And a new Theme should be created with the supplied name

  @create-theme @new
  Scenario: Create Theme with Invalid Name
    Given a valid Theme name has been entered in the Create Theme Dialog Name Field
    When the Enter key is pressed
    Then the Create Theme Dialog should show an error message
    But a new Theme should not be created

  @create-theme @new
  Scenario: Cancel Theme Creation
    When the Esc key is pressed
    Then the Create Theme Dialog should be closed
    But a new Theme should not be created