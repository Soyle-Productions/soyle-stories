@scene
Feature: Scene Details Tool

  @link-location-to-story-event
  Scenario: No Locations available
    Given a Scene has been created
    And no Locations have been created
    When the Scene Details Tool is opened
    Then the Scene Details Location dropdown should be disabled

  @link-location-to-story-event
  Scenario: No Location Linked
    Given a Scene has been created
    And 1 Locations have been created
    When the Scene Details Tool is opened
    Then the Scene Details Location dropdown should not be disabled
    And the Scene Details Location dropdown should show "Select Location"

  @link-location-to-story-event
  Scenario: Location linked
    Given a Scene has been created
    And 1 Locations have been created
    And a Location has been linked to the Scene
    When the Scene Details Tool is opened
    Then the Scene Details Location dropdown should not be disabled
    And the Scene Details Location dropdown should show the linked Location name

  @link-location-to-story-event
  Scenario: Select Location
    Given a Scene has been created
    And 1 Locations have been created
    And the Scene Details Tool has been opened
    When a Location is selected from the Scene Details Location drop-down
    Then the Scene Details Location drop-down should show the selected Location name
    And the Location should be linked to the Scene

  @link-location-to-story-event @rename-location
  Scenario: Update on Location renamed
    Given a Scene has been created
    And 1 Locations have been created
    And a Location has been linked to the Scene
    And the Scene Details Tool has been opened
    When the Location is renamed
    Then the Scene Details Location drop-down should show the new Location name

  @link-location-to-story-event @delete-location
  Scenario: Update on Location deleted
    Given a Scene has been created
    And 1 Locations have been created
    And a Location has been linked to the Scene
    And the Scene Details Tool has been opened
    When the Location is deleted
    Then the Scene Details Location dropdown should show "Select Location"

  @add-character-to-story-event
  Scenario: No Characters available
    Given a Scene has been created
    And no Characters have been created
    When the Scene Details Tool is opened
    Then the Scene Details Add Character button should be disabled

  @add-character-to-story-event
  Scenario: No Characters included
    Given a Scene has been created
    And 1 Characters have been created
    When the Scene Details Tool is opened
    Then the Scene Details Add Character button should not be disabled
    And no Characters should be listed in the Scene Details Tool

  @add-character-to-story-event
  Scenario Outline: All Characters included
    Given a Scene has been created
    And <number> Characters have been created
    And all Characters have been included in the Scene
    When the Scene Details Tool is opened
    Then the Scene Details Add Character button should be disabled
    And all Characters should be listed in the Scene Details Tool

    Examples:
      | number |
      | 4      |
      | 2      |

  @add-character-to-story-event
  Scenario: Add last Character
    Given a Scene has been created
    And 1 Characters have been created
    And the Scene Details Tool has been opened
    When the Character is included in the Scene
    Then the Scene Details Add Character button should be disabled
    And the Character should be listed in the Scene Details Tool

  @remove-character-from-story-event
  Scenario: Remove Character
    Given a Scene has been created
    And 2 Characters have been created
    And 1 Characters have been included in the Scene
    And the Scene Details Tool has been opened
    When a Character is removed from the Scene
    Then the Character should not be listed in the Scene Details Tool

  @remove-character-from-story-event
  Scenario: Remove last Character
    Given a Scene has been created
    And 2 Characters have been created
    And all Characters have been included in the Scene
    And the Scene Details Tool has been opened
    When a Character is removed from the Scene
    Then the Character should not be listed in the Scene Details Tool
    And the Scene Details Add Character button should not be disabled

  @set-character-motivation-in-scene
  Scenario: No motivation set
    Given a Scene has been created
    And 1 Characters have been created
    And 1 Characters have been included in the Scene
    When the Scene Details Tool is opened
    Then the Scene Details Character Motivation field should be blank

  @set-character-motivation-in-scene
  Scenario: Motivation set in previous scene
    Given the following Scenes
      | characters  | scene 1 | scene 2 |
      | Character A | value 1 | inherit |
    When the "scene 2" Scene Details Tool is opened
    Then the "scene 2" Scene Details "Character A" Character Motivation field should show "value 1"
    And the "scene 2" Scene Details "Character A" Character Motivation Previously Set tip should be visible

  @set-character-motivation-in-scene
  Scenario: Motivation set in scene
    Given the following Scenes
      | characters  | scene 1 |
      | Character A | value 1 |
    When the "scene 1" Scene Details Tool is opened
    Then the "scene 1" Scene Details "Character A" Character Motivation field should show "value 1"

  @set-character-motivation-in-scene
  Scenario: Motivation overwritten in this Scene
    Given the following Scenes
      | characters  | scene 1 | scene 2 |
      | Character A | value 1 | value 2 |
    When the "scene 2" Scene Details Tool is opened
    Then the "scene 2" Scene Details "Character A" Character Motivation field should show "value 2"
    And the "scene 2" Scene Details "Character A" Character Motivation Previously Set tip should be visible
    And the "scene 2" Scene Details "Character A" Character Motivation Reset button should be visible

  @set-character-motivation-in-scene
  Scenario: Hover over previously set tip
    Given the following Scenes
      | characters  | scene 1 | scene 2 |
      | Character A | value 1 | inherit |
    And the "scene 2" Scene Details Tool has been opened
    When the "scene 2" Scene Details Character Motivation Previously Set tooltip is opened for "Character A"
    Then the "scene 2" Scene Details Character Motivation Previously Set tooltip scene name should show "scene 1"
    And the "scene 2" Scene Details Character Motivation Previously Set tooltip motivation should show "value 1"

  @set-character-motivation-in-scene
  Scenario: Jump to previous Scene
    Given the following Scenes
      | characters  | scene 1 | scene 2 |
      | Character A | value 1 | inherit |
    And the "scene 2" Scene Details Tool has been opened
    And the "scene 2" Scene Details Character Motivation Previously Set tooltip has been opened for "Character A"
    When the "scene 2" Scene Details Character Motivation Previously Set tooltip scene name is selected
    Then the "scene 1" Scene Details Tool should be open
    And the "scene 1" Scene Details Tool should be in focus

  @set-character-motivation-in-scene
  Scenario: Motivation overwritten in this Scene
    Given the following Scenes
      | characters  | scene 1 | scene 2 |
      | Character A | value 1 | value 2 |
    And the "scene 2" Scene Details Tool has been opened
    When the "scene 2" Scene Details "Character A" Character Motivation Reset button is selected
    Then the "scene 2" Scene Details "Character A" Character Motivation field should show "value 1"
    And the "scene 2" Scene Details "Character A" Character Motivation Reset button should not be visible

  @rename-character @add-character-to-story-event
  Scenario: Scene Details Tool reacts to Character rename
    Given the following Scenes
      | characters  | scene 1 |
      | Character A | inherit |
    And the "scene 1" Scene Details Tool has been opened
    When the Character "Character A" is renamed to "Frank"
    Then the "scene 1" Scene Details "Character A" name should show "Frank"

  @delete-character @remove-character-from-story-event
  Scenario: Scene Details Tool reacts to Character deletion
    Given the following Scenes
      | characters  | scene 1 |
      | Character A | inherit |
    And the "scene 1" Scene Details Tool has been opened
    When the Character "Character A" is deleted
    Then the "scene 1" Scene Details should not list "Character A" as an included character
	