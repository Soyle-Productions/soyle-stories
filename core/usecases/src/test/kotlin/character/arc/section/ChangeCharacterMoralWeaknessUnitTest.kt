package com.soyle.stories.usecase.character.arc.section

import arrow.core.Either
import com.soyle.stories.domain.character.*
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.character.CharacterArcDoesNotExist
import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.*
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.characterIsNotMajorCharacterInTheme
import com.soyle.stories.usecase.theme.characterNotInTheme
import com.soyle.stories.usecase.theme.themeDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeCharacterMoralWeaknessUnitTest {

    // input
    private val themeId = Theme.Id()
    private val characterId = Character.Id()
    private val providedWeakness = "Psychological Weakness ${str()}"

    // post-conditions
    private var updatedCharacterArc: CharacterArc? = null

    // output
    private var responseModel: ChangeCharacterMoralWeakness.ResponseModel? = null

    @Test
    fun `should throw error given theme does not exist`() {
        val action = ::changeCharacterMoralWeakness

        val error = assertThrows<ThemeDoesNotExist>(action)
        error.themeId.mustEqual(themeId.uuid)
    }

    @Nested
    inner class `Given Theme Exists`
    {

        private val theme = makeTheme(id = themeId)
        init {
            themeRepository.givenTheme(theme)
        }

        @Test
        fun `should throw error given character is not in theme`() {
            val action = ::changeCharacterMoralWeakness

            val error = assertThrows<CharacterNotInTheme>(action)
            error.themeId.mustEqual(themeId.uuid)
            error.characterId.mustEqual(characterId.uuid)
        }

        @Nested
        inner class `Given Character is in Theme`
        {

            private val character = makeCharacter(id = characterId)
            init {
                themeRepository.givenTheme(theme.withCharacterIncluded(character))
            }

            @Test
            fun `should throw error given character is not a major character`() {
                val action = ::changeCharacterMoralWeakness

                val error = assertThrows<CharacterIsNotMajorCharacterInTheme>(action)
                error.characterId.mustEqual(characterId.uuid)
                error.themeId.mustEqual(themeId.uuid)
            }

            @Nested
            inner class `Given Character is Major Character`
            {

                init {
                    themeRepository.givenTheme(theme
                        .withCharacterIncluded(character)
                        .withCharacterPromoted(character.id)
                    )
                }

                @Test
                fun `should throw error given no accompanying character arc`() {
                    val action = ::changeCharacterMoralWeakness

                    val error = assertThrows<CharacterArcDoesNotExist>(action)
                    error.characterId.mustEqual(characterId.uuid)
                    error.themeId.mustEqual(themeId.uuid)
                }

                @Nested
                inner class `Given Accompanying Character Arc Exists`
                {

                    private val arc = makeCharacterArc(characterId, themeId)
                    init {
                        characterArcRepository.givenCharacterArc(arc)
                    }

                    @Test
                    fun `should add arc section to character arc`() {
                        changeCharacterMoralWeakness()

                        updatedCharacterArc!!.characterId.mustEqual(characterId)
                        updatedCharacterArc!!.themeId.mustEqual(themeId)
                        persistedArcSection()!!.value.mustEqual(providedWeakness)
                    }

                    @Test
                    fun `should output arc section added event`() {
                        changeCharacterMoralWeakness()

                        val section = persistedArcSection()!!
                        responseModel!!.characterArcSectionAddedToArc.mustEqual(
                            ArcSectionAddedToCharacterArc(
                                section.id.uuid,
                                arc.id.uuid,
                                characterId.uuid,
                                themeId.uuid,
                                MoralWeakness.id.uuid,
                                MoralWeakness.name,
                                updatedCharacterArc!!.indexInMoralArgument(section.id),
                                providedWeakness,
                                emptyList()
                            )
                        )
                    }

                    @Test
                    fun `should not output value changed event`() {
                        changeCharacterMoralWeakness()

                        responseModel!!.changedCharacterMoralWeakness.mustEqual(null)
                    }

                    private fun persistedArcSection(): CharacterArcSection? =
                        updatedCharacterArc!!.arcSections.single { it.template isSameEntityAs MoralWeakness }

                    @Nested
                    inner class `Given Character Arc has Moral Weakness Section`
                    {

                        private val arc = this@`Given Accompanying Character Arc Exists`.arc.withArcSection(
                            MoralWeakness
                        )
                        init {
                            characterArcRepository.givenCharacterArc(arc)
                        }
                        val arcSection = arc.arcSections.single { it.template isSameEntityAs MoralWeakness }

                        @Test
                        fun `should update character arc section`() {
                            changeCharacterMoralWeakness()

                            persistedArcSection()!!.id.mustEqual(arcSection.id)
                            persistedArcSection()!!.value.mustEqual(providedWeakness)
                        }

                        @Test
                        fun `should output value changed event`() {
                            changeCharacterMoralWeakness()

                            responseModel!!.changedCharacterMoralWeakness.mustEqual(
                                ChangedCharacterArcSectionValue(
                                    arcSection.id.uuid,
                                    characterId.uuid,
                                    themeId.uuid,
                                    ArcSectionType.MoralWeakness,
                                    providedWeakness
                                )
                            )
                        }

                        @Test
                        fun `should not output arc section added event`() {
                            changeCharacterMoralWeakness()

                            responseModel!!.characterArcSectionAddedToArc.mustEqual(null)
                        }

                    }

                }

            }

        }

    }


    private val characterArcRepository = CharacterArcRepositoryDouble(onUpdateCharacterArc = ::updatedCharacterArc::set)
    private val themeRepository = ThemeRepositoryDouble()

    private fun changeCharacterMoralWeakness() {
        val useCase: ChangeCharacterMoralWeakness =
            ChangeCharacterMoralWeaknessUseCase(themeRepository, characterArcRepository)
        val output = object : ChangeCharacterMoralWeakness.OutputPort {
            override suspend fun characterMoralWeaknessChanged(response: ChangeCharacterMoralWeakness.ResponseModel) {
                responseModel = response
            }
        }
        runBlocking {
            useCase.invoke(ChangeCharacterMoralWeakness.RequestModel(themeId.uuid, characterId.uuid, providedWeakness), output)
        }
    }
}