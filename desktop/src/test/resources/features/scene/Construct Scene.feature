Feature: Construct Scene
  Walter wants to construct a scene's conflict and resolution so that he can easily write the scene later

  Background:
    Given I have started a project
    And I have created a scene named "Big Battle"

  Scenario: Read initial Scene Frame values
    When I construct the "Big Battle" scene's frame
    Then the "Big Battle" scene's conflict should be ""
    And the "Big Battle" scene's resolution should be ""

  Scenario: Update Scene Conflict
    Given I am constructing the "Big Battle" scene's frame
    When I set the "Big Battle" scene's conflict to "Two guys wrestling over a melon"
    Then the "Big Battle" scene's conflict should be "Two guys wrestling over a melon"
    But the "Big Battle" scene's resolution should be ""

  Scenario: Update Scene Resolution
    Given I am constructing the "Big Battle" scene's frame
    When I set the "Big Battle" scene's resolution to "They split it in half"
    Then the "Big Battle" scene's resolution should be "They split it in half"
    But the "Big Battle" scene's conflict should be ""

  Scenario: Read Updated Scene Frame Values
    Given I have set the "Big Battle" scene's conflict to "Two guys wrestling over a melon"
    And I have set the "Big Battle" scene's resolution to "They split it in half"
    When I construct the "Big Battle" scene's frame
    Then the "Big Battle" scene's conflict should be "Two guys wrestling over a melon"
    And the "Big Battle" scene's resolution should be "They split it in half"