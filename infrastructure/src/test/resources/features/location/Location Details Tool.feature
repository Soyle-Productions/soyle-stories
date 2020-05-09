Feature: Location Details Tool
  
  Scenario: Update tab name on location rename
    Given the Location Details Tool has been opened
    When the Location associated with the Location Details Tool is renamed
    Then the Location Details Tool tab should be updated with the new Location name
