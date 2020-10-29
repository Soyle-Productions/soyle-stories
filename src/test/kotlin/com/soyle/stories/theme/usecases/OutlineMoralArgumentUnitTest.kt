package com.soyle.stories.theme.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.common.template
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcTemplate
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.outlineMoralArgument.GetMoralProblemAndThemeLineInTheme
import com.soyle.stories.theme.usecases.outlineMoralArgument.OutlineMoralArgument
import com.soyle.stories.theme.usecases.outlineMoralArgument.OutlineMoralArgumentForCharacterInTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OutlineMoralArgumentUnitTest {

    // preconditions
    private val theme = makeTheme()

    // output
    private var result: Any? = null


    private val themeRepository = ThemeRepositoryDouble()
    private val characterArcRepository = CharacterArcRepositoryDouble()
    private val outlineMoralArgument: OutlineMoralArgument =
        OutlineMoralArgument(themeRepository, characterArcRepository)

    @Nested
    inner class `Get Moral Problem and Theme Line in Theme` {

        private val useCase: GetMoralProblemAndThemeLineInTheme = outlineMoralArgument
        private val output = object : GetMoralProblemAndThemeLineInTheme.OutputPort {
            override suspend fun receiveMoralProblemAndThemeLineInTheme(response: GetMoralProblemAndThemeLineInTheme.ResponseModel) {
                result = response
            }
        }

        @Nested
        inner class `Theme does not exist` {

            @Test
            fun `should throw error`() {
                val error = assertThrows<ThemeDoesNotExist> {
                    getMoralProblemAndThemeLineInTheme()
                }
                error shouldBe themeDoesNotExist(theme.id.uuid)
            }

        }

        @Nested
        inner class `Theme exists` {

            private val moralProblem = "Moral Problem ${str()}"
            private val themeLine = "Theme Line ${str()}"

            init {
                themeRepository.givenTheme(theme.withMoralProblem(moralProblem).withThemeLine(themeLine))
            }

            @AfterEach
            fun `should output theme id`() {
                val result = result as GetMoralProblemAndThemeLineInTheme.ResponseModel
                result.themeId.mustEqual(theme.id.uuid)
            }

            @Test
            fun `should output moral problem`() {
                getMoralProblemAndThemeLineInTheme()
                val result = result as GetMoralProblemAndThemeLineInTheme.ResponseModel
                result.moralProblem.mustEqual(moralProblem)
            }

            @Test
            fun `should output theme line`() {
                getMoralProblemAndThemeLineInTheme()
                val result = result as GetMoralProblemAndThemeLineInTheme.ResponseModel
                result.themeLine.mustEqual(themeLine)
            }

        }

        private fun getMoralProblemAndThemeLineInTheme() {
            runBlocking {
                useCase.invoke(theme.id.uuid, output)
            }
        }

    }

    @Nested
    inner class `Outline Moral Argument for Character in Theme` {

        // preconditions
        private val character = makeCharacter()

        private val useCase: OutlineMoralArgumentForCharacterInTheme = outlineMoralArgument
        private val output = object : OutlineMoralArgumentForCharacterInTheme.OutputPort {
            override suspend fun receiveMoralArgumentOutlineForCharacterInTheme(response: OutlineMoralArgumentForCharacterInTheme.ResponseModel) {
                result = response
            }
        }

        @Nested
        inner class `Theme does not exist` {

            @Test
            fun `should throw error`() {
                val error = assertThrows<ThemeDoesNotExist> {
                    outlineMoralArgumentForCharacterInTheme()
                }
                error shouldBe themeDoesNotExist(theme.id.uuid)
            }

        }

        @Nested
        inner class `Character is not in Theme` {

            init {
                themeRepository.givenTheme(theme)
            }

            @Test
            fun `should throw error`() {
                val error = assertThrows<CharacterNotInTheme> {
                    outlineMoralArgumentForCharacterInTheme()
                }
                error shouldBe characterNotInTheme(theme.id.uuid, character.id.uuid)
            }

        }

        @Nested
        inner class `Character is not a Major Character in Theme` {

            init {
                themeRepository.givenTheme(theme.withCharacterIncluded(character.id, character.name, character.media))
            }

            @Test
            fun `should throw error`() {
                val error = assertThrows<CharacterIsNotMajorCharacterInTheme> {
                    outlineMoralArgumentForCharacterInTheme()
                }
                error shouldBe characterIsNotMajorCharacterInTheme(theme.id.uuid, character.id.uuid)
            }

        }

        @Nested
        inner class `Character is Major Character in Theme` {

            private val moralArcSections = List(6) {
                template("Section Template ${str()}", it % 2 == 0, it % 2 == 1, moral = true)
            }
            private val arcTemplate =
                CharacterArcTemplate(moralArcSections + List(4) { template("Section Template ${str()}", true) })
            private val characterArc = CharacterArc.planNewCharacterArc(character.id, theme.id, theme.name, arcTemplate)
                .withArcSection(moralArcSections.find { !it.isRequired }!!)

            init {
                theme
                    .withCharacterIncluded(character.id, character.name, character.media)
                    .withCharacterPromoted(character.id)
                    .let(themeRepository::givenTheme)
                characterArcRepository.givenCharacterArc(characterArc)
            }

            @Test
            fun `should output character id and name`() {
                outlineMoralArgumentForCharacterInTheme()

                val result = result as OutlineMoralArgumentForCharacterInTheme.ResponseModel
                result.characterId.mustEqual(character.id.uuid)
                result.characterName.mustEqual(character.name)
            }

            @Test
            fun `should output all and only moral character arc sections for character in theme`() {
                outlineMoralArgumentForCharacterInTheme()

                val result = result as OutlineMoralArgumentForCharacterInTheme.ResponseModel
                result.characterArcSections.size.mustEqual(4) { "Incorrect number of output character arc sections." }
                val outputIds = result.characterArcSections.map { it.arcSectionId }
                val expectedIds = characterArc.arcSections.filter { it.template in moralArcSections }.map { it.id.uuid }
                outputIds.toSet().mustEqual(expectedIds.toSet()) { "Incorrect arc section ids were output." }
            }

            @Test
            fun `should output character arc sections in correct order`() {
                outlineMoralArgumentForCharacterInTheme()

                val result = result as OutlineMoralArgumentForCharacterInTheme.ResponseModel
                val expectedOrder = characterArc.moralArgument().arcSections.withIndex().associate { it.value.id.uuid to it.index }
                result.characterArcSections.withIndex().forEach {
                    it.index.mustEqual(expectedOrder[it.value.arcSectionId]) { "Output sections in wrong order." }
                }
            }

            @Test
            fun `should output values of character arc sections`() {
                val arcWithSectionValues = characterArc.withArcSectionsMapped {
                    it.withValue("Section Value ${str()}")
                }
                characterArcRepository.givenCharacterArc(arcWithSectionValues)

                outlineMoralArgumentForCharacterInTheme()

                val result = result as OutlineMoralArgumentForCharacterInTheme.ResponseModel
                result.characterArcSections.forEach { outputSection ->
                    val baseSection =
                        arcWithSectionValues.arcSections.find { it.id.uuid == outputSection.arcSectionId }!!
                    outputSection.arcSectionValue.mustEqual(baseSection.value) { "Incorrect value was output" }
                }
            }

            @Test
            fun `should output name of template for each character arc section`() {
                outlineMoralArgumentForCharacterInTheme()

                val result = result as OutlineMoralArgumentForCharacterInTheme.ResponseModel
                result.characterArcSections.forEach { outputSection ->
                    val baseSection =
                        characterArc.arcSections.find { it.id.uuid == outputSection.arcSectionId }!!
                    outputSection.sectionTemplateName.mustEqual(baseSection.template.name) { "Incorrect template name was output" }
                }
            }

            @Test
            fun `should output if the template is required for each character arc section`() {
                outlineMoralArgumentForCharacterInTheme()

                val result = result as OutlineMoralArgumentForCharacterInTheme.ResponseModel
                result.characterArcSections.forEach { outputSection ->
                    val baseSection =
                        characterArc.arcSections.find { it.id.uuid == outputSection.arcSectionId }!!
                    outputSection.sectionTemplateIsRequired.mustEqual(baseSection.template.isRequired) { "Template requirement does not match" }
                }
            }

        }

        private fun outlineMoralArgumentForCharacterInTheme() {
            runBlocking {
                useCase.invoke(theme.id.uuid, character.id.uuid, output)
            }
        }

    }

}