package com.soyle.stories.theme

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.CoupleOf
import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.*
import com.soyle.stories.entities.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.entities.theme.characterInTheme.MinorCharacter
import com.soyle.stories.entities.theme.oppositionValue.OppositionValue
import com.soyle.stories.entities.theme.valueWeb.ValueWeb
import com.soyle.stories.translators.asCharacterArcTemplateSection
import java.util.*

fun takeNoteOfTheme(): Theme = (Theme.takeNoteOf(Project.Id(), "") as Either.Right).b

fun makeTheme(
	id: Theme.Id = Theme.Id(),
	projectId: Project.Id = Project.Id(),
	name: String = "Theme ${UUID.randomUUID().toString().take(3)}",
	symbols: List<Symbol> = listOf(),
	centralConflict: String = "",
	centralMoralQuestion: String = "",
	includedCharacters: Map<Character.Id, CharacterInTheme> = mapOf(),
	similaritiesBetweenCharacters: Map<CoupleOf<Character.Id>, String> = mapOf(),
	valueWebs: List<ValueWeb> = listOf()
): Theme = Theme(
	id, projectId, name, symbols, centralConflict, centralMoralQuestion, includedCharacters, similaritiesBetweenCharacters, valueWebs
)

fun makeValueWeb(
	id: ValueWeb.Id = ValueWeb.Id(),
	themeId: Theme.Id = Theme.Id(),
	name: String = "Value Web ${UUID.randomUUID().toString().take(3)}",
	oppositions: List<OppositionValue> = listOf()
) = ValueWeb(id, themeId, name, oppositions)

fun makeOppositionValue(
    id: OppositionValue.Id = OppositionValue.Id(),
    name: String = "Opposition Value ${UUID.randomUUID().toString().take(3)}",
    representations: List<SymbolicRepresentation> = listOf()
) = OppositionValue(id, name, representations)

fun takeNoteOfTheme(expectedId: UUID): Theme =
	makeTheme(id = Theme.Id(expectedId))

val newCharacter = makeCharacter(
    Character.Id(UUID.randomUUID()), Project.Id(), "Name"
)
val newArchetype = "Artist"
val newVariationOnMoral = "When you look at it this way..."

val themeWithCharacter = (Theme.takeNoteOf(Project.Id(), "")
	.flatMap { it.withCharacterIncluded(newCharacter.id, newCharacter.name, newCharacter.media).right() } as Either.Right).b

val themeWithoutCharacter = (Theme.takeNoteOf(Project.Id(), "") as Either.Right).b

fun promoteCharacter(): Either<ThemeException, Theme> {
	val characterInTheme = themeWithCharacter.getMinorCharacterById(newCharacter.id)!!
	return themeWithCharacter.withCharacterPromoted(characterInTheme.id).right()
}

fun ThematicSection.asCharacterArcSection(linkedLocation: Location.Id?) = CharacterArcSection(
	characterArcSectionId,
	characterId,
	themeId,
	template.asCharacterArcTemplateSection(),
  linkedLocation,
	""
)

private val sectionDifference = run {
	val thematicTemplateIds = ThematicTemplate.default().sections.map { it.characterArcTemplateSectionId }.toSet()
	CharacterArcTemplate.default().sections.filterNot { it.id in thematicTemplateIds }
}
