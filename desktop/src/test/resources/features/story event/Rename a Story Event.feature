Feature: Rename a Story Event

  Background:
    Given I have started a project
    And I have created a story event named "Something Happens"

  Scenario: Attempt to Rename Story Event to the Same Name
    Given I am renaming the "Something Happens" story event
    When I rename the "Something Happens" story event to "Something Happens"
    Then I should still be renaming the "Something Happens" story event

  Scenario: Attempt to Rename Story Event to a Blank Name
    Given I am renaming the "Something Happens" story event
    When I rename the "Something Happens" story event to "  "
    Then I should still be renaming the "Something Happens" story event
    And I should see an error that I cannot rename a story event to have a blank name

  Scenario: Rename Story Event to New, Valid Name
    Given I am renaming the "Something Happens" story event
    When I rename the "Something Happens" story event to "Something Different Happens"
    Then the story event originally named "Something Happens" should be named "Something Different Happens"
