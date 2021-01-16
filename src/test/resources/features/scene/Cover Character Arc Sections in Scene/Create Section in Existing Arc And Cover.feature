Feature: Create Section in Existing Arc And Cover

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created a character named "Bob"
    And I have included the character "Bob" in the "Big Battle" scene
    And I have created 3 character arcs for the character "Bob"
    And I have covered some character arc sections for the character "Bob" in the "Big Battle" scene

  Scenario: List Available Character Arc Section Templates
    Given I am covering character arc sections for the character "Bob" in the "Big Battle" scene
    When the user indicates that they want to create a new character arc section for one of "Bob"s character arcs
    Then all character arc section templates in "Bob"s character arc should be listed
    And templates that do not allow multiple and have a section in "Bob"s character arc should be marked

  Scenario: Create Character Arc Section
    Given the user has indicated they want to cover character arc sections for the Character "Bob" in the "Big Battle" Scene
    And the user has indicated that they want to create a new character arc section for one of "Bob"s character arcs
    When an unused arc section template is selected and given a description of "Bob wants to destroy that guy"
    Then the previously unused arc section template should have an arc section in "Bob"s character arc
    And the new arc section in "Bob"s character arc should be covered in the "Big Battle" Scene