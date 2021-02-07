package com.soyle.stories.usecase.character

import com.soyle.stories.domain.validation.DuplicateOperationException
import com.soyle.stories.domain.validation.EntityNotFoundException
import java.util.*


class CharacterDoesNotExist(val characterId: UUID) : EntityNotFoundException(characterId)
class CharacterArcDoesNotExist(val characterId: UUID, val themeId: UUID) : NoSuchElementException()
class CharacterArcSectionDoesNotExist(val characterArcSectionId: UUID) : EntityNotFoundException(characterArcSectionId)
class CharacterArcTemplateSectionDoesNotExist(val characterArcTemplateSectionId: UUID) :
    EntityNotFoundException(characterArcTemplateSectionId)
data class CharacterArcSectionAlreadyInPosition(
    val characterArcSectionId: UUID,
    val index: Int
) : DuplicateOperationException()