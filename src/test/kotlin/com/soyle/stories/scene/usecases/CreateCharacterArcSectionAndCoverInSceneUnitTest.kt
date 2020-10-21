package com.soyle.stories.scene.usecases

import arrow.given
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.characterarc.CharacterArcAlreadyContainsMaximumNumberOfTemplateSection
import com.soyle.stories.characterarc.CharacterArcDoesNotExist
import com.soyle.stories.characterarc.CharacterArcTemplateSectionDoesNotExist
import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.common.template
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcTemplate
import com.soyle.stories.entities.CharacterArcTemplateSection
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.characterArcSectionCoveredByScene
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.makeScene
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.AvailableCharacterArcSectionTypesForCharacterArc
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CreateCharacterArcSectionAndCoverInScene
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CreateCharacterArcSectionAndCoverInSceneUseCase
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.GetAvailableCharacterArcSectionTypesForCharacterArc
import com.soyle.stories.theme.makeTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class CreateCharacterArcSectionAndCoverInSceneUnitTest {

    // Preconditions
    private val character = makeCharacter()
    private val theme = makeTheme()
    private val scene = makeScene().withCharacterIncluded(character)

    // post conditions
    private var updatedArc: CharacterArc? = null
    private var updatedScene: Scene? = null

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
            val templates = List(6) { template("Template ${str()}") }
            givenCharacterArcBasedOnTemplateSections(templates)
            // when
            invoke()
            // then
            with(result as AvailableCharacterArcSectionTypesForCharacterArc) {
                characterId.mustEqual(character.id.uuid) { "Unexpected characterId for result" }
                themeId.mustEqual(theme.id.uuid) { "Unexpected themeId for result" }
                size.mustEqual(6) { "Expected all templates to be output." }
                templates.forEach(assertOutputTemplateMatchesBase(this))
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
                templateSections.forEach(assertOutputTemplateMatchesBase(this))
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
                size.mustEqual(templateSections.size) { "All template sections should be output.  Unexpected number received" }
                templateSections.forEach(assertOutputTemplateMatchesBase(this))
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
                size.mustEqual(templateSections.size) {
                    "All multiple template sections or unused sections should be output.  Unexpected number received"
                }
                templateSections.forEach(assertOutputTemplateMatchesBase(this))
            }
        }

        fun invoke() {
            runBlocking {
                useCase.invoke(
                    theme.id.uuid,
                    character.id.uuid,
                    output
                )
            }
        }

        private fun assertOutputTemplateMatchesBase(output: AvailableCharacterArcSectionTypesForCharacterArc) : (CharacterArcTemplateSection) -> Unit {

            val arc = characterArcRepository.getAllCharacterArcs().find { it.characterId.uuid == output.characterId && it.themeId.uuid == output.themeId }!!
            return fun(baseTemplate: CharacterArcTemplateSection) {
                val outputSection = output.find { it.templateSectionId == baseTemplate.id.uuid }
                    ?: throw AssertionError("Missing expected template section from output ${baseTemplate}")
                outputSection.name.mustEqual(baseTemplate.name) { "Output template name does not match." }
                outputSection.multiple.mustEqual(baseTemplate.allowsMultiple) { "Output template should have matching value for `allowMultiple`." }
                val existingSection = arc.arcSections.find { it.template.id == baseTemplate.id }
                if (existingSection != null) {
                    assertEquals(existingSection.id.uuid, outputSection.existingSection!!.first)
                    assertEquals(existingSection.value, outputSection.existingSection!!.second)
                }
                else assertNull(outputSection.existingSection) { "Should not receive backing section if not in arc." }
            }
        }


    }

    @Nested
    inner class `Invokation` {

        private val sectionTemplateId = CharacterArcTemplateSection.Id(UUID.randomUUID())

        @Test
        fun `Character Arc Must exist`() {
            // when
            val error = assertThrows<CharacterArcDoesNotExist> {
                invoke()
            }
            // then
            with(error) {
                characterId.mustEqual(character.id.uuid)
                themeId.mustEqual(theme.id.uuid)
            }
        }

        @Test
        fun `Template Section must be part of character arc template`() {
            // given
            characterArcRepository.givenCharacterArc(
                CharacterArc.planNewCharacterArc(
                    character.id,
                    theme.id,
                    theme.name
                )
            )
            // when
            val error = assertThrows<CharacterArcTemplateSectionDoesNotExist> {
                invoke()
            }
            // then
            with(error) {
                characterArcTemplateSectionId.mustEqual(sectionTemplateId.uuid)
            }
        }

        @Test
        fun `Template section cannot be used twice if it doesn't allow multiple`() {
            val templateSection =
                CharacterArcTemplateSection(sectionTemplateId, "Template ${str()}", isRequired = false, allowsMultiple = false, isMoral = false)
            val arc = givenCharacterArcBasedOnTemplateSections(listOf(templateSection)) {
                withArcSection(templateSection)
            }
            sceneRepository.givenScene(scene)
            // when
            val error = assertThrows<CharacterArcAlreadyContainsMaximumNumberOfTemplateSection> {
                invoke()
            }
            // then
            with (error) {
                characterId.mustEqual(character.id.uuid)
                themeId.mustEqual(theme.id.uuid)
                arcId.mustEqual(arc.id.uuid)
                templateSectionId.mustEqual(sectionTemplateId.uuid)
            }
        }

        @Test
        fun `Scene must exist`() {
            val templateSection = CharacterArcTemplateSection(sectionTemplateId, "Template ${str()}", false, true, isMoral = false)
            givenCharacterArcBasedOnTemplateSections(listOf(templateSection))
            // when
            val error = assertThrows<SceneDoesNotExist> {
                invoke()
            }
            // then
            with (error) {
                sceneId.mustEqual(scene.id.uuid)
            }
        }

        @Test
        fun `New arc section must be created with template`() {
            val templateSection = CharacterArcTemplateSection(sectionTemplateId, "Template ${str()}", false, true, isMoral = false)
            val arc = givenCharacterArcBasedOnTemplateSections(listOf(templateSection))
            sceneRepository.givenScene(scene)
            val inputValue = "New section value ${str()}"
            // when
            invoke(value = inputValue)
            // then
            with (updatedArc!!) {
                assertTrue(isSameEntityAs(arc))
                arcSections.size.mustEqual(arc.arcSections.size + 1)
                val newSection = arcSections.find { it.template == templateSection && it !in arc.arcSections }!!
                newSection.value.mustEqual(inputValue)
            }
            (result as CreateCharacterArcSectionAndCoverInScene.ResponseModel)
                .createdCharacterArcSection.run {
                    val newSection = updatedArc!!.arcSections.find { it.template == templateSection && it !in arc.arcSections }!!
                    characterArcSectionId.mustEqual(newSection.id.uuid)
                    arcId.mustEqual(arc.id.uuid)
                    characterId.mustEqual(arc.characterId.uuid)
                    themeId.mustEqual(arc.themeId.uuid)
                    templateSectionId.mustEqual(templateSection.id.uuid)
                    templateSectionName.mustEqual(templateSection.name)
                    value.mustEqual(inputValue)
                }
        }

        @Test
        fun `Scene should cover new arc section`() {
            val templateSection = CharacterArcTemplateSection(sectionTemplateId, "Template ${str()}", false, true, isMoral = false)
            val arc = givenCharacterArcBasedOnTemplateSections(listOf(templateSection))
            sceneRepository.givenScene(scene)
            val inputValue = "New section value ${str()}"
            // when
            invoke(value = inputValue)
            // then
            val newSection = updatedArc!!.arcSections.find { it.template == templateSection && it !in arc.arcSections }!!
            with (updatedScene!!) {
                assertTrue(isSameEntityAs(scene))
                assertTrue(isCharacterArcSectionCovered(newSection.id)) { "Scene does not contain section." }
            }
            (result as CreateCharacterArcSectionAndCoverInScene.ResponseModel)
                .characterArcSectionCoveredByScene
                .shouldBe(characterArcSectionCoveredByScene(newSection, updatedArc!!, scene.id.uuid))
        }

        fun invoke(value: String = "") {
            runBlocking {
                useCase.invoke(
                    CreateCharacterArcSectionAndCoverInScene.RequestModel(
                        theme.id.uuid,
                        character.id.uuid,
                        scene.id.uuid,
                        sectionTemplateId.uuid,
                        value
                    ),
                    output
                )
            }
        }

    }

    private fun givenCharacterArcBasedOnTemplateSections(
        templateSections: List<CharacterArcTemplateSection>,
        and: CharacterArc.() -> CharacterArc = { this }
    ): CharacterArc {
        val arc = CharacterArc.planNewCharacterArc(character.id, theme.id, theme.name, CharacterArcTemplate(templateSections))
            .and()
        characterArcRepository.givenCharacterArc(arc)
        return arc
    }

    private val characterArcRepository = CharacterArcRepositoryDouble(onUpdateCharacterArc = ::updatedArc::set)
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)

    private val useCase =
        CreateCharacterArcSectionAndCoverInSceneUseCase(characterArcRepository, sceneRepository)

    private val output = object : CreateCharacterArcSectionAndCoverInScene.OutputPort, GetAvailableCharacterArcSectionTypesForCharacterArc.OutputPort {
        override suspend fun receiveAvailableCharacterArcSectionTypesForCharacterArc(response: AvailableCharacterArcSectionTypesForCharacterArc) {
            result = response
        }

        override suspend fun characterArcCreatedAndCoveredInScene(response: CreateCharacterArcSectionAndCoverInScene.ResponseModel) {
            result = response
        }
    }

}