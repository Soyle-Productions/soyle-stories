package com.soyle.stories.domain.theme

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcTemplate
import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.media.Media
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.domain.theme.characterInTheme.MinorCharacter
import com.soyle.stories.domain.theme.characterInTheme.StoryFunction
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.Couple
import com.soyle.stories.domain.validation.CoupleOf
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

class Theme(
    override val id: Id,
    val projectId: Project.Id,
    val name: String,
    val symbols: List<Symbol>,
    val centralConflict: String,
    val centralMoralProblem: String,
    val themeLine: String,
    val thematicRevelation: String,
    private val includedCharacters: Map<Character.Id, CharacterInTheme>,
    val similaritiesBetweenCharacters: Map<CoupleOf<Character.Id>, String>,
    val valueWebs: List<ValueWeb>
) : Entity<Theme.Id> {

    constructor(
        projectId: Project.Id,
        name: String,
        symbols: List<Symbol> = listOf(),
        centralConflict: String = "",
        centralMoralProblem: String = "",
        themeLine: String = "",
        thematicRevelation: String = ""
    ) : this(Id(), projectId, name, symbols, centralConflict, centralMoralProblem, themeLine, thematicRevelation, mapOf(), mapOf(), listOf())

    val thematicTemplate: ThematicTemplate
        get() = ThematicTemplate.default()

    private fun copy(
        name: String = this.name,
        symbols: List<Symbol> = this.symbols,
        centralConflict: String = this.centralConflict,
        centralMoralProblem: String = this.centralMoralProblem,
        themeLine: String = this.themeLine,
        thematicRevelation: String = this.thematicRevelation,
        includedCharacters: Map<Character.Id, CharacterInTheme> = this.includedCharacters,
        similaritiesBetweenCharacters: Map<CoupleOf<Character.Id>, String> = this.similaritiesBetweenCharacters,
        valueWebs: List<ValueWeb> = this.valueWebs
    ) = Theme(
        id,
        projectId,
        name,
        symbols,
        centralConflict,
        centralMoralProblem,
        themeLine,
        thematicRevelation,
        includedCharacters,
        similaritiesBetweenCharacters,
        valueWebs
    )

    fun withName(name: String) = copy(name = name)
    fun withCentralConflict(centralConflict: String) = copy(centralConflict = centralConflict)

    fun withSymbol(symbol: Symbol) = copy(symbols = symbols + symbol)
    fun withoutSymbol(symbolId: Symbol.Id) = copy(symbols = symbols.filterNot { it.id == symbolId })

    fun withValueWeb(valueWeb: ValueWeb) = copy(valueWebs = valueWebs + valueWeb)
    fun withoutValueWeb(valueWebId: ValueWeb.Id) = copy(valueWebs = valueWebs.filterNot { it.id == valueWebId })
    fun withReplacedValueWeb(valueWeb: ValueWeb) = copy(valueWebs = valueWebs.filterNot { it.id == valueWeb.id } + valueWeb)

    fun withValueWeb(name: NonBlankString): Pair<Theme, ValueWeb> {
        val valueWeb = ValueWeb(id, name)
        return copy(valueWebs = valueWebs + valueWeb) to valueWeb
    }

    @Deprecated("Use of arrow", replaceWith = ReplaceWith("this.withMoralProblem(question).right()"))
    fun changeCentralMoralQuestion(question: String): Either<Exception, Theme> = withMoralProblem(question).right()

    fun withMoralProblem(moralProblem: String): Theme = copy(centralMoralProblem = moralProblem)

    fun withThemeLine(themeLine: String): Theme = copy(themeLine = themeLine)

    fun withThematicRevelation(revelation: String): Theme = copy(thematicRevelation = revelation)

    fun withCharacterIncluded(character: Character) = withCharacterIncluded(
        character.id,
        character.names.displayName.value,
        character.media
    )
    fun withCharacterIncluded(characterId: Character.Id, characterName: String, characterMediaId: Media.Id?): Theme
    {
        mustNotContainCharacter(characterId)

        val newCharacter = MinorCharacter(
            id,
            characterId,
            characterName
        )
        val minorCharacters = includedCharacters.values.filterIsInstance<MinorCharacter>()
        val majorCharacters = includedCharacters.values.filterIsInstance<MajorCharacter>().map {
            it.perceiveCharacter(characterId)
        }
        val characters = minorCharacters + majorCharacters

        return copy(
            includedCharacters = (characters + newCharacter).associateBy { it.id },
            similaritiesBetweenCharacters = similaritiesBetweenCharacters + characters.map {
                Couple(it.id, characterId) to ""
            }
        )
    }

    fun withoutCharacter(characterId: Character.Id): Theme {
        mustContainCharacter(characterId)
        val minorCharacters = includedCharacters.values.filterIsInstance<MinorCharacter>()
        val majorCharacters = includedCharacters.values.filterIsInstance<MajorCharacter>().map {
            it.ignoreCharacter(characterId)
        }
        val characters = minorCharacters + majorCharacters
        return copy(
            includedCharacters = characters.filterNot { it.id == characterId }.associateBy { it.id },
            similaritiesBetweenCharacters = similaritiesBetweenCharacters.filterNot {
                it.key.contains(characterId)
            },
            valueWebs = valueWebs.map {
                if (it.hasRepresentation(characterId.uuid)) it.withoutRepresentation(characterId.uuid)
                else it
            }
        )
    }

    fun withCharacterChangeAs(characterId: Character.Id, change: String): Theme
    {
        mustContainCharacter(characterId)
        val majorCharacter = getMajorCharacter(characterId)

        return copy(
            includedCharacters = characters
                .filterNot { it.id == characterId }
                .plus(majorCharacter.withCharacterChangeAs(change))
                .associateBy { it.id }
        )
    }

    /**
     * Sets the story function for the character with the provided [characterId] from the perspective of the
     * character with the provided [majorCharacterId].  If either character is not in the theme, it will throw a
     * [CharacterNotInTheme] error.  If the [majorCharacterId] does not represent a major character in this theme,
     * a [CharacterIsNotMajorCharacterInTheme] error will be thrown.
     *
     * @return A copy of this [Theme] with the story function applied to the character with the [characterId] from the
     * perspective of the character with the provided [majorCharacterId]
     *
     * @throws CharacterIsNotMajorCharacterInTheme if either character is not in this theme
     * @throws CharacterNotInTheme if the [majorCharacterId] does not represent a major character in this theme
     */
    fun withCharacterAsStoryFunctionForMajorCharacter(characterId: Character.Id, storyFunction: StoryFunction, majorCharacterId: Character.Id): Theme
    {
        mustContainCharacter(characterId)
        mustContainCharacter(majorCharacterId)
        val majorCharacter = getMajorCharacter(majorCharacterId)

        if (majorCharacter.hasStoryFunctionForTargetCharacter(storyFunction, characterId)) {
            throw StoryFunctionAlreadyApplied(
                id.uuid,
                majorCharacter.id.uuid,
                characterId.uuid,
                storyFunction
            )
        }
        return copy(
            includedCharacters = includedCharacters
                .minus(majorCharacter.id)
                .plus(majorCharacter.id to majorCharacter.applyStoryFunction(characterId, storyFunction))
        )
    }

    fun withCharacterAttackingMajorCharacter(characterId: Character.Id, attack: String, majorCharacterId: Character.Id): Theme
    {
        mustContainCharacter(characterId)
        mustContainCharacter(majorCharacterId)
        val majorCharacter = getMajorCharacter(majorCharacterId)

        return copy(
            includedCharacters = includedCharacters
                .minus(majorCharacter.id)
                .plus(majorCharacter.id to majorCharacter.changeAttack(characterId, attack))
        )
    }

    fun withCharactersSimilarToEachOther(characterIds: CoupleOf<Character.Id>, similarities: String): Theme
    {
        characterIds.forEach {
            mustContainCharacter(it)
        }
        return copy(
            similaritiesBetweenCharacters = similaritiesBetweenCharacters
                .minus(key = characterIds)
                .plus(characterIds to similarities)
        )
    }

    fun withCharacterHoldingPosition(characterId: Character.Id, position: String): Theme
    {
        mustContainCharacter(characterId)
        return copy(
            includedCharacters = includedCharacters
                .minus(characterId)
                .plus(characterId to getIncludedCharacterById(characterId)!!.changePosition(position))
        )
    }

    private fun mustNotContainCharacter(characterId: Character.Id) {
        if (containsCharacter(characterId)) {
            throw CharacterAlreadyIncludedInTheme(characterId.uuid, id.uuid)
        }
    }

    private fun mustContainCharacter(characterId: Character.Id) {
        if (! containsCharacter(characterId)) {
            throw CharacterNotInTheme(id.uuid, characterId.uuid)
        }
    }

    private fun getMajorCharacter(characterId: Character.Id) =
        (getMajorCharacterById(characterId)
            ?: throw CharacterIsNotMajorCharacterInTheme(characterId.uuid, id.uuid))


    @Deprecated("Use of arrow", replaceWith = ReplaceWith("this.withoutCharacter(characterId).right()"))
    fun removeCharacter(characterId: Character.Id): Either<Exception, Theme> {
		try {
            mustContainCharacter(characterId)
        } catch (c: CharacterNotInTheme) {
            return c.left()
        }
        return copy(
            includedCharacters = includedCharacters.minus(characterId)
        ).right()
    }

    fun getIncludedCharacterById(characterId: Character.Id): CharacterInTheme? {
        return includedCharacters[characterId]
    }

    fun getIncludedCharacterByIdOrError(characterId: Character.Id): CharacterInTheme {
        return includedCharacters[characterId] ?: throw CharacterNotInTheme(id.uuid, characterId.uuid)
    }

    fun getMajorCharacterById(characterId: Character.Id): MajorCharacter? {
        return includedCharacters[characterId] as? MajorCharacter
    }

    fun getMajorCharacterByIdOrError(characterId: Character.Id): MajorCharacter {
        return getIncludedCharacterByIdOrError(characterId) as? MajorCharacter
            ?: throw CharacterIsNotMajorCharacterInTheme(characterId.uuid, id.uuid)
    }

    fun getMinorCharacterById(characterId: Character.Id): MinorCharacter? =
        includedCharacters[characterId] as? MinorCharacter

    fun containsCharacter(characterId: Character.Id): Boolean =
        includedCharacters.containsKey(characterId)

    fun hasCharacters(): Boolean =
        includedCharacters.isNotEmpty()

    val characters: Collection<CharacterInTheme>
        get() = includedCharacters.values

    @Deprecated("Use of arrow", replaceWith = ReplaceWith("this.getSimilaritiesBetweenCharacters(characterA, characterB).right()"))
    fun getSimilarities(characterA: Character.Id, characterB: Character.Id): Either<Exception, String> {
		try {
            mustContainCharacter(characterA)
            mustContainCharacter(characterB)
        } catch (c: CharacterNotInTheme) {
            return c.left()
        }
        val similarities = similaritiesBetweenCharacters[Couple(characterA, characterB)]
            ?: throw error("Could not find similarities between $characterA and $characterB")
        return similarities.right()
    }

    fun getSimilaritiesBetweenCharacters(characterA: Character.Id, characterB: Character.Id): String {
        mustContainCharacter(characterA)
        mustContainCharacter(characterB)
        return similaritiesBetweenCharacters[Couple(characterA, characterB)]
            ?: throw error("Could not find similarities between $characterA and $characterB")
    }

    fun withCharacterRenamed(character: CharacterInTheme, newName: String): Either<Exception, Theme>
    {
        try {
            mustContainCharacter(character.id)
        } catch (c: CharacterNotInTheme) {
            return c.left()
        }
        return copy(
          includedCharacters = includedCharacters.minus(character.id).plus(character.id to character.changeName(newName))
        ).right()
    }

    @Deprecated("Use of arrow", replaceWith = ReplaceWith("this.withCharactersSimilarToEachOther(characterA, characterB, similarities).right()"))
    fun changeSimilarities(
        characterA: Character.Id,
        characterB: Character.Id,
        similarities: String
    ): Either<Exception, Theme> {
		try {
            mustContainCharacter(characterA)
            mustContainCharacter(characterB)
        } catch (c: CharacterNotInTheme) {
            return c.left()
        }
        val key = Couple(characterA, characterB)
        return copy(
            similaritiesBetweenCharacters = similaritiesBetweenCharacters
                .minus(key = key)
                .plus(key to similarities)
        ).right()
    }

    fun withCharactersSimilarToEachOther(
        characterA: Character.Id,
        characterB: Character.Id,
        similarities: String
    ): Theme
    {
        mustContainCharacter(characterA)
        mustContainCharacter(characterB)
        val key = Couple(characterA, characterB)
        return copy(
            similaritiesBetweenCharacters = similaritiesBetweenCharacters
                .minus(key = key)
                .plus(key to similarities)
        )
    }

    @Deprecated("Use of arrow", replaceWith = ReplaceWith("this.withCharacterWithArchetype(character, archetype).right()"))
    fun changeArchetype(character: CharacterInTheme, archetype: String): Either<Exception, Theme> {
        return try {
            withCharacterWithArchetype(character, archetype).right()
        } catch (c: CharacterNotInTheme) {
            c.left()
        }
    }

    fun withCharacterWithArchetype(character: CharacterInTheme, archetype: String): Theme {
        mustContainCharacter(character.id)
        return copy(
            includedCharacters = includedCharacters
                .minus(character.id)
                .plus(character.id to character.changeArchetype(archetype))
        )
    }

    @Deprecated("Use of arrow", replaceWith = ReplaceWith("this.withCharacterWithVariationOnMoral(character, variation).right()"))
    fun changeVariationOnMoral(character: CharacterInTheme, variation: String): Either<Exception, Theme> {
		return try {
            withCharacterWithVariationOnMoral(character, variation).right()
        } catch (c: CharacterNotInTheme) {
            c.left()
        }
    }

    fun withCharacterWithVariationOnMoral(character: CharacterInTheme, variation: String): Theme
    {
        mustContainCharacter(character.id)
        return copy(
            includedCharacters = includedCharacters
                .minus(character.id)
                .plus(character.id to character.changeVariationOnMoral(variation))
        )
    }

    fun withCharacterPromoted(characterId: Character.Id): Theme
    {
        if (! containsCharacter(characterId)) {
            throw CharacterNotInTheme(id.uuid, characterId.uuid)
        }
        if (characterIsMajorCharacter(characterId)) {
            throw CharacterAlreadyPromotedInTheme(id.uuid, characterId.uuid)
        }
        val minorCharacter = getMinorCharacterById(characterId)!!
        return copy(
            includedCharacters = includedCharacters
                .minus(characterId)
                .plus(characterId to minorCharacter.promote(includedCharacters.keys.toList(), id, name, CharacterArcTemplate.default()))
        )
    }

    @Deprecated("Use of arrow", replaceWith = ReplaceWith("this.withCharacterDemoted(character).right()"))
    fun demoteCharacter(character: MajorCharacter): Either<Exception, Theme> {
		return try {
            withCharacterDemoted(character).right()
        } catch (c: CharacterNotInTheme) {
            c.left()
        }
    }

    fun withCharacterDemoted(character: MajorCharacter): Theme
    {
        mustContainCharacter(character.id)
        return copy(
            includedCharacters = includedCharacters
                .minus(character.id)
                .plus(character.id to character.demote())
        )
    }

    private fun MajorCharacter.demote() =
        MinorCharacter(
            id,
            name,
            archetype,
            variationOnMoral,
            position
        )

    @Deprecated(message = "Outdated api.", replaceWith = ReplaceWith("this.withCharacterAsStoryFunctionForMajorCharacter(characterId, function, majorCharacter.id)"))
    fun applyStoryFunction(
        majorCharacter: MajorCharacter,
        characterId: Character.Id,
        function: StoryFunction
    ): Either<Exception, Theme> {
		try {
            mustContainCharacter(majorCharacter.id)
            mustContainCharacter(characterId)
        } catch (c: CharacterNotInTheme) {
            return c.left()
        }

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

    fun withoutCharacterAsStoryFunctionForPerspectiveCharacter(characterId: Character.Id, perspectiveCharacterId: Character.Id): Theme
    {
        mustContainCharacter(characterId)
        mustContainCharacter(perspectiveCharacterId)
        val majorCharacter = getMajorCharacter(perspectiveCharacterId)
        return copy(
            includedCharacters = includedCharacters
                .minus(majorCharacter.id)
                .plus(majorCharacter.id to majorCharacter.clearStoryFunctions(characterId))
        )
    }

    @Deprecated("Outdated api.", ReplaceWith("this.withoutCharacterAsStoryFunctionForPerspectiveCharacter(characterId, characterInTheme.id)"))
    fun clearStoryFunctions(
        characterInTheme: MajorCharacter,
        characterId: Character.Id
    ): Either<Exception, Theme> {
		try {
            mustContainCharacter(characterInTheme.id)
            mustContainCharacter(characterId)
        } catch (c: CharacterNotInTheme) {
            return c.left()
        }
        return copy(
            includedCharacters = includedCharacters
                .minus(characterInTheme.id)
                .plus(characterInTheme.id to characterInTheme.clearStoryFunctions(characterId))
        ).right()
    }

    fun characterIsMajorCharacter(characterId: Character.Id) = includedCharacters[characterId] is MajorCharacter

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Theme

        if (id != other.id) return false
        if (projectId != other.projectId) return false
        if (name != other.name) return false
        if (symbols != other.symbols) return false
        if (centralConflict != other.centralConflict) return false
        if (centralMoralProblem != other.centralMoralProblem) return false
        if (themeLine != other.themeLine) return false
        if (thematicRevelation != other.thematicRevelation) return false
        if (includedCharacters != other.includedCharacters) return false
        if (similaritiesBetweenCharacters != other.similaritiesBetweenCharacters) return false
        if (valueWebs != other.valueWebs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + projectId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + symbols.hashCode()
        result = 31 * result + centralConflict.hashCode()
        result = 31 * result + centralMoralProblem.hashCode()
        result = 31 * result + themeLine.hashCode()
        result = 31 * result + thematicRevelation.hashCode()
        result = 31 * result + includedCharacters.hashCode()
        result = 31 * result + similaritiesBetweenCharacters.hashCode()
        result = 31 * result + valueWebs.hashCode()
        return result
    }

    override fun toString(): String {
        return "Theme(id=$id, projectId=$projectId, name='$name', symbols=$symbols, centralConflict='$centralConflict', centralMoralProblem='$centralMoralProblem', themeLine='$themeLine', thematicRevelation='$thematicRevelation', includedCharacters=$includedCharacters, similaritiesBetweenCharacters=$similaritiesBetweenCharacters, valueWebs=$valueWebs)"
    }


    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "Theme(${uuid})"
    }

}