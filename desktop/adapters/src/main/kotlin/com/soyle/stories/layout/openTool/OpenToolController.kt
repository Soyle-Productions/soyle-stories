package com.soyle.stories.layout.openTool

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem

interface OpenToolController {

	fun openSceneList()
	fun openLocationDetailsTool(locationId: String)
	fun openBaseStoryStructureTool(themeId: String, characterId: String)
	fun openCharacterValueComparison(themeId: String)
	fun openStoryEventDetailsTool(storyEventId: String)
	fun openDeleteSceneRamificationsTool(sceneId: String)
	fun openReorderSceneRamificationsTool(sceneId: String, newIndex: Int)
	fun openSceneDetailsTool(sceneId: String)
	fun openValueOppositionWeb(themeId: String)
	fun openCentralConflict(themeId: String, characterId: String?)
	fun openMoralArgument(themeId: String)
	fun openSceneEditor(sceneId: String, proseId: Prose.Id)
	fun openSymbolsInScene(sceneItem: SceneItem?)

}