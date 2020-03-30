Feature: BuildNewCharacter
	Authors can create new characters at any point during the writing process

	Background:
		Given The application has started

	Scenario: As a storyteller just getting started, I want to build my first character
		Given One project has been opened
			And The character list tool has been opened
			And User has entered a name into the character name field
		When A new character is built
		Then The built character should be listed in the character list tool

	Scenario: