package com.soyle.stories.scene.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.characterarc.CharacterArcDoesNotExist
import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.common.template
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcTemplate
import com.soyle.stories.entities.CharacterArcTemplateSection
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.AvailableCharacterArcSectionTypesForCharacterArc
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CreateCharacterArcSectionAndCoverInScene
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CreateCharacterArcSectionAndCoverInSceneUseCase
import com.soyle.stories.theme.makeTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateCharacterArcSectionAndCoverInSceneUnitTest {

    // Preconditions
    private val character = makeCharacter()
    private val theme = makeTheme()

    // output
    private var result: Any? = null

    @Nested
    /**
     * Get the character arc, look at the template, output any template sections that have not yet been included in the
     * arc or allow for multiple sections of that type.
     */
    inner class `List Available Character Arc Sections` {

        @Test
        fun `Character Arc must exist`() {
            val error = assertThrows<CharacterArcDoesNotExist> {
                invoke()
            }
            // then
            with(error) {
                characterId.mustEqual(character.id.uuid) { "CharacterId for CharacterArcDoesNotExist has unexpected value" }
                themeId.mustEqual(theme.id.uuid) { "ThemeId for CharacterArcDoesNotExist has unexpected value" }
            }
        }

        @Test
        fun `Template is empty`() {
            // given
            givenCharacterArcBasedOnTemplateSections(emptyList())
            // when
            invoke()
            // then
            with(result as AvailableCharacterArcSectionTypesForCharacterArc) {
                characterId.mustEqual(character.id.uuid) { "Unexpected characterId for result" }
                themeId.mustEqual(theme.id.uuid) { "Unexpected themeId for result" }
                assertTrue(isEmpty()) { "When the template is empty, the output should be empty" }
            }
        }

        @Test
        fun `Only required sections in template`() {
            // given
            givenCharacterArcBasedOnTemplateSections(List(6) { template("Template ${str()}") })
            // when
            invoke()
            // then
            with(result as AvailableCharacterArcSectionTypesForCharacterArc) {
                characterId.mustEqual(character.id.uuid) { "Unexpected characterId for result" }
                themeId.mustEqual(theme.id.uuid) { "Unexpected themeId for result" }
                assertTrue(isEmpty()) { "When all the template sections are required, the character arc will have used them all, so the output should be empty" }
            }
        }

        @Test
        fun `Character arc already includes all template sections`() {
            // given
            val templateSections = List(6) { template("Template ${str()}", false) }
            givenCharacterArcBasedOnTemplateSections(templateSections) {
                templateSections.fold(this) { nextArc, section ->
                    nextArc.withArcSection(section)
                }
            }
            // when
            invoke()
            // then
            with(result as AvailableCharacterArcSectionTypesForCharacterArc) {
                characterId.mustEqual(character.id.uuid) { "Unexpected characterId for result" }
                themeId.mustEqual(theme.id.uuid) { "Unexpected themeId for result" }
                assertTrue(isEmpty()) { "When all the template sections have been used, so the output should be empty" }
            }
        }

        @Test
        fun `Should produce all unused templates`() {
            // given
            val requiredSections = List(4) { template("Template ${str()}") }
            val unusedUnrequiredSections = List(5) { template("Template ${str()}", false) }
            val usedRequiredSections = List(6) { template("Template ${str()}", false) }
            val templateSections = requiredSections + unusedUnrequiredSections + usedRequiredSections
            givenCharacterArcBasedOnTemplateSections(templateSections) {
                usedRequiredSections.fold(this) { arc, template ->
                    arc.withArcSection(template)
                }
            }
            // when
            invoke()
            // then
            with(result as AvailableCharacterArcSectionTypesForCharacterArc) {
                characterId.mustEqual(character.id.uuid) { "Unexpected characterId for result" }
                themeId.mustEqual(theme.id.uuid) { "Unexpected themeId for result" }
                size.mustEqual(unusedUnrequiredSections.size) { "All unused template sections should be output.  Unexpected number received" }
                unusedUnrequiredSections.forEach(assertOutputTemplateMatchesBase(this))
            }
        }

        @Test
        fun `Sections that allow multiple are always available`() {
            // given
            val requiredSections = List(4) { template("Template ${str()}") }
            val unusedAdditionalSections = List(5) { template("Template ${str()}", required = false) }
            val multiSections = List(6) { template("Template ${str()}", required = false, multiple = true) }
            val templateSections = requiredSections + unusedAdditionalSections + multiSections
            givenCharacterArcBasedOnTemplateSections(templateSections) {
                multiSections.fold(this) { nextArc, section ->
                    nextArc.withArcSection(section)
                }
            }
            // when
            invoke()
            // then
            with(result as AvailableCharacterArcSectionTypesForCharacterArc) {
                characterId.mustEqual(character.id.uuid) { "Unexpected characterId for result" }
                themeId.mustEqual(theme.id.uuid) { "Unexpected themeId for result" }
                size.mustEqual(unusedAdditionalSections.size + multiSections.size) {
                    "All multiple template sections or unused sections should be output.  Unexpected number received"
                }
                (unusedAdditionalSections + multiSections).forEach(assertOutputTemplateMatchesBase(this))
            }
        }

        fun invoke() {
            runBlocking {
                useCase.listAvailableCharacterArcSectionTypesForCharacterArc(
                    theme.id.uuid,
                    character.id.uuid,
                    output
                )
            }
        }

        private fun assertOutputTemplateMatchesBase(output: AvailableCharacterArcSectionTypesForCharacterArc) =
            fun(baseTemplate: CharacterArcTemplateSection) {
                val outputSection = output.find { it.templateSectionId == baseTemplate.id.uuid }
                    ?: throw AssertionError("Missing expected template section from output ${baseTemplate}")
                outputSection.name.mustEqual(baseTemplate.name) { "Output template name does not match." }
                outputSection.multiple.mustEqual(baseTemplate.allowsMultiple) { "Output template should have matching value for `allowMultiple`." }
            }

    }

    private fun givenCharacterArcBasedOnTemplateSections(
        templateSections: List<CharacterArcTemplateSection>,
        and: CharacterArc.() -> CharacterArc = { this }
    ) {
        characterArcRepository.givenCharacterArc(
            CharacterArc.planNewCharacterArc(character.id, theme.id, theme.name, CharacterArcTemplate(templateSections))
                .and()
        )
    }

    private val characterArcRepository = CharacterArcRepositoryDouble()

    private val useCase: CreateCharacterArcSectionAndCoverInScene =
        CreateCharacterArcSectionAndCoverInSceneUseCase(characterArcRepository)

    private val output = object : CreateCharacterArcSectionAndCoverInScene.OutputPort {
        override suspend fun receiveAvailableCharacterArcSectionTypesForCharacterArc(response: AvailableCharacterArcSectionTypesForCharacterArc) {
            result = response
        }
    }

}