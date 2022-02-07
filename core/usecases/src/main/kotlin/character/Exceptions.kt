package com.soyle.stories.usecase.character

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.exceptions.CharacterException
import com.soyle.stories.domain.validation.DuplicateOperationException
import com.soyle.stories.domain.validation.EntityNotFoundException
import java.util.*


data class CharacterDoesNotExist(override val characterId: Character.Id) : CharacterException,
    EntityNotFoundException(characterId.uuid)

class CharacterArcDoesNotExist(val characterId: UUID, val themeId: UUID) : NoSuchElementException()
class CharacterArcSectionDoesNotExist(val characterArcSectionId: UUID) :
    EntityNotFoundException(characterArcSectionId) {
    override fun getLocalizedMessage(): String = "Character Arc Section Does Not Exist $characterArcSectionId"
}

class CharacterArcTemplateSectionDoesNotExist(val characterArcTemplateSectionId: UUID) :
    EntityNotFoundException(characterArcTemplateSectionId)

data class CharacterArcSectionAlreadyInPosition(
    val characterArcSectionId: UUID,
    val index: Int
) : DuplicateOperationException()