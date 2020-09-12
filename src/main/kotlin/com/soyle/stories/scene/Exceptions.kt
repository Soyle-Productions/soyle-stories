package com.soyle.stories.scene

import com.soyle.stories.common.DuplicateOperationException
import com.soyle.stories.common.ValidationException
import java.util.*

abstract class SceneException : Exception()
class SceneNameCannotBeBlank(private val locale: Locale) : SceneException() {
	override fun getLocalizedMessage(): String = locale.sceneNameCannotBeBlank
}
class SceneDoesNotExist(private val locale: Locale?, val sceneId: UUID): SceneException() {
	constructor(sceneId: UUID) : this (null, sceneId)
	override fun getLocalizedMessage(): String = locale?.sceneDoesNotExist ?: "Scene does not exist $sceneId"
}
class NoSceneExistsWithStoryEventId(val storyEventId: UUID) : SceneException()
class CharacterNotInScene(val sceneId: UUID, val characterId: UUID) : SceneException()

class SceneAlreadyCoversCharacterArcSection(val sceneId: UUID, val characterId: UUID, val characterArcSectionId: UUID) : DuplicateOperationException()

class CharacterArcSectionIsNotPartOfCharactersArc(val characterId: UUID, val characterArcSectionId: UUID, val expectedCharacterId: UUID) : ValidationException()