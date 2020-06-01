package com.soyle.stories.layout.repositories

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import com.soyle.stories.theme.repositories.ThemeRepository

interface OpenToolContext {
	val characterRepository: CharacterRepository
	val themeRepository: ThemeRepository
	val locationRepository: LocationRepository
	val storyEventRepository: StoryEventRepository
	val sceneRepository: SceneRepository
}