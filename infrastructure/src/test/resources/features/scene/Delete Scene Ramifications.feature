@scene
Feature: Delete Scene Ramifications

	Background: 
		Given 1 Scenes have been created
		And 1 Characters have been created

	@delete-scene
	Scenario: No Characters included in Scene
		When the Delete Scene Ramifications Tool is opened
		Then the Delete Scene Ramifications Tool should display an ok message

	@delete-scene
	Scenario: No motivations set in Scene
		Given 4 Characters have been created
		And all Characters have been included in the Scene
		When the Delete Scene Ramifications Tool is opened
		Then the Delete Scene Ramifications Tool should display an ok message

	@delete-scene
	Scenario: Motivation only set in this Scene
		Given the following Scenes
			| character   | scene 1 | scene 2 |
			| character A | value1  | inherit |
		When the Delete Scene Ramifications Tool is opened for "scene 1"
		Then "character A" should be listed for "scene 2" in the Delete Scene Ramifications Tool for "scene 1"
		And the Current Motivation field for "character A" in "scene 2" in the Delete Scene Ramifications Tool for "scene 1" should show "value1"
		And the Changed Motivation field for "character A" in "scene 2" in the Delete Scene Ramifications Tool for "scene 1" should be empty

	@delete-scene
	Scenario: Motivation for one Character set in next Scene
		Given the following Scenes
			| character   | scene 1 | scene 2 |
			| character A | value1a | inherit |
			| character B | value1b | value2b |
		When the Delete Scene Ramifications Tool is opened for "scene 1"
		Then "character A" should be listed for "scene 2" in the Delete Scene Ramifications Tool for "scene 1"
		But "character B" should not be listed for "scene 2" in the Delete Scene Ramifications Tool for "scene 1"
		And the Current Motivation field for "character A" in "scene 2" in the Delete Scene Ramifications Tool for "scene 1" should show "value1a"
		And the Changed Motivation field for "character A" in "scene 2" in the Delete Scene Ramifications Tool for "scene 1" should be empty

	@delete-scene
	Scenario: Show inherited motivations
		Given the following Scenes
			| character   | scene 1 | scene 2 | scene 3 |
			| character A | value1a | value2a | inherit |
		When the Delete Scene Ramifications Tool is opened for "scene 2"
		Then "character A" should be listed for "scene 3" in the Delete Scene Ramifications Tool for "scene 2"
		And the Current Motivation field for "character A" in "scene 3" in the Delete Scene Ramifications Tool for "scene 2" should show "value2a"
		And the Changed Motivation field for "character A" in "scene 3" in the Delete Scene Ramifications Tool for "scene 2" should show "value1a"

	@delete-scene
	Scenario: React to Scene Deleted
		Given the following Scenes
			| character   | scene 1 | scene 2 | scene 3 |
			| character A | value1a | inherit | inherit |
		And the Delete Scene Ramifications Tool has been opened for "scene 1"
		When "scene 2" is deleted
		Then "scene 2" should not be listed in the Delete Scene Ramifications Tool for "scene 1"

	@delete-scene @delete-character
	Scenario: React to Character Deleted
		Given the following Scenes
			| character   | scene 1 | scene 2 |
			| character A | value1a | inherit |
			| character B | value2a | inherit |
		And the Delete Scene Ramifications Tool has been opened for "scene 1"
		When A Character is deleted
		Then the deleted Character should be removed from the Delete Scene Ramifications Tool

	@delete-scene
	Scenario: React to Last Affected Character in Scene Removed
		Given the following Scenes
			| character   | scene 1 | scene 2 | scene 3 |
			| character A | value1a | inherit | inherit |
			| character B | value1a | -       | inherit |
		And the Delete Scene Ramifications Tool has been opened for "scene 1"
		When "character A" is removed from "scene 2" in the Delete Scene Ramifications Tool for "scene 1"
		Then "scene 2" should not be listed in the Delete Scene Ramifications Tool for "scene 1"

	@delete-scene
	Scenario: React to Last Affected Scene Removed
		Given the following Scenes
			| character   | scene 1 | scene 2 |
			| character A | value1  | inherit |
		And the Delete Scene Ramifications Tool has been opened for "scene 1"
		When "scene 2" is removed from the Delete Scene Ramifications Tool for "scene 1"
		Then the Delete Scene Ramifications Tool for "scene 1" should display an ok message

	@delete-scene @set-character-motivation-in-scene
	Scenario: React to Character Motivation being cleared
		Given the following Scenes
			| character   | scene 1 | scene 2 | scene 3 |
			| character A | value1  | value2  | inherit |
		And the Delete Scene Ramifications Tool has been opened for "scene 1"
		When the Character Motivation for "character A" is cleared in "scene 2"
		Then "scene 3" should be listed in the Delete Scene Ramifications Tool for "scene 1"

	@delete-scene @set-character-motivation-in-scene
	Scenario: React to Character Motivation being set
		Given the following Scenes
			| character   | scene 1 | scene 2 | scene 3 |
			| character A | value1  | inherit | inherit |
		And the Delete Scene Ramifications Tool has been opened for "scene 1"
		When the Character Motivation for "character A" is set in "scene 2"
		Then "scene 3" should not be listed in the Delete Scene Ramifications Tool for "scene 1"

	@delete-scene @set-character-motivation-in-scene
	Scenario: React to previous Scenes clearing a Character Motivation
		Given the following Scenes
			| character   | scene 1 | scene 2 | scene 3 |
			| character A | value1  | value2  | inherit |
		And the Delete Scene Ramifications Tool has been opened for "scene 2"
		When the Character Motivation for "character A" is cleared in "scene 1"
		Then the Changed Motivation field for "character A" in "scene 3" in the Delete Scene Ramifications Tool for "scene 2" should be empty

	@delete-scene @set-character-motivation-in-scene @excluded
	Scenario: React to previous Scenes setting a Character Motivation
		Given the following Scenes
			| character   | scene 1 | scene 2 | scene 3 |
			| character A | inherit | value2  | inherit |
		And the Delete Scene Ramifications Tool has been opened for "scene 2"
		When the Character Motivation for "character A" is set in "scene 1"
		Then the Changed Motivation field for "character A" in "scene 3" in the Delete Scene Ramifications Tool for "scene 2" should show the value from "scene 1"