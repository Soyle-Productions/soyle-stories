Feature: Cover Existing Sections
    
  Background:
    Given a Scene called "Big Battle" has been created
    And a Character called "Bob" has been created
    And the Character "Bob" has been included in the "Big Battle" Scene
    And 3 Character Arcs have been created for the Character "Bob"
    And some Character Arc Sections for the Character "Bob" have been covered by the "Big Battle" Scene

  Scenario: Indicate intent to cover character arc sections in scene
    When the user indicates they want to cover character arc sections for the Character "Bob" in the "Big Battle" Scene
    Then all Character Arcs and their sections should be listed for the Character "Bob" to cover in the "Big Battle" Scene
    And any Character Arc Sections included in the "Big Battle" Scene for the Character "Bob" should be marked
    And any Character Arcs with included sections in the "Big Battle" Scene for the Character "Bob" should be marked

  Scenario: Specify Character Arc Sections to cover in Scene
    Given the user has indicated they want to cover character arc sections for the Character "Bob" in the "Big Battle" Scene
    When the user specifies additional character arc sections to cover in the "Big Battle" Scene for the Character "Bob"
    Then the specified character arc sections should be covered in the "Big Battle" Scene for the Character "Bob"

  Scenario: Remove Covered Sections
    When the user specifies which character arc sections to uncover in the "Big Battle" Scene for the Character "Bob"
    Then the specified character arc sections should be uncovered in the "Big Battle" Scene for the Character "Bob"