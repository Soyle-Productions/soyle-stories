Feature: Create Section in Existing Arc And Cover

  Background:
    Given a Scene called "Big Battle" has been created
    And a Character called "Bob" has been created
    And the Character "Bob" has been included in the "Big Battle" Scene
    And 3 Character Arcs have been created for the Character "Bob"
    And some Character Arc Sections for the Character "Bob" have been covered by the "Big Battle" Scene

  Scenario: List Available Character Arc Section Templates
    Given the user has indicated they want to cover character arc sections for the Character "Bob" in the "Big Battle" Scene
    When the user indicates that they want to create a new character arc section for one of "Bob"s character arcs
    Then all character arc section templates in "Bob"s character arc should be listed
    And templates that do not allow multiple and have a section in "Bob"s character arc should be marked

  Scenario: Create Character Arc Section
    Given the user has indicated they want to cover character arc sections for the Character "Bob" in the "Big Battle" Scene
    And the user has indicated that they want to create a new character arc section for one of "Bob"s character arcs
    When an unused arc section template is selected and given a description of "Bob wants to destroy that guy"
    Then the previously unused arc section template should have an arc section in "Bob"s character arc
    And the new arc section in "Bob"s character arc should be covered in the "Big Battle" Scene