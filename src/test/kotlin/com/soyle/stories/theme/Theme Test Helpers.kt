package com.soyle.stories.theme

import arrow.core.Either
import arrow.core.flatMap
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.CharacterArcTemplate
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.MinorCharacter
import com.soyle.stories.entities.theme.ThematicSection
import com.soyle.stories.entities.theme.ThematicTemplate
import com.soyle.stories.translators.asCharacterArcTemplateSection
import java.util.*

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 4:39 PM
 */

fun takeNoteOfTheme() = (Theme.takeNoteOf() as Either.Right).b

fun takeNoteOfTheme(expectedId: UUID) = takeNoteOfTheme().let {
	Theme(Theme.Id(expectedId), it.centralMoralQuestion, it.characters.associateBy { it.id }, it.similaritiesBetweenCharacters)
}

val newCharacter = Character(
    Character.Id(UUID.randomUUID()), UUID.randomUUID(), "Name"
)
val newArchetype = "Artist"
val newVariationOnMoral = "When you look at it this way..."

val themeWithCharacter = (Theme.takeNoteOf()
	.flatMap { it.includeCharacter(newCharacter) } as Either.Right).b

val themeWithoutCharacter = (Theme.takeNoteOf() as Either.Right).b

fun promoteCharacter(): Either<ThemeException, Theme> {
	val characterInTheme = themeWithCharacter.getMinorCharacterById(newCharacter.id)!!
	return themeWithCharacter
		.promoteCharacter(characterInTheme)
}

fun Theme.includeCharacter(character: Character) = includeCharacter(character, thematicTemplate.sections.map {
	CharacterArcSection(
		CharacterArcSection.Id(
			UUID.randomUUID()
		), character.id, id, it.asCharacterArcTemplateSection(), ""
	)
})

fun ThematicSection.asCharacterArcSection() = CharacterArcSection(
	characterArcSectionId,
	characterId,
	themeId,
	template.asCharacterArcTemplateSection(),
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
		), minorCharacter.id, id, it, ""
	)
})