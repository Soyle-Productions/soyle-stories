package com.soyle.stories.characterarc

import com.soyle.stories.common.DuplicateOperationException
import com.soyle.stories.common.EntityNotFoundException
import com.soyle.stories.common.ValidationException
import java.util.*

/**
 * Created by Brendan
 * Date: 2/26/2020
 * Time: 3:35 PM
 */
abstract class CharacterArcException : Exception()

class CharacterArcNameCannotBeBlank(val characterId: UUID, val themeId: UUID) : CharacterArcException()
class CharacterArcDoesNotExist(val characterId: UUID, val themeId: UUID) : CharacterArcException()
class CharacterArcSectionDoesNotExist(val characterArcSectionId: UUID) : CharacterArcException()

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
) : ValidationException()