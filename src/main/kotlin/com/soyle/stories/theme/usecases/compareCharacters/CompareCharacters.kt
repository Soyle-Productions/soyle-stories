/**
 * Created by Brendan
 * Date: 2/23/2020
 * Time: 11:33 AM
 */
package com.soyle.stories.theme.usecases.compareCharacters

import com.soyle.stories.theme.ThemeException
import java.util.*

interface CompareCharacters {

    suspend operator fun invoke(themeId: UUID, focusCharacterId: UUID?, outputPort: OutputPort)

    class ResponseModel(
        val characterSummaries: CharacterSummaries,
        val majorCharacterIds: List<UUID>,
        val focusedCharacterId: UUID?,
        val centralQuestion: String,
        val comparisonSections: List<String>
    )

    class CharacterSummaries(private val summaries: Map<UUID, CharacterComparisonSummary>) {

        fun getById(id: UUID) = summaries[id]
        fun forceGetById(id: UUID) = summaries[id] ?: throw error("character id <$id> is not in response model")

        val values
            get() = summaries.values

        val ids
            get() = summaries.keys

    }

    class CharacterComparisonSummary(
        val id: UUID,
        val name: String,
        val storyFunctions: List<StoryFunction>,
        val archetypes: String,
        val variationOnMoral: String,
        val attackAgainstHero: String,
        val similaritiesToHero: String,
        val comparisonSections: List<Pair<UUID, String>>
    )

    enum class StoryFunction {
        Hero, Antagonist, FakeAllyAntagonist, FakeAntagonistAlly, Ally, Subplot;
    }

    interface OutputPort {
        fun receiveCompareCharactersFailure(error: ThemeException)
        fun receiveCharacterComparison(response: ResponseModel)
    }

}