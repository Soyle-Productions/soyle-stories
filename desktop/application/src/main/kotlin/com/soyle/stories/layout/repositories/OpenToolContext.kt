package com.soyle.stories.layout.repositories

import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.theme.ThemeRepository

interface OpenToolContext {
	val characterRepository: CharacterRepository
	val themeRepository: ThemeRepository
	val locationRepository: LocationRepository
	val storyEventRepository: StoryEventRepository
	val sceneRepository: SceneRepository
}