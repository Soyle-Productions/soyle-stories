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
	| 4 |
	| 2 |
	
	@add-character-to-story-event
	Scenario: Add last Character
	Given a Scene has been created
	And 1 Characters have been created
	And the Scene Details Tool has been opened
	When the Character is included in the Scene
	Then the Scene Details Add Character button should be disabled
	And the Character should be listed in the Scene Details Tool
	
	@remove-character-from-story-event @excluded
	Scenario: Remove Character
	Given a Scene has been created
	And 2 Characters have been created
	And 1 Characters have been included in the Scene
	And the Scene Details Tool has been opened
	When a Character is removed from the Scene
	Then the Character should not be listed in the Scene Details Tool
	
	@remove-character-from-story-event @excluded
	Scenario: Remove last Character
	Given a Scene has been created
	And 2 Characters have been created
	And all Characters have been included in the Scene
	And the Scene Details Tool has been opened
	When a Character is removed from the Scene
	Then the Character should not be listed in the Scene Details Tool
	And the Scene Details Add Character button should not be disabled
	
	@set-character-motivation-in-scene @excluded
	Scenario: No motivation set
	Given a Scene has been created
	And 1 Characters have been created
	And 1 Characters have been included in the Scene
	When the Scene Details Tool is opened
	Then the Scene Details Character Motivation field should be blank
	
	@set-character-motivation-in-scene @excluded
	Scenario: Motivation set in previous scene
	Given 2 Scenes have been created
	And 1 Characters have been created
	And 1 Characters have been included in the Scene
	And the Character has been included in the previous Scene
	And the Character's motivation has been set in the previous Scene
	When the Scene Details Tool is opened
	Then the Scene Details Character Motivation field should show the motivation set in the previous Scene
	And the Scene Details Character Motivation Previously Set tip should be visible
	
	@set-character-motivation-in-scene @excluded
	Scenario: Motivation set in scene
	Given a Scene has been created
	And 1 Characters have been created
	And 1 Characters have been included in the Scene
	And the Character's motivation has been set in this Scene
	When the Scene Details Tool is opened
	Then the Scene Details Character Motivation field should show the motivation set in this Scene
	
	@set-character-motivation-in-scene @excluded
	Scenario: Motivation overwritten in this Scene
	Given 2 Scenes have been created
	And 1 Characters have been created
	And the Character has been included in both Scenes
	And the Character's motivation has been set in the previous Scene
	And the Character's motivation has been set in this Scene
	When the Scene Details Tool is opened
	Then the Scene Details Character Motivation field should show the motivation set in this Scene
	And the Scene Details Character Motivation Previously Set tip should be visible
	And the Scene Details Character Motivation Reset button should be visible
	
	@set-character-motivation-in-scene @excluded
	Scenario: Hover over previously set tip
	Given 2 Scenes have been created
	And 1 Characters have been created
	And the Character has been included in both Scenes
	And the Character's motivation has been set in the previous Scene
	And the Scene Details Tool has been opened
	When the Scene Details Character Motivation Previously Set tooltip is opened
	Then the Scene Details Character Motivation Previously Set tooltip should show the previous Scene name
	And the Scene Details Character Motivation Previously Set tooltip should show the motivation set in the previous Scene
	
	@set-character-motivation-in-scene @excluded
	Scenario: Jump to previous Scene
	Given 2 Scenes have been created
	And 1 Characters have been created
	And the Character has been included in both Scenes
	And the Character's motivation has been set in the previous Scene
	And the Scene Details Tool has been opened
	And the Scene Details Character Motivation Previously Set tooltip has been opened
	When the Scene Details Character Motivation Previously Set tooltip Scene name is selected
	Then the Scene Details Tool for the previous Scene should be open
	And the Scene Details Tool for the previous Scene should be in focus
	
	@set-character-motivation-in-scene @excluded
	Scenario: Motivation overwritten in this Scene
	Given 2 Scenes have been created
	And 1 Characters have been created
	And the Character has been included in both Scenes
	And the Character's motivation has been set in both Scenes
	And the Scene Details Tool has been opened
	When the Scene Details Character Motivation Reset button is selected
	Then the Scene Details Character Motivation should show should show the motivation set in the previous Scene
	And the Scene Details Character Motivation Reset button should not be visible
	
	@rename-character @add-character-to-story-event @excluded
	Scenario: Scene Details Tool reacts to Chracter rename
	Given 1 Characters have been created
	And 1 Scenes have been created
	And the Character has been included in the Scene
	And the Scene Details Tool has been opened
	When the Character is renamed
	Then the Scene Details Tool should show the Character's new name
	
	@delete-character @remove-character-from-story-event @excluded
	Scenario: Scene Details Tool reacts to Chracter deletion
	Given 1 Characters have been created
	And 1 Scenes have been created
	And the Character has been included in the Scene
	And the Scene Details Tool has been opened
	When the Character is deleted
	Then the Scene Details Tool should not show the deleted Character
	