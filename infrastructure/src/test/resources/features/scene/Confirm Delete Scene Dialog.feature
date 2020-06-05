@scene
Feature: Confirm Delete Scene Dialog

	Background:
	Given 2 Scenes have been created
	  And the Confirm Delete Scene Dialog has been opened

	@delete-scene
	Scenario: Cancel the deletion
	 When the Confirm Delete Scene Dialog "Cancel" button is selected
	 Then the Confirm Delete Scene Dialog should be closed
	  And the Scene should not be deleted
	
	@delete-scene
	Scenario Outline: Toggle show dialog
	 When the Confirm Delete Scene Dialog do not show again check-box is checked
	  And the Confirm Delete Scene Dialog <button> button is selected
	 Then the Confirm Delete Scene Dialog should not open the next time a Scene is deleted
	  
	Examples: 
	| button |
	| "Delete" |
	| "Show Ramifications" |
	
	@delete-scene
	Scenario: Delete without showing ramifications
	 When the Confirm Delete Scene Dialog "Delete" button is selected
	 Then the Scene should be deleted
	
	@delete-scene
	Scenario: Show ramifications
	 When the Confirm Delete Scene Dialog "Show Ramifications" button is selected
	 Then the Scene should not be deleted
	  And the Confirm Delete Scene Dialog should be closed
	  And the Delete Scene Ramifications Tool should be open
