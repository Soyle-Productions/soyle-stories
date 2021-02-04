package com.soyle.stories.characterarc

import com.soyle.stories.common.DuplicateOperationException
import com.soyle.stories.common.EntityNotFoundException
import com.soyle.stories.common.ValidationException
import java.util.*

abstract class CharacterArcException : Exception()

class CharacterArcNameCannotBeBlank(val characterId: UUID, val themeId: UUID) : CharacterArcException()
class CharacterArcDoesNotExist(val characterId: UUID, val themeId: UUID) : CharacterArcException()
class CharacterArcSectionDoesNotExist(val characterArcSectionId: UUID) : CharacterArcException()

data class CharacterArcSectionNotInMoralArgument(
    val characterArcSectionId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val arcId: UUID
) : EntityNotFoundException(characterArcSectionId)

data class CharacterArcSectionAlreadyInPosition(
    val characterArcSectionId: UUID,
    val index: Int
) : DuplicateOperationException()

class CharacterArcTemplateSectionDoesNotExist(val characterArcTemplateSectionId: UUID) :
    EntityNotFoundException(characterArcTemplateSectionId)

class CharacterArcAlreadyContainsMaximumNumberOfTemplateSection(
    val arcId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val templateSectionId: UUID,
) : DuplicateOperationException()

class TemplateSectionIsNotPartOfArcTemplate(
    val arcId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val templateSectionId: UUID
) : EntityNotFoundException(templateSectionId)

data class ArcTemplateSectionIsNotMoral(
    val arcId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val templateSectionId: UUID
) : ValidationException()