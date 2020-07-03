package com.soyle.stories.theme

import com.soyle.stories.common.DuplicateOperationException
import com.soyle.stories.common.EntityNotFoundException
import com.soyle.stories.common.ValidationException
import com.soyle.stories.entities.theme.StoryFunction
import java.util.*

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 4:03 PM
 */
abstract class ThemeException : Exception() {
    abstract val themeId: UUID
}

object ThemeNameCannotBeBlank : Exception()

class CharacterAlreadyIncludedInTheme(val characterId: UUID, override val themeId: UUID) : ThemeException()
class CharacterNotInTheme(override val themeId: UUID, val characterId: UUID) : ThemeException()
class StoryFunctionAlreadyApplied(
    override val themeId: UUID,
    val perspectiveCharacterId: UUID,
    val appliedCharacterId: UUID,
    val storyFunction: StoryFunction
) : ThemeException()

class ThemeDoesNotExist(override val themeId: UUID) : ThemeException()
class CharacterIsNotMajorCharacterInTheme(val characterId: UUID, override val themeId: UUID) : ThemeException()
class CharacterIsAlreadyMajorCharacterInTheme(val characterId: UUID, override val themeId: UUID) : ThemeException()
class CharacterArcAlreadyExistsForCharacterInTheme(val characterId: UUID, override val themeId: UUID) : ThemeException()


data class CannotCreateCharacterArcForCharacterNotInTheme(val characterId: UUID)
data class CannotCreateCharacterArcIfAlreadyCreatedInTheme(val characterId: UUID)
data class CannotExcludeCharactersWithACharacterArc(
    val charactersWithArcs: List<UUID>,
    val charactersOkToExclude: List<UUID>
)

data class CannotSeparateCharacterArcNotInTheme(val characterId: UUID)
data class CannotSeparateCharacterArcNotYetCreated(val characterId: UUID)

object RequiredArcSectionTypesCannotAllowMultiple
object ArcSectionTypesUsedInCharacterCompCannotAllowMultiple

class SymbolDoesNotExist(val symbolId: UUID) : EntityNotFoundException(symbolId)
class SymbolAlreadyHasName(val symbolId: UUID, val symbolName: String) : DuplicateOperationException()

class ValueWebDoesNotExist(val valueWebId: UUID) : EntityNotFoundException(valueWebId)
class OppositionValueDoesNotExist(val oppositionValueId: UUID) : EntityNotFoundException(oppositionValueId)
object OppositionValueNameCannotBeBlank : ValidationException()
class OppositionValueAlreadyHasName(val oppositionValueId: UUID, val oppositionValueName: String) : DuplicateOperationException()
class ValueWebDoesNotContainOppositionValue(val valueWebId: UUID, val oppositionValueId: UUID) : EntityNotFoundException(oppositionValueId)