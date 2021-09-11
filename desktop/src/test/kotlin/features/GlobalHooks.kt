package com.soyle.stories.desktop.config.features

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.config.drivers.character.CharacterDriver
import com.soyle.stories.desktop.config.drivers.location.LocationDriver
import com.soyle.stories.desktop.config.drivers.scene.SceneDriver
import com.soyle.stories.desktop.config.drivers.soylestories.SyncThreadTransformer
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.storyevent.`Story Event Robot`
import com.soyle.stories.desktop.config.drivers.theme.ThemeDriver
import com.soyle.stories.desktop.config.locale.LocaleHolder
import com.soyle.stories.desktop.config.soylestories.configureLocalization
import com.soyle.stories.desktop.config.soylestories.configureModules
import com.soyle.stories.desktop.locale.SoyleMessages
import com.soyle.stories.desktop.view.runHeadless
import com.soyle.stories.di.DI
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcTemplate
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.SoyleStories
import io.cucumber.java8.En
import io.cucumber.java8.Scenario
import kotlinx.coroutines.CoroutineScope
import org.junit.jupiter.api.fail
import org.testfx.api.FxToolkit
import java.util.*
import kotlin.concurrent.thread

lateinit var soyleStories: SoyleStories
    private set

class GlobalHooks : En {

    companion object {
        private val closeThread by lazy {
            val close = thread(start = false) {
                Thread.currentThread().interrupt()
            }
            Runtime.getRuntime().addShutdownHook(close)
            close
        }
    }

    private fun synchronizeBackgroundTasks() {
        DI.register(ThreadTransformer::class, ApplicationScope::class, { SyncThreadTransformer() })
    }

    private val primaryTestThread = Thread.getAllStackTraces().keys.find { it.name == "main" }!!

    init {
        Before { scenario: Scenario ->
            if (! FxToolkit.isFXApplicationThreadRunning()) {
                runHeadless()
                closeThread
                SoyleStories.initialization = {
                    configureLocalization()
                    configureModules()
                    synchronizeBackgroundTasks()
                }
                FxToolkit.registerPrimaryStage()
                Thread.getAllStackTraces().keys.find { it.name == "JavaFX Application Thread" }?.let {
                    val currentHandler = it.uncaughtExceptionHandler
                    it.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { t, e ->
                        if (e is NullPointerException && e.stackTrace.any { it.className == "javafx.scene.control.skin.MenuButtonSkinBase" }) {
                            println("Ignored MenuButtonSkinBase NPE.")
                            e.printStackTrace()
                        } else {
                            currentHandler?.uncaughtException(t, e)

                            // sometimes, the gui thread just passed the error directly to the uncaught
                            // exception handler and continues on it's merry way.  Throw again to ensure
                            // that the test thread gets the error too
                            throw e
                        }
                    }
                }
            }
            soyleStories = FxToolkit.setupApplication(SoyleStories::class.java) as SoyleStories
        }

        After { scenario: Scenario ->
            try {
                FxToolkit.cleanupApplication(soyleStories)
                FxToolkit.cleanupStages()
            } catch (t: Throwable) {
                println("Exception after scenario finished:")
                println(t)
            }
        }

        ParameterType<Character?>("character", "character \"(.*?)\"|\"(.*?)\" character") { name: String ->
            CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()).getCharacterByNameOrError(name)
        }
        ParameterType<Location?>("location", "location \"(.*?)\"|\"(.*?)\" location") { name: String ->
            LocationDriver(soyleStories.getAnyOpenWorkbenchOrError()).getLocationByName(name)
        }
        ParameterType<Theme>("theme", "theme \"(.*?)\"|\"(.*?)\" theme") { name: String ->
            ThemeDriver(soyleStories.getAnyOpenWorkbenchOrError()).getThemeByNameOrError(name)
        }
        ParameterType<Theme>("moral argument", "moral argument \"(.*?)\"|\"(.*?)\" moral argument") { name: String ->
            ThemeDriver(soyleStories.getAnyOpenWorkbenchOrError()).getThemeByNameOrError(name)
        }
        ParameterType<com.soyle.stories.domain.scene.Scene>("scene", "scene \"(.*?)\"|\"(.*?)\" scene") { name: String ->
            SceneDriver(soyleStories.getAnyOpenWorkbenchOrError()).getSceneByNameOrError(name)
        }
        ParameterType<StoryEvent>("story event", "story event \"(.*?)\"|\"(.*?)\" story event") { temp: String?, name: String ->
            `Story Event Robot`(soyleStories.getAnyOpenWorkbenchOrError()).getStoryEventByName(name)!!
        }
        ParameterType<CharacterArcTemplateSection>("template", "\"(.*?)\"") { name: String ->
            CharacterArcTemplate.default().sections.single { it.name == name }
        }

        ParameterType("ordinal", "(\\d+)(?:st|nd|rd|th)") { ordinal: String ->
            Regex("(\\d+)").find(ordinal)?.value?.toIntOrNull()?.minus(1)
                ?: error("detected ordinal, but did not find parsable string")
        }

    }

}