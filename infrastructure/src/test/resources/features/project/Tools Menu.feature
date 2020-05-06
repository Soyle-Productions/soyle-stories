Feature: Tools Menu

  Background:
    Given A project has been opened

  Scenario Outline: Tool menu shows each project-level tool
    When the Tools Menu is opened
    Then the Tools Menu should display <item>

    Examples:
      | item       |
      | "Characters" |
      | "Locations"  |
      | "Scenes"     |

  Scenario Outline: Open tools are checked
    Given the <option> tool has been opened
    When the Tools Menu is opened
    Then the Tools Menu <option> option should be checked

    Examples:
      | option     |
      | "Characters" |
      | "Locations"  |
      | "Scenes"     |

  Scenario Outline: Closed tools are unchecked
    Given the <option> tool has been closed
    When the Tools Menu is opened
    Then the Tools Menu <option> option should be unchecked

    Examples:
      | option     |
      | "Characters" |
      | "Locations"  |
      | "Scenes"     |

  Scenario Outline: Toggle tool open
    Given the <option> tool has been closed
    And the Tools Menu has been opened
    When the <option> tool item is selected
    Then the <option> tool should be open

    Examples:
      | option     |
      | "Characters" |
      | "Locations"  |
      | "Scenes"     |

  Scenario Outline: Toggle tool closed
    Given the <option> tool has been opened
    And the Tools Menu has been opened
    When the <option> tool item is selected
    Then the <option> tool should be closed

    Examples:
      | option     |
      | "Characters" |
      | "Locations"  |
      | "Scenes"     |
