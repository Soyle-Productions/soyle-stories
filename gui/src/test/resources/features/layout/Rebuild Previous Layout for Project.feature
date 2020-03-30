Feature: Rebuild Previous Layout for Project
	User re-opens a project, the system also loads the layout saved for that project so they can pick up exactly where
	they left off.

	Background:
		Given The application has started

	Scenario: As a user starting a project, I want to see the tools available to me so I can get started quickly.
		Given User has selected a directory
			And The selected directory exists
			And User has entered a name into the project name field
			And A file with the entered name does not exist in selected directory
		When User starts a new project
		Then The default tool group should be open
			And The helper tips tool should be open
			And The character list tool should be open
		
	Scenario Outline: As a returning user, I want to be visually reminded of what tools I had open last time so I can easily pick up where I left off.
		Given One project has been opened
			And The layout has been in <State>
		When Application is closed
			And Application is started
		Then The layout should be in <State>

		Examples:
			| State |
			| "DefaultState" |
			| "AllStaticToolsOpen" |
			| "NoToolsOpen" |