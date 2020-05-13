Feature: Delete Story Event

  Background:
    Given A project has been opened
    And A story event has been created

  Scenario: Populated Story Event List
    Given The Story Event List Tool has been opened
    And the story event right-click menu is open
    When the user clicks the story event list tool right-click menu delete button
    Then the confirm delete story event dialog should be opened
    And the confirm delete story event dialog should show the story event name

  Scenario: Populated Story Event List + button
    Given The Story Event List Tool has been opened
    And a story event has been selected
    When the user clicks the story event list tool delete button
    Then the confirm delete story event dialog should be opened
    And the confirm delete story event dialog should show the story event name

  Scenario: Confirm Delete Dialog
    Given the delete story event dialog has been opened
    When the user clicks the confirm delete story event dialog delete button
    Then the delete story event dialog should be closed

  Scenario: Cancel Delete Dialog
    Given the delete story event dialog has been opened
    When the user clicks the confirm delete story event dialog cancel button
    Then the delete story event dialog should be closed