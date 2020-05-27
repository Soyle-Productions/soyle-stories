Feature: Reorder Scene Ramifications

	Background: 
		Given 2 Scenes have been created
		And 1 Characters have been created

	@new
	Scenario Outline: No Characters included in Scene
		When the Reorder Scene Ramifications Tool is opened for <sceneId>
		Then the Reorder Scene Ramifications Tool should display an ok message
	
	Examples:
	| sceneId |
	| "scene 1" |
	| "scene 2" |
	
	@new
	Scenario Outline: No motivations set in Scene
		Given 4 Characters have been created
		And all Characters have been included in all Scenes
		When the Reorder Scene Ramifications Tool is opened for <sceneId>
		Then the Reorder Scene Ramifications Tool should display an ok message
	
	Examples:
	| sceneId |
	| "scene 1" |
	| "scene 2" |
		
	@new
	Scenario: Back-to-back scenes with set motivations
		Given the following Scenes
			| character   | scene 1 | scene 2 |
			| character A | value 1 | value 2 |
		When the Reorder Scene Ramifications Tool is opened for "scene 1"
		Then the Reorder Scene Ramifications Tool should display an ok message
	
	@new
	Scenario: Motivation not set in scene currently following this scene
		Given the following Scenes
			| character   | scene 1 | scene 2 |
			| character A | value 1 | inherit |
		When the Reorder Scene Ramifications Tool is opened for "scene 1"
		Then "scene 2" should be listed in the Reorder Scene Ramifications Tool for "scene 1"
		 And "character A" should be listed for "scene 2" in the Reorder Scene Ramifications Tool for "scene 1"
		 And the Reorder Scene Ramifications Current Motivation field for "character A" in "scene 2" should show "value 1"
		 And the Reorder Scene Ramifications Changed Motivation field for "character A" in "scene 2" should be empty
		 
	@new
	Scenario: Motivation not set in scene now following this scene
		Given the following Scenes
			| character   | scene 1 | scene 2 |
			| character A | inherit | value 2 |
		When the Reorder Scene Ramifications Tool is opened for "scene 2"
		Then "scene 1" should be listed in the Reorder Scene Ramifications Tool for "scene 2"
		 And "character A" should be listed for "scene 1" in the Reorder Scene Ramifications Tool for "scene 2"
		 And the Reorder Scene Ramifications Current Motivation field for "character A" in "scene 1" should be empty
		 And the Reorder Scene Ramifications Changed Motivation field for "character A" in "scene 1" should show "value 2"
		 
	@new
	Scenario: Motivation set in scene now before this scene
		Given the following Scenes
			| character   | scene 1 | scene 2 |
			| character A | inherit | value 2 |
		When the Reorder Scene Ramifications Tool is opened for "scene 1"
		Then "scene 1" should be listed in the Reorder Scene Ramifications Tool for "scene 1"
		 And "character A" should be listed for "scene 1" in the Reorder Scene Ramifications Tool for "scene 1"
		 And the Reorder Scene Ramifications Current Motivation field for "character A" in "scene 1" should be empty
		 And the Reorder Scene Ramifications Changed Motivation field for "character A" in "scene 1" should show "value 2"
		 
	@new
	Scenario: Motivation set in scene now before this scene
		Given the following Scenes
			| character   | scene 1 | scene 2 |
			| character A | value 1 | inherit |
		When the Reorder Scene Ramifications Tool is opened for "scene 2"
		Then "scene 2" should be listed in the Reorder Scene Ramifications Tool for "scene 2"
		 And "character A" should be listed for "scene 2" in the Reorder Scene Ramifications Tool for "scene 2"
		 And the Reorder Scene Ramifications Current Motivation field for "character A" in "scene 2" should show "value 1"
		 And the Reorder Scene Ramifications Changed Motivation field for "character A" in "scene 2" should be empty
		
		
	
