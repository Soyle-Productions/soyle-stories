/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:47 PM
 */
package com.soyle.stories.entities

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.common.Entity
import com.soyle.stories.entities.theme.*
import com.soyle.stories.theme.CharacterAlreadyIncludedInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.StoryFunctionAlreadyApplied
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.translators.asMinorCharacter
import com.soyle.stories.translators.asThematicSection
import java.util.*

class Theme(
    override val id: Id,
    val centralMoralQuestion: String,
    private val includedCharacters: Map<Character.Id, CharacterInTheme>,
    val similaritiesBetweenCharacters: Map<Set<Character.Id>, String>
) : Entity<Theme.Id> {

    val thematicTemplate: ThematicTemplate
        get() = ThematicTemplate.default()

    private fun copy(
        centralMoralQuestion: String = this.centralMoralQuestion,
        includedCharacters: Map<Character.Id, CharacterInTheme> = this.includedCharacters,
        similaritiesBetweenCharacters: Map<Set<Character.Id>, String> = this.similaritiesBetweenCharacters
    ) = Theme(
        id,
        centralMoralQuestion,
        includedCharacters,
        similaritiesBetweenCharacters
    )

    fun changeCentralMoralQuestion(question: String): Either<ThemeException, Theme> {
        return copy(
            centralMoralQuestion = question
        ).right()
    }

    fun includeCharacter(
        character: Character,
        initialSections: List<CharacterArcSection>
    ): Either<ThemeException, Theme> {
        if (includedCharacters.containsKey(character.id)) return CharacterAlreadyIncludedInTheme(
            character.id.uuid,
            id.uuid
        ).left()

        val characterArcSections = initialSections.map {
            it.asThematicSection()
        }
        val newCharacter = character.asMinorCharacter(characterArcSections)
        val minorCharacters = includedCharacters.values.filterIsInstance<MinorCharacter>()
        val majorCharacters = includedCharacters.values.filterIsInstance<MajorCharacter>().map {
            it.perceiveCharacter(character.id)
        }

        val characters = minorCharacters + majorCharacters

        return copy(
            includedCharacters = (characters + newCharacter).associateBy { it.id },
            similaritiesBetweenCharacters = similaritiesBetweenCharacters + characters.map {
                setOf(it.id, character.id) to ""
            }
        ).right()
    }

    fun removeCharacter(characterId: Character.Id): Either<ThemeException, Theme> {
		verifyCharacterInTheme(characterId)?.let { return it.left() }
        return copy(
            includedCharacters = includedCharacters.minus(characterId)
        ).right()
    }

    fun getIncludedCharacterById(characterId: Character.Id): CharacterInTheme? {
        return includedCharacters[characterId]
    }

    fun getMajorCharacterById(characterId: Character.Id): MajorCharacter? {
        return includedCharacters[characterId] as? MajorCharacter
    }

    fun getMinorCharacterById(characterId: Character.Id): MinorCharacter? =
        includedCharacters[characterId] as? MinorCharacter

    fun containsCharacter(characterId: Character.Id): Boolean =
        includedCharacters.containsKey(characterId)

    fun hasCharacters(): Boolean =
        includedCharacters.isNotEmpty()

    val characters: Collection<CharacterInTheme>
        get() = includedCharacters.values

    fun getSimilarities(characterA: Character.Id, characterB: Character.Id): Either<ThemeException, String> {
		verifyCharacterInTheme(characterA)?.let { return it.left() }
		verifyCharacterInTheme(characterB)?.let { return it.left() }
        val similarities = similaritiesBetweenCharacters[setOf(characterA, characterB)]
        if (similarities == null) {
            throw error("Could not find similarities between $characterA and $characterB")
        }
        return similarities.right()
    }

    fun withCharacterRenamed(character: CharacterInTheme, newName: String): Either<ThemeException, Theme>
    {
        verifyCharacterInTheme(character.id)?.let { return it.left() }
        return copy(
          includedCharacters = includedCharacters.minus(character.id).plus(character.id to character.changeName(newName))
        ).right()
    }

    fun changeSimilarities(
        characterA: Character.Id,
        characterB: Character.Id,
        similarities: String
    ): Either<ThemeException, Theme> {
		verifyCharacterInTheme(characterA)?.let { return it.left() }
		verifyCharacterInTheme(characterB)?.let { return it.left() }
        val key = setOf(characterA, characterB)
        return copy(
            similaritiesBetweenCharacters = similaritiesBetweenCharacters
                .minus(key = key)
                .plus(key to similarities)
        ).right()
    }

    fun changeArchetype(character: CharacterInTheme, archetype: String): Either<ThemeException, Theme> {
		verifyCharacterInTheme(character.id)?.let { return it.left() }
        return copy(
            includedCharacters = includedCharacters
                .minus(character.id)
                .plus(character.id to character.changeArchetype(archetype))
        ).right()
    }

    fun changeVariationOnMoral(character: CharacterInTheme, variation: String): Either<ThemeException, Theme> {
		verifyCharacterInTheme(character.id)?.let { return it.left() }
        return copy(
            includedCharacters = includedCharacters
                .minus(character.id)
                .plus(character.id to character.changeVariationOnMoral(variation))
        ).right()
    }

    fun promoteCharacter(
        character: MinorCharacter,
        additionalSections: List<CharacterArcSection>
    ): Either<ThemeException, Theme> {
		verifyCharacterInTheme(character.id)?.let { return it.left() }
        return copy(
            includedCharacters = includedCharacters
                .minus(character.id)
                .plus(character.id to character.promote(additionalSections))
        ).right()
    }

    private fun MinorCharacter.promote(additionalSections: List<CharacterArcSection>) =
        MajorCharacter(
            id, name, archetype, variationOnMoral, thematicSections + additionalSections.map { it.asThematicSection() },
            CharacterPerspective(
                includedCharacters.keys.toList().minus(id).associateWith {
                    @Suppress("RemoveExplicitTypeArguments")
                    emptyList<StoryFunction>()
                }, emptyMap()
            )
        )

    fun demoteCharacter(character: MajorCharacter): Either<ThemeException, Theme> {
		verifyCharacterInTheme(character.id)?.let { return it.left() }
        return copy(
            includedCharacters = includedCharacters
                .minus(character.id)
                .plus(character.id to character.demote())
        ).right()
    }

    private fun MajorCharacter.demote() =
        MinorCharacter(
            id,
            name,
            archetype,
            variationOnMoral,
            thematicSections
        )

    fun applyStoryFunction(
        majorCharacter: MajorCharacter,
        characterId: Character.Id,
        function: StoryFunction
    ): Either<ThemeException, Theme> {
		verifyCharacterInTheme(majorCharacter.id)?.let { return it.left() }
		verifyCharacterInTheme(characterId)?.let { return it.left() }

        if (majorCharacter.hasStoryFunctionForTargetCharacter(function, characterId)) {
            return StoryFunctionAlreadyApplied(
                id.uuid,
                majorCharacter.id.uuid,
                characterId.uuid,
                function
            ).left()
        }
        return copy(
            includedCharacters = includedCharacters
                .minus(majorCharacter.id)
                .plus(majorCharacter.id to majorCharacter.applyStoryFunction(characterId, function))
        ).right()
    }

    fun clearStoryFunctions(
        characterInTheme: MajorCharacter,
        characterId: Character.Id
    ): Either<ThemeException, Theme> {
		verifyCharacterInTheme(characterInTheme.id)?.let { return it.left() }
		verifyCharacterInTheme(characterId)?.let { return it.left() }
        return copy(
            includedCharacters = includedCharacters
                .minus(characterInTheme.id)
                .plus(characterInTheme.id to characterInTheme.clearStoryFunctions(characterId))
        ).right()
    }

    fun changeAttack(
        characterInTheme: MajorCharacter,
        characterId: Character.Id,
        attack: String
    ): Either<ThemeException, Theme> {
		verifyCharacterInTheme(characterInTheme.id)?.let { return it.left() }
		verifyCharacterInTheme(characterId)?.let { return it.left() }
        return copy(
            includedCharacters = includedCharacters
                .minus(characterInTheme.id)
                .plus(characterInTheme.id to characterInTheme.changeAttack(characterId, attack))
        ).right()
    }

    private fun verifyCharacterInTheme(characterId: Character.Id): CharacterNotInTheme? {
        return if (!includedCharacters.containsKey(characterId)) CharacterNotInTheme(
            id.uuid,
            characterId.uuid
        )
        else null
    }

    data class Id(val uuid: UUID) {
        override fun toString(): String = "Theme(${uuid})"
    }

    companion object {
        fun takeNoteOf(centralMoralQuestion: String = ""): Either<ThemeException, Theme> {
            return Theme(
                Id(UUID.randomUUID()),
                centralMoralQuestion,
                mapOf(),
                mapOf()
            ).right()
        }
    }

}