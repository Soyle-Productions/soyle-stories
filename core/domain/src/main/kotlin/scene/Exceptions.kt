package com.soyle.stories.domain.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.validation.DuplicateOperationException
import com.soyle.stories.domain.validation.EntityNotFoundException
import com.soyle.stories.domain.validation.ValidationException
import java.util.*
class SceneDoesNotIncludeCharacter(val sceneId: Scene.Id, val characterId: Character.Id) : EntityNotFoundException(characterId.uuid)
{
	override val message: String?
		get() = "$sceneId does not track $characterId"
}
class SceneDoesNotTrackSymbol(val sceneId: Scene.Id, val symbolId: Symbol.Id) : EntityNotFoundException(symbolId.uuid)
{
	override val message: String?
		get() = "$sceneId does not track $symbolId"
}
class SceneDoesNotUseLocation(val sceneId: Scene.Id, val locationId: Location.Id) : EntityNotFoundException(locationId.uuid) {

	override val message: String?
		get() = "$sceneId does not use $locationId"
}

class SceneAlreadyContainsCharacter(val sceneId: UUID, val characterId: UUID) : DuplicateOperationException()

class SceneAlreadyCoversCharacterArcSection(val sceneId: UUID, val characterId: UUID, val characterArcSectionId: UUID) : DuplicateOperationException()

class CharacterArcSectionIsNotPartOfCharactersArc(val characterId: UUID, val characterArcSectionId: UUID, val expectedCharacterId: UUID) : ValidationException()