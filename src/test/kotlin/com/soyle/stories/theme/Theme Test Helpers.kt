package com.soyle.stories.theme

import arrow.core.Either
import arrow.core.flatMap
import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.*
import com.soyle.stories.translators.asCharacterArcTemplateSection
import java.util.*

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 4:39 PM
 */

fun takeNoteOfTheme(): Theme = (Theme.takeNoteOf(Project.Id(), "") as Either.Right).b

fun makeTheme(
	id: Theme.Id = Theme.Id(),
	projectId: Project.Id = Project.Id(),
	name: String = "",
	symbols: List<Symbol> = listOf(),
	centralMoralQuestion: String = "",
	includedCharacters: Map<Character.Id, CharacterInTheme> = mapOf(),
	similaritiesBetweenCharacters: Map<Set<Character.Id>, String> = mapOf(),
	valueWebs: List<ValueWeb> = listOf()
): Theme = Theme(
	id, projectId, name, symbols, centralMoralQuestion, includedCharacters, similaritiesBetweenCharacters, valueWebs
)

fun takeNoteOfTheme(expectedId: UUID): Theme =
	makeTheme(id = Theme.Id(expectedId))

val newCharacter = Character(
    Character.Id(UUID.randomUUID()), Project.Id(), "Name"
)
val newArchetype = "Artist"
val newVariationOnMoral = "When you look at it this way..."

val themeWithCharacter = (Theme.takeNoteOf(Project.Id(), "")
	.flatMap { it.includeCharacter(newCharacter) } as Either.Right).b

val themeWithoutCharacter = (Theme.takeNoteOf(Project.Id(), "") as Either.Right).b

fun promoteCharacter(): Either<ThemeException, Theme> {
	val characterInTheme = themeWithCharacter.getMinorCharacterById(newCharacter.id)!!
	return themeWithCharacter
		.promoteCharacter(characterInTheme)
}

fun Theme.includeCharacter(character: Character) = includeCharacter(character, thematicTemplate.sections.map {
	CharacterArcSection(
		CharacterArcSection.Id(
			UUID.randomUUID()
		), character.id, id, it.asCharacterArcTemplateSection(), null, ""
	)
})

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

fun Theme.promoteCharacter(minorCharacter: MinorCharacter) = promoteCharacter(minorCharacter, sectionDifference.map {
	CharacterArcSection(
		CharacterArcSection.Id(
			UUID.randomUUID()
		), minorCharacter.id, id, it, null, ""
	)
})