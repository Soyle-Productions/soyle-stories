package com.soyle.stories.usecase.character

import com.soyle.stories.domain.character.*
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.character.addCharacterArcSectionToMoralArgument.AddCharacterArcSectionToMoralArgument
import com.soyle.stories.usecase.character.addCharacterArcSectionToMoralArgument.AddCharacterArcSectionToMoralArgumentUseCase
import com.soyle.stories.usecase.character.addCharacterArcSectionToMoralArgument.ListAvailableArcSectionTypesToAddToMoralArgument
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AddCharacterArcSectionToMoralArgumentUnitTest {

    private val theme = makeTheme()
    private val character = makeCharacter()
    private fun makeCharacterArc(template: CharacterArcTemplate) =
        CharacterArc.planNewCharacterArc(character.id, theme.id, theme.name, template)

    private var updatedCharacterArc: CharacterArc? = null
    private var result: Any? = null

    private val characterArcRepository = CharacterArcRepositoryDouble(onUpdateCharacterArc = ::updatedCharacterArc::set)
    private val addCharacterArcSectionToMoralArgument = AddCharacterArcSectionToMoralArgumentUseCase(characterArcRepository)

    @Nested
    inner class `List Available Character Arc Section Types to Add to Moral Argument` {

        private val useCase: ListAvailableArcSectionTypesToAddToMoralArgument = addCharacterArcSectionToMoralArgument
        private val output = object : ListAvailableArcSectionTypesToAddToMoralArgument.OutputPort {
            override suspend fun receiveAvailableArcSectionTypesToAddToMoralArgument(
                response: ListAvailableArcSectionTypesToAddToMoralArgument.ResponseModel
            ) {
                result = response
            }
        }

        @Nested
        inner class `Character Arc Doesn't Exist` {

            @Test
            fun `should throw error`() {
                val error = assertThrows<CharacterArcDoesNotExist> {
                    listAvailableCharacterArcSectionTypesToAddToMoralArgument()
                }

                error.themeId.mustEqual(theme.id.uuid) { "Theme id of CharacterArcDoesNotExist error does not match" }
                error.characterId.mustEqual(character.id.uuid) { "Character id of CharacterArcDoesNotExist error does not match" }
            }

        }

        @Nested
        inner class `No Moral Section Types in Character Arc Template` {

            private val arcTemplate = CharacterArcTemplate(List(10) {
                template(
                    "Section ${str()}",
                    required = false,
                    multiple = false,
                    moral = false
                )
            })

            init {
                characterArcRepository.givenCharacterArc(makeCharacterArc(arcTemplate))
            }

            @Test
            fun `output should be empty`() {
                listAvailableCharacterArcSectionTypesToAddToMoralArgument()

                val result = result as ListAvailableArcSectionTypesToAddToMoralArgument.ResponseModel
                result.isEmpty().mustEqual(true) { "Response Model should be empty" }
            }

            @Test
            fun `should output request information`() {
                listAvailableCharacterArcSectionTypesToAddToMoralArgument()

                val result = result as ListAvailableArcSectionTypesToAddToMoralArgument.ResponseModel
                result.themeId.mustEqual(theme.id.uuid) { "Theme id of Response Model does not match" }
                result.characterId.mustEqual(character.id.uuid) { "Character id of Response Model does not match" }
            }

        }

        @Nested
        inner class `No Moral Section Types used in Character Arc Yet` {

            private val arcTemplate = CharacterArcTemplate(List(10) {
                template(
                    "Section ${str()}",
                    required = false,
                    multiple = false,
                    moral = true
                )
            })

            init {
                characterArcRepository.givenCharacterArc(makeCharacterArc(arcTemplate))
            }

            @Test
            fun `should output all moral arc sections`() {
                listAvailableCharacterArcSectionTypesToAddToMoralArgument()

                val result = result as ListAvailableArcSectionTypesToAddToMoralArgument.ResponseModel
                result.size.mustEqual(arcTemplate.sections.size) { "Incorrect number of sections output" }
                result.map { it.sectionTemplateId }.toSet().mustEqual(arcTemplate.sections.map { it.id.uuid }.toSet()) {
                    "Incorrect arc section template ids output"
                }
            }

            @Test
            fun `should output name of each arc section template`() {
                listAvailableCharacterArcSectionTypesToAddToMoralArgument()

                val result = result as ListAvailableArcSectionTypesToAddToMoralArgument.ResponseModel
                result.forEach { availableArcType ->
                    val template = arcTemplate.sections.find { it.id.uuid == availableArcType.sectionTemplateId }!!
                    availableArcType.sectionTemplateName.mustEqual(template.name)
                }
            }

            @Test
            fun `should show sections as available to be added`() {
                listAvailableCharacterArcSectionTypesToAddToMoralArgument()

                val result = result as ListAvailableArcSectionTypesToAddToMoralArgument.ResponseModel
                result.forEach { availableArcType ->
                    availableArcType.canBeCreated.mustEqual(true) { "Unused sections should be able to be created" }
                }
            }

        }

        @Nested
        inner class `Some Templates are Required and thus Used Already` {

            private val arcTemplate = CharacterArcTemplate(List(10) {
                template(
                    "Section ${str()}",
                    required = it % 2 == 0,
                    multiple = it % 3 == 0,
                    moral = true
                )
            })
            private val characterArc = makeCharacterArc(arcTemplate)

            init {
                characterArcRepository.givenCharacterArc(characterArc)
            }

            @Test
            fun `should not show sections as available to be added`() {
                listAvailableCharacterArcSectionTypesToAddToMoralArgument()

                val result = result as ListAvailableArcSectionTypesToAddToMoralArgument.ResponseModel
                result.forEachIndexed { index, availableArcType ->
                    availableArcType.canBeCreated.mustEqual(index % 2 != 0 || index % 3 == 0) {
                        "Should not be able to create used, singular section types"
                    }
                }
            }

            @Test
            fun `should output section id for already created sections`() {
                listAvailableCharacterArcSectionTypesToAddToMoralArgument()

                val result = result as ListAvailableArcSectionTypesToAddToMoralArgument.ResponseModel
                result.forEachIndexed { index, availableArcType ->
                    if (index % 2 == 0 && index % 3 != 0) {
                        val existingSection = characterArc.arcSections.find {
                            it.template.id.uuid == availableArcType.sectionTemplateId
                        }!!
                        availableArcType.existingSectionId.mustEqual(existingSection.id.uuid)
                    }
                }
            }

            @Test
            fun `should output index of section in moral argument for already created sections`() {
                listAvailableCharacterArcSectionTypesToAddToMoralArgument()

                val result = result as ListAvailableArcSectionTypesToAddToMoralArgument.ResponseModel
                result.forEachIndexed { index, availableArcType ->
                    if (index % 2 == 0 && index % 3 != 0) {
                        val indexInMoralArgument = characterArc.moralArgument().arcSections.indexOfFirst {
                            it.template.id.uuid == availableArcType.sectionTemplateId
                        }
                        availableArcType.indexInMoralArgument.mustEqual(indexInMoralArgument)
                    }
                }
            }

        }

        private fun listAvailableCharacterArcSectionTypesToAddToMoralArgument() {
            runBlocking {
                useCase.invoke(theme.id.uuid, character.id.uuid, output)
            }
        }

    }

    @Nested
    inner class `Add Character Arc Section to Moral Argument` {

        private val templateSection = template("Template ${str()}", required = false, multiple = false, moral = true)
        private lateinit var characterArc: CharacterArc

        private val useCase: AddCharacterArcSectionToMoralArgument = addCharacterArcSectionToMoralArgument
        private val output = object : AddCharacterArcSectionToMoralArgument.OutputPort {
            override suspend fun characterArcSectionAddedToMoralArgument(response: AddCharacterArcSectionToMoralArgument.ResponseModel) {
                result = response
            }
        }

        @BeforeEach
        fun givenCharacterArc() {
            characterArcRepository.givenCharacterArc(characterArc)
        }

        @Nested
        inner class `Character Arc Doesn't Exist` {

            init {
                characterArc = CharacterArc.planNewCharacterArc(Character.Id(), Theme.Id(), "")
            }

            @Test
            fun `should throw error`() {
                val error = assertThrows<CharacterArcDoesNotExist> {
                    addCharacterArcSectionToMoralArgument()
                }

                error.themeId.mustEqual(theme.id.uuid) { "Theme id of CharacterArcDoesNotExist error does not match" }
                error.characterId.mustEqual(character.id.uuid) { "Character id of CharacterArcDoesNotExist error does not match" }
            }

        }

        @Nested
        inner class `Template Section Doesn't Exist in Arc Template` {

            init {
                characterArc = makeCharacterArc(CharacterArcTemplate(listOf()))
            }

            @Test
            fun `should throw error`() {
                val error = assertThrows<TemplateSectionIsNotPartOfArcTemplate> {
                    addCharacterArcSectionToMoralArgument()
                }

                error.arcId.mustEqual(characterArc.id.uuid)
                error.characterId.mustEqual(character.id.uuid)
                error.themeId.mustEqual(theme.id.uuid)
                error.templateSectionId.mustEqual(templateSection.id.uuid)
            }

        }

        @Nested
        inner class `Requested Template Section is Not Moral` {

            init {
                characterArc = makeCharacterArc(CharacterArcTemplate(listOf(
                    CharacterArcTemplateSection(
                        templateSection.id,
                        templateSection.name,
                        templateSection.isRequired,
                        templateSection.allowsMultiple,
                        isMoral = false
                    )
                )))
            }

            @Test
            fun `should throw error`() {
                val error = assertThrows<ArcTemplateSectionIsNotMoral> {
                    addCharacterArcSectionToMoralArgument()
                }

                error.arcId.mustEqual(characterArc.id.uuid)
                error.characterId.mustEqual(character.id.uuid)
                error.themeId.mustEqual(theme.id.uuid)
                error.templateSectionId.mustEqual(templateSection.id.uuid)
            }

        }

        @Nested
        inner class `Requested Template Section is Already Used and Doesn't Allow Multiple` {

            init {
                characterArc = makeCharacterArc(CharacterArcTemplate(listOf(
                    CharacterArcTemplateSection(
                        templateSection.id,
                        templateSection.name,
                        isRequired = true,
                        allowsMultiple = false,
                        templateSection.isMoral
                    )
                )))
            }

            @Test
            fun `should throw error`() {
                val error = assertThrows<CharacterArcAlreadyContainsMaximumNumberOfTemplateSection> {
                    addCharacterArcSectionToMoralArgument()
                }

                error.arcId.mustEqual(characterArc.id.uuid)
                error.characterId.mustEqual(character.id.uuid)
                error.themeId.mustEqual(theme.id.uuid)
                error.templateSectionId.mustEqual(templateSection.id.uuid)
            }

        }

        @Nested
        inner class `No Index Specified` {

            init {
                characterArc = makeCharacterArc(CharacterArcTemplate(
                    listOf(templateSection) +
                            List(10) {
                                template("Template ${str()}", required = true, multiple = false, moral = true)
                            }
                ))
            }

            @Test
            fun `should be added to end of moral argument`() {
                addCharacterArcSectionToMoralArgument()

                val moralArgument = updatedCharacterArc!!.moralArgument()
                val lastMoralSection = moralArgument.arcSections.last()
                val sectionWithTemplate = moralArgument.arcSections.find { it.template === templateSection }!!
                sectionWithTemplate.mustEqual(lastMoralSection) {
                    "Section was not added ot end.  Found at ${moralArgument.arcSections.indexOfFirst { it === sectionWithTemplate }}"
                }
            }

            @Test
            fun `should report arc section created`() {
                addCharacterArcSectionToMoralArgument()

                val result = result as AddCharacterArcSectionToMoralArgument.ResponseModel
                val report = result.characterArcSectionAddedToMoralArgument
                report.arcId.mustEqual(characterArc.id.uuid)
                report.characterId.mustEqual(character.id.uuid)
                report.themeId.mustEqual(theme.id.uuid)
                report.templateSectionId.mustEqual(templateSection.id.uuid)
                report.templateSectionName.mustEqual(templateSection.name)
                val createdSection = updatedCharacterArc!!.moralArgument().arcSections.find { it.template === templateSection }!!
                report.characterArcSectionId.mustEqual(createdSection.id.uuid)
                report.indexInMoralArgument.mustEqual(10)
                report.value.mustEqual("")
            }

        }

        @Nested
        inner class `Index Specified` {

            init {
                characterArc = makeCharacterArc(CharacterArcTemplate(
                    listOf(templateSection) +
                            List(10) {
                                template("Template ${str()}", required = true, multiple = false, moral = true)
                            }
                ))
            }

            @Test
            fun `Add to middle`() {
                addCharacterArcSectionToMoralArgument(3)

                val updatedCharacterArc = updatedCharacterArc!!
                val newSection = updatedCharacterArc.moralArgument().arcSections.single { it.template === templateSection }
                updatedCharacterArc.indexInMoralArgument(newSection.id).mustEqual(3)

                val originalMoralArgument = characterArc.moralArgument()
                originalMoralArgument.arcSections.takeLast(7).forEachIndexed { initialIndexOffsetBy3, section ->
                    updatedCharacterArc.indexInMoralArgument(section.id).mustEqual(initialIndexOffsetBy3 + 4)
                }

                val result = result as AddCharacterArcSectionToMoralArgument.ResponseModel
                val report = result.characterArcSectionAddedToMoralArgument
                report.indexInMoralArgument.mustEqual(3)

                report.displacedArcSections.size.mustEqual(7)
                report.displacedArcSections.toSet().mustEqual(originalMoralArgument.arcSections.takeLast(7).map { it.id.uuid }.toSet())
            }

            @Test
            fun `Add to End`() {
                addCharacterArcSectionToMoralArgument(10)

                val updatedCharacterArc = updatedCharacterArc!!
                val newSection = updatedCharacterArc.moralArgument().arcSections.single { it.template === templateSection }
                updatedCharacterArc.indexInMoralArgument(newSection.id).mustEqual(10)

                val originalMoralArgument = characterArc.moralArgument()
                originalMoralArgument.arcSections.forEachIndexed { initialIndex, section ->
                    updatedCharacterArc.indexInMoralArgument(section.id).mustEqual(initialIndex)
                }

                val result = result as AddCharacterArcSectionToMoralArgument.ResponseModel
                val report = result.characterArcSectionAddedToMoralArgument
                report.indexInMoralArgument.mustEqual(10)

                report.displacedArcSections.size.mustEqual(0)

            }

            @Test
            fun `Negative Index`() {
                assertThrows<IndexOutOfBoundsException> {
                    addCharacterArcSectionToMoralArgument(-4)
                }

                assertNull(updatedCharacterArc)
            }

            @Test
            fun `Index too large`() {
                assertThrows<IndexOutOfBoundsException> {
                    addCharacterArcSectionToMoralArgument(11)
                }

                assertNull(updatedCharacterArc)
            }

        }

        fun addCharacterArcSectionToMoralArgument(index: Int? = null) {
            val request = AddCharacterArcSectionToMoralArgument.RequestModel(
                theme.id.uuid,
                character.id.uuid,
                templateSection.id.uuid,
                index
            )
            runBlocking {
                useCase.invoke(request, output)
            }
        }

    }

}