@project
Feature: File New Menu

  Background:
    Given A project has been opened

  Scenario Outline: File New Menu shows all domain concepts
    Given the File Menu has been opened
    When the File New Menu is opened
    Then the File New Menu should display <option>

    Examples:
      | option      |
      | "Project"   |
      | "Character" |
      | "Location"  |
      | "Scene"     |

    @theme
    Examples:
      | option   |
      | "Theme"  |
      | "Symbol" |

  Scenario Outline: File New Option opens creation dialog
    Given the File New Menu has been opened
    When the File New <option> option is selected
    Then the Create New <option> Dialog should be open

    Examples:
      | option      |
      | "Project"   |
      | "Character" |
      | "Location"  |
      | "Scene"     |

    @theme
    Examples:
      | option   |
      | "Theme"  |
      | "Symbol" |
