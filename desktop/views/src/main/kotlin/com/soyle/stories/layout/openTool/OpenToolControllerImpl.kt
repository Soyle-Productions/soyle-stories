package com.soyle.stories.layout.openTool

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.layout.config.dynamic.*
import com.soyle.stories.layout.config.fixed.*
import com.soyle.stories.layout.config.temporary.ReorderSceneRamifications
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.usecase.scene.list.SceneItem
import java.util.*

class OpenToolControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val openTool: OpenTool,
    private val openToolOutputPort: OpenTool.OutputPort
) : OpenToolController, OpenToolController.OpenCharacterToolController, OpenToolController.OpenLocationToolController,
    OpenToolController.OpenSceneToolController, OpenToolController.OpenThemeToolController {

    override val character: OpenToolController.OpenCharacterToolController
        get() = this
    override val location: OpenToolController.OpenLocationToolController
        get() = this
    override val scene: OpenToolController.OpenSceneToolController
        get() = this
    override val theme: OpenToolController.OpenThemeToolController
        get() = this

    override fun openSceneList() {
        threadTransformer.async {
            openTool.invoke(SceneList, openToolOutputPort)
        }
    }

    override fun openRamificationsTool() {
        threadTransformer.async {
            openTool.invoke(Ramifications, openToolOutputPort)
        }
    }

    override fun openLocationDetailsTool(locationId: String) {
        threadTransformer.async {
            openTool.invoke(
                LocationDetails(UUID.fromString(locationId)),
                openToolOutputPort
            )
        }
    }

    override fun openBaseStoryStructureTool(themeId: String, characterId: String) {
        threadTransformer.async {
            openTool.invoke(
                BaseStoryStructure(
                    Character.Id(UUID.fromString(characterId)),
                    UUID.fromString(themeId)
                ),
                openToolOutputPort
            )
        }
    }

    override fun openCharacterValueComparison(themeId: String) {
        val request = CharacterValueComparison(
            UUID.fromString(themeId)
        )
        threadTransformer.async {
            openTool.invoke(request, openToolOutputPort)
        }
    }

    override fun openCentralConflict(themeId: String, characterId: String?) {
        val request = CharacterConflict(
            UUID.fromString(themeId),
            characterId?.let(UUID::fromString)
        )
        threadTransformer.async {
            openTool.invoke(request, openToolOutputPort)
        }
    }

    override fun openMoralArgument(themeId: String) {
        val request = MoralArgument(
            UUID.fromString(themeId),
        )
        threadTransformer.async {
            openTool.invoke(request, openToolOutputPort)
        }
    }

    override fun openDeleteSceneRamificationsTool(sceneId: String) {

    }

    override fun openReorderSceneRamificationsTool(sceneId: String) {
        threadTransformer.async {
            openTool.invoke(
                ReorderSceneRamifications(
                    UUID.fromString(sceneId)
                ),
                openToolOutputPort
            )
        }
    }

    override fun openValueOppositionWeb(themeId: String) {
        threadTransformer.async {
            openTool.invoke(
                ValueOppositionWebs(
                    UUID.fromString(themeId)
                ),
                openToolOutputPort
            )
        }
    }

    override fun openSceneEditor(sceneId: String, proseId: Prose.Id) {
        val request = SceneEditor(
            Scene.Id(UUID.fromString(sceneId)),
            proseId
        )
        threadTransformer.async {
            openTool.invoke(request, openToolOutputPort)
        }
    }

    override fun openSceneOutline() {
        threadTransformer.async {
            openTool.invoke(SceneOutline, openToolOutputPort)
        }
    }

    override fun openSceneCharacters(sceneItem: SceneItem?) {
        threadTransformer.async {
            openTool.invoke(SceneCharacters, openToolOutputPort)
        }
    }

    override fun openSceneLocations(sceneItem: SceneItem?) {
        threadTransformer.async {
            openTool.invoke(SceneSetting, openToolOutputPort)
        }
    }

    override fun openSymbolsInScene(sceneItem: SceneItem?) {
        threadTransformer.async {
            openTool.invoke(SceneSymbols, openToolOutputPort)
        }
    }
}