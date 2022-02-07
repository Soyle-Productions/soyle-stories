package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.validation.DuplicateOperationException
import com.soyle.stories.domain.validation.EntityNotFoundException
import com.soyle.stories.domain.validation.ValidationException
import java.util.*

abstract class SceneException : Exception()
class SceneDoesNotExist(val sceneId: UUID): SceneException() {
	override fun getLocalizedMessage(): String = "Scene does not exist $sceneId"

	override fun equals(other: Any?): Boolean {
		if (other !is SceneDoesNotExist) return false
		return other.sceneId == sceneId
	}
}
class NoSceneExistsWithStoryEventId(val storyEventId: UUID) : SceneException()
class SceneDoesNotTrackSymbol(val sceneId: Scene.Id, val symbolId: Symbol.Id) : EntityNotFoundException(symbolId.uuid)
{
	override val message: String?
		get() = "$sceneId does not track $symbolId"
}
class SceneDoesNotUseLocation(val sceneId: Scene.Id, val locationId: Location.Id) : EntityNotFoundException(locationId.uuid)
{
	override val message: String?
		get() = "$sceneId does not use $locationId"
}

class SceneAlreadyCoversCharacterArcSection(val sceneId: UUID, val characterId: UUID, val characterArcSectionId: UUID) : DuplicateOperationException()

class CharacterArcSectionIsNotPartOfCharactersArc(val characterId: UUID, val characterArcSectionId: UUID, val expectedCharacterId: UUID) : ValidationException()