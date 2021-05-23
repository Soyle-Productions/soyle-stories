package com.soyle.stories.desktop.config.features.scene

import com.soyle.stories.desktop.config.drivers.scene.*
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.ThemeDriver
import com.soyle.stories.desktop.config.drivers.theme.createSymbolAndThemeNamed
import com.soyle.stories.desktop.config.drivers.theme.createSymbolWithName
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.sceneEditor.SceneEditorAssertions
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.prose.MentionedCharacterId
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.scene.sceneList.SceneListView
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

class `Scene Prose Steps` : En {

    private val sceneListView: SceneListView
        get() = soyleStories.getAnyOpenWorkbenchOrError()
            .givenSceneListToolHasBeenOpened()

    init {
        Given(
            "I have mentioned the following symbols in the {scene}'s prose"
        ) { scene: Scene, dataTable: DataTable ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            dataTable.asLists().drop(1).map { (themeName, symbolName) ->
                val theme = themeDriver.getThemeByNameOrError(themeName)
                val symbol = theme.symbols.find { it.name == symbolName }!!
                SceneDriver(workbench).givenSceneProseMentionsEntity(scene, symbol.id.mentioned(theme.id), symbolName)
            }
        }
        Given(
            "I have mentioned the {string} symbol from the {theme} in the {scene}'s prose again"
        ) { symbolName: String, theme: Theme, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val symbol = theme.symbols.find { it.name == symbolName }!!
            SceneDriver(workbench).givenSceneProseMentionsEntity(scene, symbol.id.mentioned(theme.id), symbolName)
        }
        Given(
            "I have removed the {string} mention from the {scene}'s prose"
        ) { mentionName: String, scene: Scene ->
            SceneDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenSceneProseDoesNotMention(scene, mentionName)
        }
        Given(
            "I have mentioned the {string} name variant for the {character} in the {scene}'s prose"
        ) { nameVariant: String, character: Character, scene: Scene ->
            SceneDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenSceneProseMentionsEntity(scene, MentionedCharacterId(character.id), nameVariant)
        }


        When(
            "I create a symbol named {string} and a theme named {string} to replace the {string} mention in the {scene}'s prose"
        ) { newSymbolName: String, newThemeName: String, mentionText: String, scene: Scene ->
            sceneListView
                .givenSceneEditorToolHasBeenOpened(scene)
                .givenMentionIsBeingInvestigated(mentionText)
                .givenReplacingInvestigatedMentionWithNewSymbol()
                .createSymbolAndThemeNamed(newThemeName, newSymbolName)
        }
        When(
            "I create symbol named {string} in the {theme} to replace the {string} mention in the {scene}'s prose"
        ) { newSymbolName: String, theme: Theme, mentionText: String, scene: Scene ->
            sceneListView
                .givenSceneEditorToolHasBeenOpened(scene)
                .givenMentionIsBeingInvestigated(mentionText)
                .givenReplacingInvestigatedMentionWithNewSymbol()
                .createSymbolWithName(newSymbolName)
        }
        When(
            "I mention the {string} symbol from the {theme} in the {scene}'s prose"
        ) { symbolName: String, theme: Theme, scene: Scene ->
            sceneListView
                .givenSceneEditorToolHasBeenOpened(scene)
                .run {
                    query(symbolName)
                    selectMentionSuggestion(symbolName)
                }
        }

        Then(
            "I should be able to create a new symbol to replace the {string} mention in the {scene}'s prose"
        ) { mentionText: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    isShowingMentionIssueMenuForMention(mentionText)
                    mentionIssueReplacementMenuHasOption("Create New Symbol")
                }
            }
        }
        Then(
            "I should see the following matching characters for the {scene} in this order"
        ) { scene: Scene, data: DataTable ->
            val rows = data.asLists().drop(1) // first row is header

            val sceneEditor = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)

            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    suggestedMentionListIsVisible()
                    isListingAllStoryElementsInOrder(rows.map {
                        val (nameVariant, fullName) = it
                        if (fullName.isNullOrBlank()) Triple(nameVariant, "character", null)
                        else Triple(nameVariant, "character", fullName)
                    })
                }
            }
        }
    }


}