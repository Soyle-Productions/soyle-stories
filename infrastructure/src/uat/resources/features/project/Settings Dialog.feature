@project @set-dialog-preferences
Feature: Settings Dialog

  Background:
    Given A project has been opened

  Scenario Outline: Enabled dialogs are checked
    Given the <dialog> has not been requested to be hidden
    When the Settings Dialog is opened
    Then the <dialog> option should be checked

    Examples:
      | dialog                         |
      | "Confirm Reorder Scene Dialog" |
      | "Confirm Delete Scene Dialog"  |

    @new
    Examples:
      | dialog                        |
      | "Confirm Delete Theme Dialog" |

  Scenario Outline: Disabled dialogs are unchecked
    Given the <dialog> has been requested to be hidden
    When the Settings Dialog is opened
    Then the <dialog> option should not be checked

    Examples:
      | dialog                         |
      | "Confirm Reorder Scene Dialog" |
      | "Confirm Delete Scene Dialog"  |

    @new
    Examples:
      | dialog                        |
      | "Confirm Delete Theme Dialog" |

  Scenario: Settings Dialog Save button disabled without changes
    When the Settings Dialog is opened
    Then the Settings Dialog "Save" button should be disabled

  Scenario Outline: Changes enable save
    Given the Settings Dialog has been opened
    When the <dialog> option is toggled
    Then the Settings Dialog "Save" button should not be disabled

    Examples:
      | dialog                         |
      | "Confirm Reorder Scene Dialog" |
      | "Confirm Delete Scene Dialog"  |

    @new
    Examples:
      | dialog                        |
      | "Confirm Delete Theme Dialog" |

  Scenario Outline: Close without saving
    Given the Settings Dialog has been opened
    And the <dialog> option has been toggled
    When the Settings Dialog "Cancel" button is selected
    Then the Settings Dialog should be closed
    And no changes should be made to the user dialog preferences

    Examples:
      | dialog                         |
      | "Confirm Reorder Scene Dialog" |
      | "Confirm Delete Scene Dialog"  |

    @new
    Examples:
      | dialog                        |
      | "Confirm Delete Theme Dialog" |

  Scenario Outline: Request to hide
    Given the <dialog> has not been requested to be hidden
    And the Settings Dialog has been opened
    And the <dialog> option has been toggled
    When the Settings Dialog "Save" button is selected
    Then the Settings Dialog should be closed
    And the <dialog> should be requested to be hidden

    Examples:
      | dialog                         |
      | "Confirm Reorder Scene Dialog" |
      | "Confirm Delete Scene Dialog"  |

    @new
    Examples:
      | dialog                        |
      | "Confirm Delete Theme Dialog" |

  Scenario Outline: Undo request to hide
    Given the <dialog> has been requested to be hidden
    And the Settings Dialog has been opened
    And the <dialog> option has been toggled
    When the Settings Dialog "Save" button is selected
    Then the Settings Dialog should be closed
    And the <dialog> should not be requested to be hidden

    Examples:
      | dialog                         |
      | "Confirm Reorder Scene Dialog" |
      | "Confirm Delete Scene Dialog"  |

    @new
    Examples:
      | dialog                        |
      | "Confirm Delete Theme Dialog" |
