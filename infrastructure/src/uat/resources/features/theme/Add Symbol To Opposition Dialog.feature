Feature: Add Symbol To Opposition Dialog

  #Rule: All available characters should be listed

  @new
  Scenario: Open Dialog Before Any Characters Created
    Given A project has been opened
    And a theme has been created with a value web
    When the Add Symbol to Opposition Dialog is opened
    Then no characters should be listed in the Add Symbol to Opposition Dialog
    And the character tab in the Add Symbol to Opposition Dialog should be disabled

  @new
  Scenario: Open Dialog with Characters Created
    Given A project has been opened
    And a theme has been created with a value web
    And a Character has been created
    When the Add Symbol to Opposition Dialog is opened
    Then all characters should be listed in the Add Symbol to Opposition Dialog

  @new
  Scenario: React to New Character Created
    Given A project has been opened
    And a theme has been created with a value web
    And the Add Symbol to Opposition Dialog has been opened
    When A new Character is created
    Then all characters should be listed in the Add Symbol to Opposition Dialog

  @new
  Scenario: React to Character Renamed
    Given A project has been opened
    And a theme has been created with a value web
    And a character called "Bob" has been created
    And the Add Symbol to Opposition Dialog has been opened
    When the character "Bob" is renamed to "Frank"
    Then the Add Symbol to Opposition Dialog should show the character "Bob" renamed to "Frank"

  @new
  Scenario: React to Character Deleted
    Given A project has been opened
    And a theme has been created with a value web
    And a character called "Bob" has been created
    And the Add Symbol to Opposition Dialog has been opened
    When the character "Bob" is removed from the story
    Then the Add Symbol to Opposition Dialog should no longer list the character "Bob"

  #Rule: All available locations should be listed

  @new
  Scenario: Open Dialog Before Any Locations Created
    Given A project has been opened
    And a theme has been created with a value web
    When the Add Symbol to Opposition Dialog is opened
    Then no locations should be listed in the Add Symbol to Opposition Dialog
    And the location tab in the Add Symbol to Opposition Dialog should be disabled

  @new
  Scenario: Open Dialog with Locations Created
    Given A project has been opened
    And a theme has been created with a value web
    And a Location has been created
    When the Add Symbol to Opposition Dialog is opened
    Then all locations should be listed in the Add Symbol to Opposition Dialog

  @new
  Scenario: React to New Location Created
    Given A project has been opened
    And a theme has been created with a value web
    And the Add Symbol to Opposition Dialog has been opened
    When A new Location is created
    Then all locations should be listed in the Add Symbol to Opposition Dialog

  @new
  Scenario: React to Location Renamed
    Given A project has been opened
    And a theme has been created with a value web
    And a location called "Home" has been created
    And the Add Symbol to Opposition Dialog has been opened
    When the location "Home" is renamed to "Work"
    Then the Add Symbol to Opposition Dialog should show the location "Home" renamed to "Work"

  @new
  Scenario: React to Location Deleted
    Given A project has been opened
    And a theme has been created with a value web
    And a location called "Home" has been created
    And the Add Symbol to Opposition Dialog has been opened
    When the location "Home" is removed from the story
    Then the Add Symbol to Opposition Dialog should no longer list the location "Home"

  #Rule: Only symbols in the theme should be listed

  @new
  Scenario: Open Dialog Before Any Symbols Created in Theme
    Given A project has been opened
    And a theme has been created with a value web
    When the Add Symbol to Opposition Dialog is opened
    Then no symbols should be listed in the Add Symbol to Opposition Dialog
    But the symbol tab in the Add Symbol to Opposition Dialog should not be disabled

  @new
  Scenario: Open Dialog with Symbols Created in Another Theme
    Given A project has been opened
    And a theme has been created with a value web
    And 2 Themes have been created
    And a symbol has been created in a different theme
    When the Add Symbol to Opposition Dialog is opened
    Then no symbols should be listed in the Add Symbol to Opposition Dialog
    But the symbol tab in the Add Symbol to Opposition Dialog should not be disabled

  @new
  Scenario: Open Dialog with Symbols Created in this Theme
    Given A project has been opened
    And a theme has been created with a value web
    And a symbol has been created in the theme
    When the Add Symbol to Opposition Dialog is opened
    Then all symbols in this theme should be listed in the Add Symbol to Opposition Dialog

  @new
  Scenario: React to New Symbol Created for Theme
    Given A project has been opened
    And a theme has been created with a value web
    And the Add Symbol to Opposition Dialog has been opened
    When a symbol is created in this theme
    Then all symbols in this theme should be listed in the Add Symbol to Opposition Dialog

  @new
  Scenario: Do not react to New Symbol Created in Different Theme
    Given A project has been opened
    And a theme has been created with a value web
    And 2 Themes have been created
    And the Add Symbol to Opposition Dialog has been opened
    When a symbol is created in another theme
    Then the new symbol should not be listed in the Add Symbol to Opposition Dialog

  @new
  Scenario: React to Symbol Renamed
    Given A project has been opened
    And a theme has been created with a value web
    And a symbol called "The Ring" has been created in this theme
    And the Add Symbol to Opposition Dialog has been opened
    When the symbol "The Ring" is renamed to "A Banana"
    Then the Add Symbol to Opposition Dialog should show the symbol "The Ring" renamed to "A Banana"

  @new
  Scenario: React to Symbol Deleted
    Given A project has been opened
    And a theme has been created with a value web
    And a symbol called "The Ring" has been created in this theme
    And the Add Symbol to Opposition Dialog has been opened
    When the symbol "The Ring" is removed from the story
    Then the Add Symbol to Opposition Dialog should no longer list the Symbol "The Ring"

  #Rule: Selecting an item adds that item as a symbol

  @new
  Scenario: Add Symbolic Character to Value Web
    Given A project has been opened
    And a theme has been created with a value web
    And a character called "Bob" has been created
    And the Add Symbol to Opposition Dialog has been opened
    When the character "Bob" is selected in the Add Symbol to Opposition Dialog
    Then the Add Symbol to Opposition Dialog should be closed
    And the character "Bob" should be listed as a symbol in the value opposition

  @new
  Scenario: Add Symbolic Location to Value Web
    Given A project has been opened
    And a theme has been created with a value web
    And a location called "Home" has been created
    And the Add Symbol to Opposition Dialog has been opened
    When the location "Home" is selected in the Add Symbol to Opposition Dialog
    Then the Add Symbol to Opposition Dialog should be closed
    And the location "Home" should be listed as a symbol in the value opposition

  @new
  Scenario: Add Symbol to Value Web
    Given A project has been opened
    And a theme has been created with a value web
    And a symbol called "The Ring" has been created in this theme
    And the Add Symbol to Opposition Dialog has been opened
    When the symbol "The Ring" is selected in the Add Symbol to Opposition Dialog
    Then the Add Symbol to Opposition Dialog should be closed
    And the symbol "The Ring" should be listed as a symbol in the value opposition

  @new
  Scenario: Create Custom Symbol to Add to Value Web
    Given A project has been opened
    And a theme has been created with a value web
    And the Add Symbol to Opposition Dialog has been opened
    When the Create Symbol button is selected in the Add Symbol to Opposition Dialog
    Then the Create Symbol Dialog should be open
    And the Create Symbol Dialog Theme field should not be visible

  #Rule: No effects should take place if the dialog is closed

  @new
  Scenario: Cancel Adding Symbol to Value Web
    Given A project has been opened
    And a theme has been created with a value web
    And the Add Symbol to Opposition Dialog has been opened
    When the Esc key is pressed
    Then the Add Symbol to Opposition Dialog should be closed
    But nothing should be listed as a symbol in the value opposition