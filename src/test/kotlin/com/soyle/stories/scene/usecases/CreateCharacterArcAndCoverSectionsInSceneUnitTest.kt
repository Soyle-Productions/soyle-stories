package com.soyle.stories.scene.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.str
import com.soyle.stories.common.template
import com.soyle.stories.doubles.CharacterArcTemplateRepositoryDouble
import com.soyle.stories.entities.CharacterArcTemplate
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CreateCharacterArcSectionAndCoverInScene
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CreateCharacterArcAndCoverSectionsInSceneUseCase
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.RequiredCharacterArcSectionTypes
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CreateCharacterArcAndCoverSectionsInSceneUnitTest {

    // post-conditions
    private var result: Any? = null

    @Nested
    /**
     * Read the character arc template and get all the required sections
     */
    inner class `List Character Arc Section Types that Will be Created`
    {

        @Test
        fun `Default Character Arc Template is empty`() {
            // when
            invoke()
            // then
            with(result as RequiredCharacterArcSectionTypes) {
                assert(isEmpty()) { "result should be empty when character arc template is empty" }
            }
        }

        @Test
        fun `Default Character Arc Template has no Required Sections`() {
            // given
            characterArcTemplateRepository.givenDefaultTemplate(
                CharacterArcTemplate(List(6) {
                    template("Template ${str()}", false)
                })
            )
            // when
            invoke()
            // then
            with(result as RequiredCharacterArcSectionTypes) {
                assert(isEmpty()) { "result should be empty when character arc template has no required sections" }
            }
        }

        @Test
        fun `Default Character Arc Template has Required Sections`() {
            // given
            val requiredSections = List(6) {
                template("Template ${str()}", true)
            }
            characterArcTemplateRepository.givenDefaultTemplate(
                CharacterArcTemplate(List(6) {
                    template("Template ${str()}", false)
                } + requiredSections)
            )
            // when
            invoke()
            // then
            with(result as RequiredCharacterArcSectionTypes) {
                size.mustEqual(requiredSections.size) { "Result size should be the same number of required sections" }

                requiredSections.forEach { baseSection ->
                    val outputSection = find { it.templateSectionId == baseSection.id.uuid }
                        ?: error("Missing $baseSection from output")

                    outputSection.name.mustEqual(baseSection.name) {
                        "Output template section name does not match expected name" }
                }
            }
        }

        fun invoke() {
            val useCase = CreateCharacterArcAndCoverSectionsInSceneUseCase(characterArcTemplateRepository)
            val output = object : CreateCharacterArcSectionAndCoverInScene.OutputPort {
                override suspend fun receiveRequiredCharacterArcSectionTypes(response: RequiredCharacterArcSectionTypes) {
                    result = response
                }
            }
            runBlocking {
                useCase.listAvailableCharacterArcSectionTypesForCharacterArc(output)
            }
        }

    }

    @Nested
    inner class `Create Character Arc Section and Cover in Scene` {

    }

    private val characterArcTemplateRepository = CharacterArcTemplateRepositoryDouble()

}