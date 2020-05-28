Feature: Settings Dialog

  @excluded
  Scenario Outline: Enabled dialogs are checked
    Given <dialog> has not been requested to be hidden
    When the Settings Dialog is opened
    Then the <dialog> option should be checked

    Examples:
      | dialog                         |
      | "Confirm Reorder Scene Dialog" |
      | "Confirm Delete Scene Dialog"  |

  @excluded
  Scenario Outline: Disabled dialogs are unchecked
    Given <dialog> has been requested to be hidden
    When the Settings Dialog is opened
    Then the <dialog> option should not be checked

    Examples:
      | dialog                         |
      | "Confirm Reorder Scene Dialog" |
      | "Confirm Delete Scene Dialog"  |

  @excluded
  Scenario: Settings Dialog Save button disabled without changes
    When the Settings Dialog is opened
    Then the Settings Dialog "Save" button should be disabled

  @excluded
  Scenario Outline: Changes enable save
    Given the Settings Dialog has been opened
    When the <dialog> option is toggled
    Then the Settings Dialog "Save" button should not be disabled

    Examples:
      | dialog                         |
      | "Confirm Reorder Scene Dialog" |
      | "Confirm Delete Scene Dialog"  |

  @excluded
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

  @excluded
  Scenario Outline: Request to hide
    Given <dialog> has not been requested to be hidden
    And the Settings Dialog has been opened
    And the <dialog> option has been toggled
    When the Settings Dialog "Save" button is selected
    Then the Settings Dialog should be closed
    And the <dialog> should be requested to be hidden

    Examples:
      | dialog                         |
      | "Confirm Reorder Scene Dialog" |
      | "Confirm Delete Scene Dialog"  |

  @excluded
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