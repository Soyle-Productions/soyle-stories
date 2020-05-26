Feature: Reorder Scene Ramifications

	Background: 
		Given 1 Scenes have been created
		And 1 Characters have been created

	@new
	Scenario: No Characters included in Scene
		When the Reorder Scene Ramifications Tool is opened
		Then the Reorder Scene Ramifications Tool should display an ok message
	
	@new
	Scenario: No motivations set in Scene
		Given 4 Characters have been created
		And all Characters have been included in the Scene
		When the Reorder Scene Ramifications Tool is opened
		Then the Reorder Scene Ramifications Tool should display an ok message
	
	@new
	Scenario: Motivation only set in this Scene
		Given 2 Scenes have been created
		 And this is the first Scene in the story
		 And the Character has been included in both Scenes
		 And the Character Motivation has been set in this Scene
		When the Reorder Scene Ramifications Tool is opened
		Then the other Scene should be listed in the Reorder Scene Ramifications Tool
		 And the Character should be listed in the Reorder Scene Ramifications Tool
		 And the Reorder Scene Ramifications Current Motivation field for this Character should show the Motivation set in this Scene
		 And the Reorder Scene Ramifications Changed Motivation field for this Character should be empty
		 
	@new
	Scenario: Motivation only set in next Scene
		Given 2 Scenes have been created
		 And this is the Scene after the first Scene in the story
		 And the Character has been included in both Scenes
		 And the Character Motivation has been set in this Scene
		 And the Character Motivation in the first scene is empty
		When the Reorder Scene Ramifications Tool is opened
		Then the first Scene should be listed in the Reorder Scene Ramifications Tool
		 And the Character should be listed in the Reorder Scene Ramifications Tool
		 And the Reorder Scene Ramifications Current Motivation field for this Character should show the Motivation set in this Scene
		 And the Reorder Scene Ramifications Changed Motivation field for this Character should be empty
		
	@new
	Scenario: Motivation for one Character set in next Scene
		Given 2 Scenes have been created
		 And this is the first Scene in the story
		 And 2 Characters have been created
		 And all Characters have been included in all Scenes
		 And the Character Motivation has been set for all Characters in this Scene
		 And the Character Motivation for one Character has been set in the next Scene
		When the Reorder Scene Ramifications Tool is opened
		Then the other Scene should be listed in the Reorder Scene Ramifications Tool
		 And only the Character without their Motivation set in the next Scene should be listed in the Reorder Scene Ramifications Tool
		 And the Reorder Scene Ramifications Current Motivation field for this Character should show the Motivation set in this Scene
		 And the Reorder Scene Ramifications Changed Motivation field for this Character should be empty
		
	