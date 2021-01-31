package com.soyle.stories.scene

import com.soyle.stories.common.DuplicateOperationException
import com.soyle.stories.common.EntityNotFoundException
import com.soyle.stories.common.ValidationException
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.theme.Symbol
import java.util.*

abstract class SceneException : Exception()
class SceneDoesNotExist(private val locale: Locale?, val sceneId: UUID): SceneException() {
	constructor(sceneId: UUID) : this (null, sceneId)
	override fun getLocalizedMessage(): String = locale?.sceneDoesNotExist ?: "Scene does not exist $sceneId"
}
class NoSceneExistsWithStoryEventId(val storyEventId: UUID) : SceneException()
class CharacterNotInScene(val sceneId: UUID, val characterId: UUID) : SceneException()
class SceneDoesNotTrackSymbol(val sceneId: Scene.Id, val symbolId: Symbol.Id) : EntityNotFoundException(symbolId.uuid)
{
	override val message: String?
		get() = "$sceneId does not track $symbolId"
}

class SceneAlreadyContainsCharacter(val sceneId: UUID, val characterId: UUID) : DuplicateOperationException()

class SceneAlreadyCoversCharacterArcSection(val sceneId: UUID, val characterId: UUID, val characterArcSectionId: UUID) : DuplicateOperationException()

class CharacterArcSectionIsNotPartOfCharactersArc(val characterId: UUID, val characterArcSectionId: UUID, val expectedCharacterId: UUID) : ValidationException()