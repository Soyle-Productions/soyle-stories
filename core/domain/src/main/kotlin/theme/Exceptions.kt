package com.soyle.stories.domain.theme

import com.soyle.stories.domain.theme.characterInTheme.StoryFunction
import com.soyle.stories.domain.validation.DuplicateOperationException
import com.soyle.stories.domain.validation.EntityNotFoundException
import com.soyle.stories.domain.validation.ValidationException
import java.util.*

abstract class ThemeException : Exception() {
    abstract val themeId: UUID
}

class CharacterAlreadyIncludedInTheme(val characterId: UUID, override val themeId: UUID) : ThemeException()
class CharacterNotInTheme(override val themeId: UUID, val characterId: UUID) : ThemeException()
class StoryFunctionAlreadyApplied(
    override val themeId: UUID,
    val perspectiveCharacterId: UUID,
    val appliedCharacterId: UUID,
    val storyFunction: StoryFunction
) : ThemeException()

class CharacterIsNotAnOpponentOfPerspectiveCharacter(
    val themeId: UUID,
    val characterId: UUID,
    val perspectiveCharacterId: UUID
) : ValidationException()

class CharacterIsNotMajorCharacterInTheme(val characterId: UUID, override val themeId: UUID) : ThemeException()
class CharacterIsAlreadyMajorCharacterInTheme(val characterId: UUID, val themeId: UUID) : DuplicateOperationException()
class CharacterArcAlreadyExistsForCharacterInTheme(val characterId: UUID, val themeId: UUID) :
    DuplicateOperationException()

class OppositionValueDoesNotExist(val oppositionValueId: UUID) : EntityNotFoundException(oppositionValueId)

class CharacterAlreadyPromotedInTheme(
    val themeId: UUID,
    val characterId: UUID
) : DuplicateOperationException()