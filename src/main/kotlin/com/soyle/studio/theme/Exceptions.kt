package com.soyle.studio.theme

import java.util.*

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 4:03 PM
 */

data class CannotCreateCharacterArcForCharacterNotInTheme(val characterId: UUID)
data class CannotExcludeCharactersWithExplicitlyCreatedArcs(
	val charactersWithArcs: List<UUID>,
	val charactersOkToExclude: List<UUID>
)
data class CannotSeparateCharacterArcNotInTheme(val characterId: UUID)
data class CannotSeparateCharacterArcNotYetCreated(val characterId: UUID)

object RequiredArcSectionTypesCannotAllowMultiple
object ArcSectionTypesUsedInCharacterCompCannotAllowMultiple