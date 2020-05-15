package com.soyle.stories.storyevent

import courgette.api.CourgetteOptions
import courgette.api.CourgetteRunLevel
import courgette.api.CucumberOptions
import courgette.api.junit.Courgette
import org.junit.runner.RunWith

@RunWith(Courgette::class)
@CourgetteOptions(
  threads = 1,
  runLevel = CourgetteRunLevel.SCENARIO,
  rerunFailedScenarios = false,
  showTestOutput = true,
  reportTargetDir = "build",
  plugin = ["extentreports"],
  cucumberOptions = CucumberOptions(
	features = ["src/test/resources/features"],
	glue = ["com.soyle.stories"],
	tags = ["not @excluded", "@storyevent"],
	plugin = [
		"pretty",
		"json:build/cucumber-report/cucumber.json",
		"html:build/cucumber-report/cucumber.html",
		"junit:build/cucumber-report/cucumber.xml"],
	strict = true
  ))
class StoryEventSuite