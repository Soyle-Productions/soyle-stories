Feature: Story Events Involve Characters

  Background:
    Given a project has been started

  Scenario: No Characters are Involved in a new Story Event
    When I create a story event named "Something Happens"
    Then the "Something Happens" story event should not involve any characters

  Scenario: Involve Character in Story Event
    Given I have created a story event named "Something Happens"
    And I have created a character named "Bob"
    When I involve the "Bob" character in the "Something Happens" story event
    Then the "Something Happens" story event should involve the "Bob" character

  Scenario: Delete Character Involved in Story Event
    Given I have created a story event named "Something Happens"
    And I have created a character named "Bob"
    And I have involved the "Bob" character in the "Something Happens" story event
    When I remove the "Bob" character from the story
    Then the "Something Happens" story event should not involve any characters

  Scenario: Stop Involving Character in Story Event
    Given I have created a story event named "Something Happens"
    And I have created a character named "Bob"
    And I have involved the "Bob" character in the "Something Happens" story event
    When I stop involving the "Bob" character in the "Something Happens" story event
    Then the "Something Happens" story event should not involve any characters
