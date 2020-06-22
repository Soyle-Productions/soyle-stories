@theme
Feature: Create Value Web Dialog

  Background:
    Given A project has been opened
    And the Create Value Web Dialog has been opened

  @create-value-web @new
  Scenario: Create Value Web with Valid Name
    Given a valid value web name has been entered in the Create Value Web Dialog Name Field
    When the Enter key is pressed
    Then the Create Value Web Dialog should be closed
    And a new value web should be created with the supplied name

  @create-value-web @new
  Scenario: Create Value Web with Invalid Name
    Given an invalid value web  name has been entered in the Create Value Web Dialog Name Field
    When the Enter key is pressed
    Then the Create Value Web Dialog should show an error message
    But a new value web should not be created

  @create-value-web @new
  Scenario: Cancel Value Web Creation
    When the Esc key is pressed
    Then the Create Value Web Dialog should be closed
    But a new value web should not be created