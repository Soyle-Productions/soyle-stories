package com.soyle.stories.layout.openTool

interface OpenToolController {

	fun openLocationDetailsTool(locationId: String)
	fun openBaseStoryStructureTool(themeId: String, characterId: String)
	fun openCharacterComparison(themeId: String, characterId: String)
	fun openStoryEventDetailsTool(storyEventId: String)
	fun openDeleteSceneRamificationsTool(sceneId: String)
	fun openReorderSceneRamificationsTool(sceneId: String, newIndex: Int)
	fun openSceneDetailsTool(sceneId: String)

}