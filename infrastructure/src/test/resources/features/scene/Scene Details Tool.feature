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
	When a Location is selected from the Scene Details Location dropdown
	Then the Scene Details Location dropdown should show the selected Location name
	And the Location should be linked to the Scene
	
	@link-location-to-story-event @rename-location @excluded
	Scenario: Update on Location renamed
	Given a Scene has been created
	And 1 Locations have been created
	And a Location has been linked to the Scene
	And the Scene Details Tool has been opened
	When the Location is renamed
	Then the Scene Details Location dropdown should show the new Location name
	
	@link-location-to-story-event @delete-location @excluded
	Scenario: Update on Location deleted
	Given a Scene has been created
	And 1 Locations have been created
	And a Location has been linked to the Scene
	And the Scene Details Tool has been opened
	When the Location is deleted
	Then the Scene Details Location dropdown should show "Select Location"