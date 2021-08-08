package com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.character.PsychologicalWeakness
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc
import com.soyle.stories.usecase.theme.ThemeRepository

internal class ChangeOptionalCharacterArcSectionValue(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository,
    private val sectionTemplate: CharacterArcTemplateSection,
    private val sectionType: ArcSectionType
) {

    sealed class Response {
        abstract val event: Any
    }
    class Added(override val event: ArcSectionAddedToCharacterArc) : Response()
    class Changed(override val event: ChangedCharacterArcSectionValue) : Response()

    suspend fun changeOptionalSectionValue(themeId: Theme.Id, characterId: Character.Id, newValue: String): Response
    {
        val theme = themeRepository.getThemeOrError(themeId)

        val character = getMajorCharacter(theme, characterId)

        val characterArc = characterArcRepository.getCharacterArcOrError(character.id.uuid, theme.id.uuid)

        val existingArcSection = characterArc.arcSections.find { it.template isSameEntityAs sectionTemplate }

        val updatedCharacterArc = if (existingArcSection == null) {
            characterArc.withArcSection(sectionTemplate, null, newValue)
        } else {
            characterArc.withArcSectionsMapped {
                if (it.template isSameEntityAs sectionTemplate) it.withValue(newValue)
                else it
            }
        }

        characterArcRepository.replaceCharacterArcs(updatedCharacterArc)

        return if (existingArcSection == null) {
            Added(
                ArcSectionAddedToCharacterArc(
                    updatedCharacterArc,
                    updatedCharacterArc.arcSections.single { it.template isSameEntityAs sectionTemplate }
                )
            )
        } else {
            Changed(
                ChangedCharacterArcSectionValue(
                    existingArcSection.id.uuid,
                    character.id.uuid,
                    theme.id.uuid,
                    sectionType,
                    newValue
                )
            )
        }
    }

    private fun getMajorCharacter(
        theme: Theme,
        characterId: Character.Id
    ): CharacterInTheme {
        val character = theme.getIncludedCharacterById(characterId)
            ?: throw CharacterNotInTheme(theme.id.uuid, characterId.uuid)

        character as? MajorCharacter ?: throw CharacterIsNotMajorCharacterInTheme(characterId.uuid, theme.id.uuid)
        return character
    }
}