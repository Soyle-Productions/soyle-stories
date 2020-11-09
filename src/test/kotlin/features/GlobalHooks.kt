package com.soyle.stories.desktop.config.features

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.config.drivers.character.CharacterDriver
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.SyncThreadTransformer
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.ThemeDriver
import com.soyle.stories.desktop.config.soylestories.configureModules
import com.soyle.stories.di.DI
import com.soyle.stories.di.configureDI
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.SoyleStories
import io.cucumber.java8.En
import io.cucumber.java8.ParameterDefinitionBody
import io.cucumber.java8.Scenario
import javafx.application.Application
import javafx.stage.Stage
import org.testfx.api.FxToolkit
import kotlin.concurrent.thread

lateinit var soyleStories: SoyleStories
    private set

class GlobalHooks : En {

    companion object {
        private val closeThread = thread(start = false) {
            Thread.currentThread().interrupt()
        }
    }

    private fun runHeadless()
    {
        System.setProperty("testfx.robot", "glass")
        System.setProperty("testfx.headless", "true")
        System.setProperty("prism.order", "sw")
        System.setProperty("prism.text", "t2k")
        System.setProperty("java.awt.headless", "true")
        System.setProperty("headless.geometry", "1600x1200-32")
    }

    private fun synchronizeBackgroundTasks() {
        DI.registerTypeFactory(ThreadTransformer::class, ApplicationScope::class) { SyncThreadTransformer() }
    }

    init {
        Before { scenario: Scenario ->
            if (! FxToolkit.isFXApplicationThreadRunning()) {
                runHeadless()
                Runtime.getRuntime().addShutdownHook(closeThread)
                configureModules()
                synchronizeBackgroundTasks()
                FxToolkit.registerPrimaryStage()
            }
            soyleStories = FxToolkit.setupApplication(SoyleStories::class.java) as SoyleStories
        }

        After { scenario: Scenario ->
            FxToolkit.cleanupStages()
            FxToolkit.cleanupApplication(soyleStories)
        }

        ParameterType<Character?>("character", "[A-Z]\\w+") { name: String ->
            CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()).getCharacterByName(name)
        }
        ParameterType<Theme>("theme", "\"(.*?)\"") { name: String ->
            ThemeDriver(soyleStories.getAnyOpenWorkbenchOrError()).getThemeByNameOrError(name)
        }

        ParameterType("ordinal", "(\\d+)(?:st|nd|rd|th)") { ordinal: String ->
            Regex("(\\d+)").find(ordinal)?.value?.toIntOrNull()?.minus(1)
                ?: error("detected ordinal, but did not find parsable string")
        }

    }

}