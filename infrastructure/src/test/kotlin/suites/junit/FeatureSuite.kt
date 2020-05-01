package suites.junit

import courgette.api.CourgetteOptions
import courgette.api.CourgetteRunLevel
import courgette.api.CucumberOptions
import courgette.api.junit.Courgette
import org.junit.runner.RunWith

@RunWith(Courgette::class)
@CourgetteOptions(
  threads = 10,
  runLevel = CourgetteRunLevel.FEATURE,
  rerunFailedScenarios = true,
  showTestOutput = true,
  reportTargetDir = "build",
  plugin = ["extentreports"],
  cucumberOptions = CucumberOptions(
	features = ["src/test/resources/features"],
	glue = ["com.soyle.stories"],
	tags = ["not @excluded"],
	plugin = [
		"pretty",
		"json:build/cucumber-report/cucumber.json",
		"html:build/cucumber-report/cucumber.html",
		"junit:build/cucumber-report/cucumber.xml"],
	strict = true
  ))
class FeatureSuite