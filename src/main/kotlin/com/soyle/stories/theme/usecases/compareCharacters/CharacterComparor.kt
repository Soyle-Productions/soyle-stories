package com.soyle.stories.theme.usecases.compareCharacters

import arrow.core.Either
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.entities.theme.ThematicSection
import com.soyle.stories.entities.theme.ThematicTemplateSection
import java.util.*

internal class CharacterComparor(
    private val theme: Theme,
    private val focusCharacter: MajorCharacter?,
    arcSections: List<CharacterArcSection>
) {

    private val arcSections: Map<CharacterArcSection.Id, CharacterArcSection> =
        arcSections.associateBy { it.id }

    private val templateIds = theme.thematicTemplate
        .sections
        .map { it.characterArcTemplateSectionId }.toSet()

    fun compareCharacters(): CompareCharacters.ResponseModel {
        return CompareCharacters.ResponseModel(
            UUID.randomUUID(), listOf()
        )
    }

    private fun thematicTemplateSectionNames(): List<String> =
        theme.thematicTemplate.sections.map(ThematicTemplateSection::name)

    private fun majorCharacterIds(): List<UUID> =
        theme.characters.asSequence()
            .filterIsInstance<MajorCharacter>()
            .map { it.id.uuid }
            .toList()

    private fun summarizeCharacters(): CompareCharacters.CharacterSummaries {
        if (focusCharacter == null) return CompareCharacters.CharacterSummaries(mapOf())
        return (unfocusedCharacterComparisons() + focusCharacter.toCharacterComparison())
            .associateBy { it.id }
            .let(CompareCharacters::CharacterSummaries)
    }

    private fun unfocusedCharacterComparisons(): List<CompareCharacters.CharacterComparisonSummary> =
        theme.characters.asSequence()
            .filter { it.id != focusCharacter!!.id }
            .map { focusCharacter!!.compareToCharacter(it) }
            .toList()

    private fun MajorCharacter.compareToCharacter(
        character: CharacterInTheme
    ): CompareCharacters.CharacterComparisonSummary {
        return character.summarize(
            storyFunctions = getResponseStoryFunctionsForCharacter(character.id),
            attackAgainstHero = getAttacksByCharacter(character.id) ?: "",
            similaritiesToHero = (theme.getSimilarities(this.id, character.id) as Either.Right).b
        )
    }

    private fun MajorCharacter.toCharacterComparison(
    ): CompareCharacters.CharacterComparisonSummary {
        return summarize(
            storyFunctions = listOf(CompareCharacters.StoryFunction.Hero),
            attackAgainstHero = FOCUS_CHARACTER_DOES_NOT_ATTACK_THEMSELVES,
            similaritiesToHero = FOCUS_CHARACTER_IS_NOT_SIMILAR_TO_THEMSELVES
        )
    }

    private fun CharacterInTheme.summarize(
        storyFunctions: List<CompareCharacters.StoryFunction>,
        attackAgainstHero: String,
        similaritiesToHero: String
    ): CompareCharacters.CharacterComparisonSummary {
        return CompareCharacters.CharacterComparisonSummary(
            id = id.uuid,
            name = name,
            archetypes = archetype,
            variationOnMoral = variationOnMoral,
            comparisonSections = thematicSections.valuesInThematicTemplate(),
            storyFunctions = storyFunctions,
            attackAgainstHero = attackAgainstHero,
            similaritiesToHero = similaritiesToHero
        )
    }

    private fun MajorCharacter.getResponseStoryFunctionsForCharacter(targetCharacterId: Character.Id): List<CompareCharacters.StoryFunction> {
        return listOfNotNull(getStoryFunctionsForCharacter(targetCharacterId)?.let {
            CompareCharacters.StoryFunction.valueOf(it.name)
        })
    }

    private fun List<ThematicSection>.valuesInThematicTemplate(): List<Pair<UUID, String>> {
        return asSequence()
            .filter { it.template.characterArcTemplateSectionId in templateIds }
            .map { arcSections.getValue(it.characterArcSectionId) }
            .map { it.id.uuid to it.value }.toList()
    }

    companion object {
        private const val FOCUS_CHARACTER_DOES_NOT_ATTACK_THEMSELVES = ""
        private const val FOCUS_CHARACTER_IS_NOT_SIMILAR_TO_THEMSELVES = ""
    }
}