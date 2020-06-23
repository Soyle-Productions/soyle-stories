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

  @delete-theme
  Scenario: React to Theme Deleted
    Given a Theme has been created
    And the Theme List tool has been opened
    When a theme is deleted
    Then the Theme List Tool should not show the deleted theme

  Scenario Outline: Right-Click on Theme
    Given a Theme has been created
    And the Theme List tool has been opened
    When a Theme is right-clicked
    Then the Theme List Theme Context Menu should be open
    And the Theme List Theme Context Menu should have <option> as an option

    @open-tool
    Examples:
      | option           |
      | "Compare Values" |

    @open-tool
    Examples:
      | option               |
      | "Compare Characters" |

    @create-symbol
    Examples:
      | option          |
      | "Create Symbol" |

    @rename-theme
    Examples:
      | option   |
      | "Rename" |

    @delete-theme
    Examples:
      | option   |
      | "Delete" |

  @open-tool
  Scenario: Open Theme Value Web
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Theme Context Menu has been opened
    When the Theme List Theme Context Menu "Compare Values" option is selected
    Then the Value Web Tool should be open

  @open-tool
  Scenario: Open Character Comparison
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Theme Context Menu has been opened
    When the Theme List Theme Context Menu "Compare Characters" option is selected
    Then the Character Comparison Tool should be open

  @create-symbol
  Scenario: Create Symbol for Theme
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Theme Context Menu has been opened
    When the Theme List Theme Context Menu "Create Symbol" option is selected
    Then the Create Symbol Dialog should be open

  @delete-theme
  Scenario: Delete Theme by Right-Clicking
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Theme Context Menu has been opened
    When the Theme List Theme Context Menu "Delete" option is selected
    Then the Confirm Delete Theme Dialog should be open

  @rename-theme
  Scenario: Rename
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Theme Context Menu has been opened
    When the Theme List Theme Context Menu "Rename" option is selected
    Then the Theme List Rename Theme Text Field should be open

  @rename-theme
  Scenario Outline: Cancel Rename Theme
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Rename Theme Text Field has been opened
    When the Theme rename is cancelled by <action>
    Then the Theme List Rename Theme Text Field should not be open

    Examples:
      | action          |
      | Pressing Escape |
      | Clicking Away   |

  @rename-theme
  Scenario Outline: Commit Rename Theme
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Rename Theme Text Field has been opened
    And a valid Theme name has been entered in the Theme List Rename Theme Field
    When the Theme rename is committed by <action>
    Then the Theme List Rename Theme Text Field should not be open
    And the Theme should be renamed

    Examples:
      | action         |
      | Pressing Enter |
      | Clicking Away  |

  @rename-theme
  Scenario Outline: Fail to Rename Theme
    Given a Theme has been created
    And the Theme List tool has been opened
    And the Theme List Rename Theme Text Field has been opened
    And an invalid Theme name has been entered in the Theme List Rename Theme Field
    When the Theme rename is committed by <action>
    Then the Theme List Rename Theme Text Field should show an error message
    But the Theme should not be renamed

    Examples:
      | action         |
      | Pressing Enter |
      | Clicking Away  |

  @new
  Scenario Outline: Right-Click on Symbol
    Given a symbol has been created
    And the Theme List tool has been opened
    When a symbol is right-clicked
    Then the Theme List Symbol Context Menu should be open
    And the Theme List Symbol Context Menu should have <option> as an option

    Examples:
      | option   |
      | "Rename" |
      | "Delete" |

  @delete-symbol @new
  Scenario: Delete Symbol by Right-Clicking
    Given a symbol has been created
    And the Theme List tool has been opened
    And a symbol has been right-clicked
    When the Theme List Symbol Context Menu "Delete" option is selected
    Then the Confirm Delete Symbol Dialog should be open

  @rename-symbol @new
  Scenario: Rename Symbol
    Given a symbol has been created
    And the Theme List tool has been opened
    And the Theme List Symbol Context Menu has been opened
    When the Theme List Symbol Context Menu "Rename" option is selected
    Then the Theme List Rename Symbol Text Field should be open

  @rename-symbol @new
  Scenario Outline: Cancel Rename Symbol
    Given a symbol has been created
    And the Theme List tool has been opened
    And the Theme List Rename Symbol Text Field has been opened
    When the symbol rename is cancelled by <action>
    Then the Theme List Rename Symbol Text Field should not be open

    Examples:
      | action          |
      | Pressing Escape |
      | Clicking Away   |

  @rename-symbol @new
  Scenario Outline: Commit Rename Symbol
    Given a symbol has been created
    And the Theme List tool has been opened
    And the Theme List Rename Symbol Text Field has been opened
    And a valid symbol name has been entered in the Theme List Rename Symbol Field
    When the symbol rename is committed by <action>
    Then the Theme List Rename Symbol Text Field should not be open
    And the symbol should be renamed

    Examples:
      | action         |
      | Pressing Enter |
      | Clicking Away  |

  @rename-symbol @new
  Scenario Outline: Fail to Rename Symbol
    Given a symbol has been created
    And the Theme List tool has been opened
    And the Theme List Rename Symbol Text Field has been opened
    And an invalid symbol name has been entered in the Theme List Rename Symbol Text Field
    When the symbol rename is committed by <action>
    Then the Theme List Rename Symbol Text Field should show an error message
    But the symbol should not be renamed

    Examples:
      | action         |
      | Pressing Enter |
      | Clicking Away  |

  @delete-theme
  Scenario: Delete Theme by Selection and Delete Button
    Given a Theme has been created
    And the Theme List tool has been opened
    And a Theme has been selected in the Theme List tool
    When the Theme List "Delete" button is selected
    Then the Confirm Delete Theme Dialog should be open

  @delete-symbol @new
  Scenario: Delete Symbol by Selection and Delete Button
    Given a symbol has been created
    And the Theme List tool has been opened
    And a symbol has been selected in the Theme List tool
    When the Theme List "Delete" button is selected
    Then the Confirm Delete Symbol Dialog should be open

  @create-symbol @new
  Scenario: Create Symbol without Theme
    Given the Theme List tool has been opened
    When the Theme List "Create Symbol" button is selected
    Then the Create Symbol Dialog should be open

  @create-symbol
  Scenario: React Symbol Created
    Given a Theme has been created
    And the Theme List tool has been opened
    When a symbol is created
    Then the Theme List Tool should show the new symbol