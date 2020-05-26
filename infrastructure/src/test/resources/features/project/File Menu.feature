Feature: File Menu

  Background:
    Given A project has been opened

  @excluded
  Scenario Outline: File menu shows the Settings option
    When the File Menu is opened
    Then the File Menu should display <item>
	
    Examples:
      | item       |
      | "Settings" |
	  
  @excluded
  Scenario: Open Settings dialog
    Given the File Menu has been opened
    When the File Menu "Settings" item is selected
    Then the Settings Dialog should be open
	
	