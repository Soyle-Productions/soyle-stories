Feature: Open Project
	User can select a file to open as a project

	Background:
		Given The application has started
		And User has selected a file
		And The selected file exists
		And The selected file is a project file

	Scenario: Open project from welcome screen
		Given Welcome screen has been opened
		When The selected file is opened
		Then The selected file should be open
			But Welcome screen should not be open

	Scenario: Open project while other project is still open
		Given One project has been opened
		When The selected file is opened
		Then Open project option dialog should be open
			But The selected file should not be open

	Scenario: Replace open project
		Given One project has been opened
		When The selected file is opened
		And The open in current window option is selected
		Then The selected file should be open
		But First open project should not be open

	Scenario: Open second project
		Given One project has been opened
		When The selected file is opened
		And The open in new window option is selected
		Then The selected file should be open
		And First open project should be open

  Scenario: Open third project
    Given Two projects have been opened
    When The selected file is opened
    Then The selected file should be open
    And First two projects should be open
		But Open project option dialog should not be open

