package com.soyle.stories.layout.openTool

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem

interface OpenToolController {

	val scene: OpenSceneToolController
	val location: OpenLocationToolController
	val character: OpenCharacterToolController
	val theme: OpenThemeToolController

	fun openSceneList()
	fun openLocationDetailsTool(locationId: String)
	fun openBaseStoryStructureTool(themeId: String, characterId: String)
	fun openCharacterValueComparison(themeId: String)
	fun openStoryEventDetailsTool(storyEventId: String)
	fun openDeleteSceneRamificationsTool(sceneId: String)
	fun openReorderSceneRamificationsTool(sceneId: String, newIndex: Int)
	fun openValueOppositionWeb(themeId: String)
	fun openCentralConflict(themeId: String, characterId: String?)
	fun openMoralArgument(themeId: String)
	fun openSceneEditor(sceneId: String, proseId: Prose.Id)
	fun openSymbolsInScene(sceneItem: SceneItem?)

	interface OpenCharacterToolController {
		fun openBaseStoryStructureTool(themeId: String, characterId: String)
	}
	interface OpenLocationToolController {
		fun openLocationDetailsTool(locationId: String)
	}
	interface OpenSceneToolController {
		fun openSceneList()
		fun openDeleteSceneRamificationsTool(sceneId: String)
		fun openReorderSceneRamificationsTool(sceneId: String, newIndex: Int)
		fun openSceneEditor(sceneId: String, proseId: Prose.Id)
		fun openSymbolsInScene(sceneItem: SceneItem?)
		fun openSceneLocations(sceneItem: SceneItem?)
		fun openSceneCharacters(sceneItem: SceneItem?)
		fun openSceneOutline()
	}
	interface OpenThemeToolController {
		fun openCharacterValueComparison(themeId: String)
		fun openValueOppositionWeb(themeId: String)
		fun openCentralConflict(themeId: String, characterId: String?)
		fun openMoralArgument(themeId: String)
	}

}