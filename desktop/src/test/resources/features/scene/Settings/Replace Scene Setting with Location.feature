Feature: Replace Scene Setting with Location

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"
    And I have created the following locations
      | Home | Work | City | Town |

  Scenario: List Available Locations to Replace Scene Setting
    Given I have used the "Home" location as a setting for the "Big Battle" scene
    When I want to replace the "Home" setting in the "Big Battle" scene
    Then the following locations should be listed to replace the "Home" setting in the "Big Battle" scene
      | Work | City | Town |

  Scenario: All Locations in the Project have been Used
    Given I have used the following locations as settings for the "Big Battle" scene
      | Home | Work | City | Town |
    When I want to replace the "Home" setting in the "Big Battle" scene
    Then there should be no available locations to replace the "Home" setting in the "Big Battle" scene

  Scenario Outline: Previously Used Location is Made Available
    Given I have used the following locations as settings for the "Big Battle" scene
      | Home | Work | City |
    And <I have made a setting available>
    When I want to replace the "Home" setting in the "Big Battle" scene
    Then the <location> should be listed to replace the "Home" setting in the "Big Battle" scene

    Examples:
      | location        | I have made a setting available                                                       |
      | "City" location | I have stopped using the "City" setting in the "Big Battle" scene                     |
      | "City" location | I have replaced the "City" setting in the "Big Battle" scene with the "Town" location |

  Scenario: Replace a Scene Setting
    Given I have used the "Home" location as a setting for the "Big Battle" scene
    And I wanted to replace the "Home" setting in the "Big Battle" scene
    When I select the "Work" location to replace the "Home" setting in the "Big Battle" scene
    Then the "Home" location should not be a setting for the "Big Battle" scene
    * the "Home" location should not host a scene named "Big Battle"
    * the "Work" location should be a setting for the "Big Battle" scene
    * the "Big Battle" scene should take place at the "Work" location