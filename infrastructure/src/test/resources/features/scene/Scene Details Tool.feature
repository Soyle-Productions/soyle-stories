@scene
Feature: Scene Details Tool

	@link-location-to-story-event @excluded
	Scenario: No Locations available
	Given a Scene has been created
	And no Locations have been created
	When the Scene Details Tool is opened
	Then the Scene Details Location dropdown should be disabled
	
	@link-location-to-story-event @excluded
	Scenario: No Location Linked
	Given a Scene has been created
	And 1 Locations have been created
	When the Scene Details Tool is opened
	Then the Scene Details Location dropdown should not be disabled
	And the Scene Details Location dropdown should show "Select Location"
	
	@link-location-to-story-event @excluded
	Scenario: Location linked
	Given a Scene has been created
	And 1 Locations have been created
	And a Location has been linked to the Scene
	When the Scene Details Tool is opened
	Then the Scene Details Location dropdown should not be disabled
	And the Scene Details Location dropdown should show the linked Location name
	
	@link-location-to-story-event @excluded
	Scenario: Select Location
	Given a Scene has been created
	And 1 Locations have been created
	And the Scene Details Tool has been opened
	When a Location is selected from the Scene Details Location drop-down
	Then the Scene Details Location drop-down should show the selected Location name
	And the Location should be linked to the Scene
	
	@link-location-to-story-event @rename-location @excluded
	Scenario: Update on Location renamed
	Given a Scene has been created
	And 1 Locations have been created
	And a Location has been linked to the Scene
	And the Scene Details Tool has been opened
	When the Location is renamed
	Then the Scene Details Location drop-down should show the new Location name
	
	@link-location-to-story-event @delete-location @excluded
	Scenario: Update on Location deleted
	Given a Scene has been created
	And 1 Locations have been created
	And a Location has been linked to the Scene
	And the Scene Details Tool has been opened
	When the Location is deleted
	Then the Scene Details Location drop-down should show "Select Location"
	
	@add-character-to-story-event @excluded
	Scenario: No Characters available
	Given a Scene has been created
	And no Characters have been created
	When the Scene Details Tool is opened
	Then the Scene Details Add Character button should be disabled

	@add-character-to-story-event @excluded
	Scenario: No Characters included
	Given a Scene has been created
	And 1 Characters have been created
	When the Scene Details Tool is opened
	Then the Scene Details Add Character button should not be disabled
	And no Characters should be listed in the Scene Details Tool
	
	@add-character-to-story-event @excluded
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
	
	@add-character-to-story-event @excluded
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