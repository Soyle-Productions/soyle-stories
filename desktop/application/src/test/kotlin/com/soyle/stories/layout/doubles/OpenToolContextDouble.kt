package com.soyle.stories.layout.doubles

import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.usecase.repositories.*

class OpenToolContextDouble : OpenToolContext {

	override val characterRepository: CharacterRepositoryDouble = CharacterRepositoryDouble()

	override val locationRepository: LocationRepositoryDouble = LocationRepositoryDouble()

	override val storyEventRepository: StoryEventRepositoryDouble = StoryEventRepositoryDouble()

	override val themeRepository: ThemeRepositoryDouble = ThemeRepositoryDouble()

	override val sceneRepository: SceneRepositoryDouble = SceneRepositoryDouble()
}