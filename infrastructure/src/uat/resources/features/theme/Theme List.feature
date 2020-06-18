@theme
Feature: Theme List

  Background:
    Given A project has been opened

  @list-symbols-by-theme
  Scenario: No Themes Yet Created
    When the Theme List tool is opened
    Then the Theme List tool should show a special empty message

  @create-theme
  Scenario: Create First Theme
    Given the Theme List tool has been opened
    When the Theme List Create First Theme button is selected
    Then the Create Theme Dialog should be open

  @list-symbols-by-theme
  Scenario Outline: Some Themes Created
    Given <number> Themes have been created
    When the Theme List tool is opened
    Then the Theme List tool should show all <number> themes

    Examples:
    | number |
    | 2      |
    | 4      |

  @create-theme
  Scenario: Create Another Theme
    Given a Theme has been created
    And the Theme List tool has been opened
    When the Theme List "Create New Theme" button is selected
    Then the Create Theme Dialog should be open

  @create-theme
  Scenario: React to New Theme Created
    Given the Theme List tool has been opened
    When a theme is created
    Then the Theme List Tool should show the new theme

  @new
  Scenario Outline: Right-Click on Theme
    Given a Theme has been created
    And the Theme List tool has been opened
    When a Theme is right-clicked
    Then the Theme List Theme Context Menu should be open
    And the Theme List Theme Context Menu should have <option> as an option

    @open-tool @new @excluded
    Examples:
      | option |
      | "Compare Values" |

    @open-tool @new
    Examples:
      | option |
      | "Compare Characters" |

    @create-symbol @new
    Examples:
      | option |
      | "Create Symbol" |

    @rename-theme @new
    Examples:
      | option |
      | "Rename" |

    @delete-theme @new
    Examples:
      | option |
      | "Delete" |

  @open-tool @new @excluded
  Scenario: Open Theme Value Web

  @open-tool @new @excluded
  Scenario: Open Character Comparison

  @create-symbol @new
  Scenario: Create Symbol for Theme
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Theme Context Menu has been opened
    When the Theme List Theme Context Menu "Create Symbol" option is selected
    Then the Create Symbol Dialog should be open

  @delete-theme @new
  Scenario: Delete Theme by Right-Clicking
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Theme Context Menu has been opened
    When the Theme List Theme Context Menu "Delete" option is selected
    Then the Confirm Delete Theme Dialog should be open

  @rename-theme @new
  Scenario: Rename
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Theme Context Menu has been opened
    When the Theme List Theme Context Menu "Rename" option is selected
    Then the Theme List Rename Theme Text Field should be open

  @rename-theme @new
  Scenario Outline: Cancel Rename Theme
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Rename Theme Text Field has been opened
    When the Theme rename is cancelled by <action>
    Then the Theme List Rename Theme Text Field should not be open

    Examples:
      | action |
      | Pressing Escape |
      | Clicking Away |

  @rename-theme @new
  Scenario Outline: Commit Rename Theme
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Rename Theme Text Field has been opened
    And a valid Theme name has been entered in the Theme List Rename Theme Field
    When the Theme rename is committed by <action>
    Then the Theme List Rename Theme Text Field should not be open
    And the Theme should be renamed

    Examples:
      | action |
      | Pressing Enter |
      | Clicking Away |

  @rename-theme @new
  Scenario Outline: Fail to Rename Theme
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Rename Theme Text Field has been opened
    And an invalid Theme name has been entered in the Theme List Rename Theme Field
    When the Theme rename is committed by <action>
    Then the Theme List Rename Theme Text Field should show an error message
    But the Theme should not be renamed

    Examples:
      | action |
      | Pressing Enter |
      | Clicking Away |

  @new @excluded
  Scenario: Right-Click on Symbol

  @delete-symbol @new @excluded
  Scenario: Delete Symbol by Right-Clicking

  @rename-symbol @new @excluded
  Scenario: Rename Symbol

  @rename-symbol @new @excluded
  Scenario: Cancel Rename Symbol

  @rename-symbol @new @excluded
  Scenario: Commit Rename Symbol

  @rename-symbol @new @excluded
  Scenario: Fail to Rename Symbol

  @delete-theme @new
  Scenario: Delete Theme by Selection and Delete Button
    Given a Theme has been created
    And the Theme List tool has been opened
    And a Theme has been selected in the Theme List tool
    When the Theme List "Delete" button is selected
    Then the Confirm Delete Theme Dialog should be open

  @delete-symbol @new @excluded
  Scenario: Delete Symbol by Selection and Delete Button

  @create-symbol @new @excluded
  Scenario: Create Symbol without Theme