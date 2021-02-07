package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.validation.DuplicateOperationException
import com.soyle.stories.domain.validation.EntityNotFoundException
import com.soyle.stories.domain.validation.ValidationException
import java.util.*

abstract class SceneException : Exception()
class SceneDoesNotExist(private val locale: SceneLocale?, val sceneId: UUID): SceneException() {
	constructor(sceneId: UUID) : this (null, sceneId)
	override fun getLocalizedMessage(): String = locale?.sceneDoesNotExist ?: "Scene does not exist $sceneId"
}
class NoSceneExistsWithStoryEventId(val storyEventId: UUID) : SceneException()
class SceneDoesNotTrackSymbol(val sceneId: Scene.Id, val symbolId: Symbol.Id) : EntityNotFoundException(symbolId.uuid)
{
	override val message: String?
		get() = "$sceneId does not track $symbolId"
}

class SceneAlreadyCoversCharacterArcSection(val sceneId: UUID, val characterId: UUID, val characterArcSectionId: UUID) : DuplicateOperationException()

class CharacterArcSectionIsNotPartOfCharactersArc(val characterId: UUID, val characterArcSectionId: UUID, val expectedCharacterId: UUID) : ValidationException()