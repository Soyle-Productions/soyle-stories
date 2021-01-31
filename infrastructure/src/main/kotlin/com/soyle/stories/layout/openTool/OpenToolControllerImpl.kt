package com.soyle.stories.layout.openTool

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.Scene
import com.soyle.stories.layout.config.dynamic.*
import com.soyle.stories.layout.config.fixed.SceneSymbols
import com.soyle.stories.layout.config.temporary.DeleteSceneRamifications
import com.soyle.stories.layout.config.temporary.ReorderSceneRamifications
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.scene.usecases.listAllScenes.SceneItem
import java.util.*

class OpenToolControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val openTool: OpenTool,
  private val openToolOutputPort: OpenTool.OutputPort
) : OpenToolController {

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
				UUID.fromString(characterId),
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

	override fun openStoryEventDetailsTool(storyEventId: String) {
		threadTransformer.async {
			openTool.invoke(
			  StoryEventDetails(
				UUID.fromString(storyEventId)
			  ),
			  openToolOutputPort
			)
		}
	}

	override fun openDeleteSceneRamificationsTool(sceneId: String) {
		threadTransformer.async {
			openTool.invoke(
			  DeleteSceneRamifications(
				UUID.fromString(sceneId),
				localeManager.getCurrentLocale()
			  ),
			  openToolOutputPort
			)
		}
	}

	override fun openReorderSceneRamificationsTool(sceneId: String, newIndex: Int) {
		threadTransformer.async {
			openTool.invoke(
				ReorderSceneRamifications(
					UUID.fromString(sceneId),
					newIndex,
					localeManager.getCurrentLocale()
				),
				openToolOutputPort
			)
		}
	}

	override fun openSceneDetailsTool(sceneId: String) {
		threadTransformer.async {
			openTool.invoke(
			  SceneDetails(
				UUID.fromString(sceneId),
				localeManager.getCurrentLocale()
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

	override fun openSymbolsInScene(sceneItem: SceneItem?) {
		threadTransformer.async {
			openTool.invoke(SceneSymbols, openToolOutputPort)
		}
	}
}