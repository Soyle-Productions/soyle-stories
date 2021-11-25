@PostAlpha
Feature: Compare Character Values

  Background:
    Given I have started a project
    And I have created a theme named "Growing Up"

  Scenario: Add Existing Character to Thematic Character Value Comparison
    Given I have created a character named "Bob"
    And I am comparing the character values of the "Growing Up" theme
    When I include the "Bob" character in the "Growing Up" theme's character value comparison
    Then the "Bob" character should be included in the "Growing Up" theme's character value comparison
    And the "Bob" character should have no values in the "Growing Up" theme

  Scenario: Add New Character to Thematic Character Value Comparison
    Given I am comparing the character values of the "Growing Up" theme
    When I create a character named "Bob" to include in the "Growing Up" theme's character value comparison
    Then the "Bob" character should be included in the "Growing Up" theme's character value comparison
    And the "Bob" character should have no values in the "Growing Up" theme

  Scenario: Add Existing Opposition Value
    Given I have created a value web named "Love" in the "Growing Up" theme
    And I have created a character named "Bob"
    And I am comparing the character values of the "Growing Up" theme
    And I have included the "Bob" character in the "Growing Up" theme's character value comparison
    And I am selecting a value to add to the "Bob" character in the "Growing Up" theme's character value comparison
    When I select the "Growing Up" theme's "Love" value web's "Love" opposition value to add to the "Bob" character
    Then the "Bob" character in the "Growing Up" theme should have the "Love" value web's "Love" opposition value

  Scenario: Add New Opposition Value from Existing Value Web
    Given I have created a value web named "Love" in the "Growing Up" theme
    And I have created a character named "Bob"
    And I am comparing the character values of the "Growing Up" theme
    And I have included the "Bob" character in the "Growing Up" theme's character value comparison
    And I am selecting a value to add to the "Bob" character in the "Growing Up" theme's character value comparison
    When I create an opposition value named "Hate" in the "Growing Up" theme's "Love" value web to add to the "Bob" character
    Then the "Bob" character in the "Growing Up" theme should have the "Love" value web's "Hate" opposition value

  Scenario: Add New Value Web and Opposition Value
    Given I have created a character named "Bob"
    And I am comparing the character values of the "Growing Up" theme
    And I have included the "Bob" character in the "Growing Up" theme's character value comparison
    And I am selecting a value to add to the "Bob" character in the "Growing Up" theme's character value comparison
    When I create a value web named "Love" in the "Growing Up" theme to add to the "Bob" character
    Then the "Bob" character in the "Growing Up" theme should have the "Love" value web's "Love" opposition value

  Scenario: Rename Opposition Value Used as Character Value
    Given I have created a value web named "Love" in the "Growing Up" theme
    And I have created a character named "Bob"
    And I am comparing the character values of the "Growing Up" theme
    And I have included the "Bob" character in the "Growing Up" theme's character value comparison
    And I have used the "Growing Up" theme's "Love" value web's "Love" opposition value for the "Bob" character
    When I rename the "Growing Up" theme's "Love" value web's "Love" opposition value to "Hate"
    Then the "Bob" character in the "Growing Up" theme should have the "Love" value web's "Hate" opposition value
    And the "Bob" character in the "Growing Up" theme should not have the "Love" value web's "Love" opposition value

  Scenario: Rename Value Web with Opposition Value Used as Character Value
    Given I have created a value web named "Love" in the "Growing Up" theme
    And I have created a character named "Bob"
    And I am comparing the character values of the "Growing Up" theme
    And I have included the "Bob" character in the "Growing Up" theme's character value comparison
    And I have used the "Growing Up" theme's "Love" value web's "Love" opposition value for the "Bob" character
    When I rename the "Growing Up" theme's "Love" value web to "Greed"
    Then the "Bob" character in the "Growing Up" theme should have the "Greed" value web's "Love" opposition value
