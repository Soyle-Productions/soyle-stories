Feature: Add Symbol To Opposition Dialog

  Background:
    Given A project has been opened
    And a value web called "Justice" has been created for the "Growing Up" theme

  #Rule: All available characters should be listed

  Scenario: Open Dialog Before Any Characters Created
    When the Add Symbol to Opposition Dialog is opened
    Then no characters should be listed in the Add Symbol to Opposition Dialog
    And the character tab in the Add Symbol to Opposition Dialog should be disabled

  Scenario: Open Dialog with Characters Created
    Given a Character called "Bob" has been created
    When the Add Symbol to Opposition Dialog is opened
    Then the character "Bob" should be listed in the Add Symbol to Opposition Dialog

  Scenario: React to New Character Created
    Given the Add Symbol to Opposition Dialog has been opened
    When a Character called "Bob" is created
    Then the character "Bob" should be listed in the Add Symbol to Opposition Dialog

  Scenario: React to Character Renamed
    Given a Character called "Bob" has been created
    And the Add Symbol to Opposition Dialog has been opened
    When the character "Bob" is renamed to "Frank"
    Then the Add Symbol to Opposition Dialog should show the character "Bob" renamed to "Frank"

  Scenario: React to Character Deleted
    Given a Character called "Bob" has been created
    And the Add Symbol to Opposition Dialog has been opened
    When the character "Bob" is removed from the story
    Then the Add Symbol to Opposition Dialog should no longer list the character "Bob"

  #Rule: All available locations should be listed

  Scenario: Open Dialog Before Any Locations Created
    When the Add Symbol to Opposition Dialog is opened
    Then no locations should be listed in the Add Symbol to Opposition Dialog
    And the location tab in the Add Symbol to Opposition Dialog should be disabled

  Scenario: Open Dialog with Locations Created
    Given at least one Location has been created
    When the Add Symbol to Opposition Dialog is opened
    Then all locations should be listed in the Add Symbol to Opposition Dialog

  Scenario: React to New Location Created
    Given the Add Symbol to Opposition Dialog has been opened
    When A new Location is created
    Then all locations should be listed in the Add Symbol to Opposition Dialog

  Scenario: React to Location Renamed
    Given a location called "Home" has been created
    And the Add Symbol to Opposition Dialog has been opened
    When the location "Home" is renamed to "Work"
    Then the Add Symbol to Opposition Dialog should show the location "Home" renamed to "Work"

  Scenario: React to Location Deleted
    Given a location called "Home" has been created
    And the Add Symbol to Opposition Dialog has been opened
    When the location "Home" is removed from the story
    Then the Add Symbol to Opposition Dialog should no longer list the location "Home"

  #Rule: Only symbols in the theme should be listed

  Scenario: Open Dialog Before Any Symbols Created in Theme
    When the Add Symbol to Opposition Dialog is opened
    Then no symbols should be listed in the Add Symbol to Opposition Dialog
    But the symbol tab in the Add Symbol to Opposition Dialog should not be disabled

  Scenario: Open Dialog with Symbols Created in Another Theme
    Given a theme called "Moving On" has been created
    And a symbol has been created for the "Moving On" theme
    When the Add Symbol to Opposition Dialog is opened
    Then no symbols should be listed in the Add Symbol to Opposition Dialog
    But the symbol tab in the Add Symbol to Opposition Dialog should not be disabled

  Scenario: Open Dialog with Symbols Created in this Theme
    Given a symbol has been created for the "Growing Up" theme
    When the Add Symbol to Opposition Dialog is opened
    Then all symbols in this theme should be listed in the Add Symbol to Opposition Dialog

  Scenario: React to New Symbol Created for Theme
    Given the Add Symbol to Opposition Dialog has been opened
    When a symbol is created for the "Growing Up" theme
    Then all symbols in this theme should be listed in the Add Symbol to Opposition Dialog

  Scenario: Do not react to New Symbol Created in Different Theme
    Given a theme called "Moving On" has been created
    And the Add Symbol to Opposition Dialog has been opened
    When a symbol called "Banana" is created for the "Moving On" theme
    Then the symbol "Banana" should not be listed in the Add Symbol to Opposition Dialog

  Scenario: React to Symbol Renamed
    Given a symbol called "The Ring" has been created for the "Growing Up" theme
    And the Add Symbol to Opposition Dialog has been opened
    When the symbol "The Ring" is renamed to "A Banana"
    Then the Add Symbol to Opposition Dialog should show the symbol "The Ring" renamed to "A Banana"

  Scenario: React to Symbol Deleted
    Given a symbol called "The Ring" has been created for the "Growing Up" theme
    And the Add Symbol to Opposition Dialog has been opened
    When the symbol "The Ring" is removed from the story
    Then the Add Symbol to Opposition Dialog should no longer list the Symbol "The Ring"

  #Rule: Selecting an item adds that item as a symbol

  Scenario: Add Symbolic Character to Value Web
    Given a Character called "Bob" has been created
    And the Add Symbol to Opposition Dialog has been opened
    When the character "Bob" is selected in the Add Symbol to Opposition Dialog
    Then the Add Symbol to Opposition Dialog should be closed
    And the character "Bob" should be listed as a symbol in the value opposition

  Scenario: Add Symbolic Location to Value Web
    Given a location called "Home" has been created
    And the Add Symbol to Opposition Dialog has been opened
    When the location "Home" is selected in the Add Symbol to Opposition Dialog
    Then the Add Symbol to Opposition Dialog should be closed
    And the location "Home" should be listed as a symbol in the value opposition

  Scenario: Add Symbol to Value Web
    Given a symbol called "The Ring" has been created for the "Growing Up" theme
    And the Add Symbol to Opposition Dialog has been opened
    When the symbol "The Ring" is selected in the Add Symbol to Opposition Dialog
    Then the Add Symbol to Opposition Dialog should be closed
    And the symbol "The Ring" should be listed as a symbol in the value opposition

  Scenario: Create Custom Symbol to Add to Value Web
    Given the Add Symbol to Opposition Dialog has been opened
    When the Create Symbol button is selected in the Add Symbol to Opposition Dialog
    Then the Create Symbol Dialog should be open
    And the Create Symbol Dialog Theme field should not be visible
    But the Add Symbol to Opposition Dialog should be closed

  Scenario: Complete Creating Symbol to Add to Value Web
    Given the Add Symbol to Opposition Dialog has been opened
    And the Create Symbol Dialog has been opened from the Add Symbol to Opposition Dialog
    And "The Ring" has been entered into the Create Symbol Dialog name field
    When the Enter key is pressed
    Then the Create Symbol Dialog should be closed
    And the symbol "The Ring" should be listed as a symbol in the value opposition

  #Rule: No effects should take place if the dialog is closed

  Scenario: Cancel Adding Symbol to Value Web
    Given the Add Symbol to Opposition Dialog has been opened
    When the Esc key is pressed
    Then the Add Symbol to Opposition Dialog should be closed
    But nothing should be listed as a symbol in the value opposition