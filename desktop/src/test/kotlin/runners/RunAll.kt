package com.soyle.stories.desktop.config.runners

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["src/test/resources/features/"],
    glue = ["classpath:com.soyle.stories.desktop.config.features"],
    plugin = ["html:target.html"],
)
class RunAll