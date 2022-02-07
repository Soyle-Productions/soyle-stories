package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.*
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.*
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.CharacterArcSectionDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.characterArcSectionCoveredByScene
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.AvailableCharacterArcSectionsForCharacterInScene
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.CoverCharacterArcSectionsInScene
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.CoverCharacterArcSectionsInSceneUseCase
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.GetAvailableCharacterArcsForCharacterInScene
import com.soyle.stories.usecase.scene.sceneDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class `Cover Character Arc Sections in Scene Unit Test` {

    // Preconditions
    val scene = makeScene()
    val character = makeCharacter()

    // Post-conditions
    private var savedScene: Scene? = null

    // output
    private var result: Any? = null

    // repositories
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::savedScene::set)
    private val characterArcRepository = CharacterArcRepositoryDouble()

    // use case
    private val useCase = CoverCharacterArcSectionsInSceneUseCase(sceneRepository, characterArcRepository)
    private val output = object : CoverCharacterArcSectionsInScene.OutputPort, GetAvailableCharacterArcsForCharacterInScene.OutputPort {
        override suspend fun availableCharacterArcSectionsForCharacterInSceneListed(response: AvailableCharacterArcSectionsForCharacterInScene) {
            result = response
        }

        override suspend fun characterArcSectionsCoveredInScene(response: CoverCharacterArcSectionsInScene.ResponseModel) {
            result = response
        }
    }

    @Nested
    inner class `List available character arcs for character in scene` {

        @Test
        /*
        Summary: when the scene does not exist, throw a scene does not exist error with the scene id

        When the user indicates that they want to cover character arc sections for a character in a scene
        then a SceneDoesNotExist error should be thrown with the requested scene id
         */
        fun `Scene does not exist`() {
            // when
            val error = assertThrows<SceneDoesNotExist> {
                listAvailableCharacterArcsForCharacterInScene()
            }
            // then
            error shouldBe sceneDoesNotExist(scene.id.uuid)
        }

        @Test
        /*
        Summary: when the character is not in the scene, throw a character not in scene error with the scene id and
                 character id

        Given the scene ${scene} exists
        When the user indicates that they want to cover character arc sections for a character in this scene
        then a CharacterNotInScene error should be thrown with the requested character id and scene id
         */
        fun `Character not in scene`() {
            // given
            sceneRepository.givenScene(scene)
            // when
            val error = assertThrows<SceneDoesNotIncludeCharacter> {
                listAvailableCharacterArcsForCharacterInScene()
            }
            // then
            error shouldBe characterNotInScene(scene.id, character.id)
        }

        @Test
        /**
        * - Given the [scene] exists
        * - and the [scene] includes the [character]
        * - When the user indicates that they want to cover character arc sections for this [character] in this [scene]
        * - Then the system should respond with the available character arc sections for this [character] in this [scene]
        * - and there should be no listed character arc sections
         */
        fun `No character arcs exist for character`() {
            // given
            sceneRepository.givenScene(scene.givenCharacter(character))
            // when
            listAvailableCharacterArcsForCharacterInScene()
            // then
            result shouldBe availableCharacterArcSectionsForCharacterInScene(
                scene.id.uuid,
                character.id.uuid
            ) {
                assertTrue(it.isEmpty())
            }
        }

        @Test
        /*
        Given the scene ${scene} exists
        and the scene ${scene} includes the character ${character}
        and a character arc with sections exists for this character
        When the user indicates that they want to cover character arc sections for this character in this scene
        Then the system should respond with the available character arc sections for this character in this scene
        and
         */
        fun `character arcs have sections`() {
            // given
            sceneRepository.givenScene(scene.givenCharacter(character))
            val baseArc = makeCharacterArc(character.id).also(characterArcRepository::givenCharacterArc)
            val baseSections = baseArc.arcSections
            // when
            listAvailableCharacterArcsForCharacterInScene()
            // then
            result shouldBe availableCharacterArcSectionsForCharacterInScene(scene.id.uuid, character.id.uuid) {
                val availableArc = it.single()
                assertEquals(baseArc.name, availableArc.characterArcName)
                assertEquals(baseArc.themeId.uuid, availableArc.themeId)
                assertEquals(baseSections.map { it.id.uuid }.toSet(), availableArc.map { it.arcSectionId }.toSet())
                availableArc.forEach { section ->
                    val baseSection = baseSections.single { it.id.uuid == section.arcSectionId }
                    assertEquals(baseSection.template.name, section.templateName)
                    assertEquals(baseSection.value, section.sectionValue)
                    assertFalse(section.usedInScene)
                    assertEquals(baseSection.template.allowsMultiple, section.isMultiTemplate)
                }
            }
        }

        @Test
        fun `some arc sections covered by scene already`() {
            val baseArc = makeCharacterArc(character.id).also(characterArcRepository::givenCharacterArc)
            val baseSections = baseArc.arcSections
            scene.givenCharacter(character)
                .givenCoveredSection(baseSections.first())
                .also(sceneRepository::givenScene)

            listAvailableCharacterArcsForCharacterInScene()

            result shouldBe availableCharacterArcSectionsForCharacterInScene(scene.id.uuid, character.id.uuid) {
                val availableArc = it.single()
                assertEquals(baseArc.name, availableArc.characterArcName)
                assertEquals(baseSections.map { it.id.uuid }.toSet(), availableArc.map { it.arcSectionId }.toSet())
                availableArc.forEach { section ->
                    val baseSection = baseSections.single { it.id.uuid == section.arcSectionId }
                    assertEquals(baseSection.template.name, section.templateName)
                    assertEquals(baseSection.value, section.sectionValue)
                    assertEquals(baseSection == baseSections.first(), section.usedInScene)
                    assertEquals(baseSection.template.allowsMultiple, section.isMultiTemplate)
                }
            }
        }

        @Test
        fun `Arc Sections must output if template allows multiple`() {
            val template = CharacterArcTemplate(List(5) {
                template("Template ${str()}", true, it % 2 == 0)
            })
            val baseArc = makeCharacterArc(character.id, template = template)
                .also(characterArcRepository::givenCharacterArc)
            val baseSections = baseArc.arcSections
            scene.givenCharacter(character)
                .givenCoveredSection(baseSections.first())
                .also(sceneRepository::givenScene)

            listAvailableCharacterArcsForCharacterInScene()

            result shouldBe availableCharacterArcSectionsForCharacterInScene(scene.id.uuid, character.id.uuid) {
                val availableArc = it.single()
                assertEquals(baseArc.name, availableArc.characterArcName)
                assertEquals(baseSections.map { it.id.uuid }.toSet(), availableArc.map { it.arcSectionId }.toSet())
                availableArc.forEach { section ->
                    val baseSection = baseSections.single { it.id.uuid == section.arcSectionId }
                    assertEquals(baseSection.template.allowsMultiple, section.isMultiTemplate)
                }
            }
        }

        private fun listAvailableCharacterArcsForCharacterInScene() {
            runBlocking {
                useCase.invoke(scene.id.uuid, character.id.uuid, output)
            }
        }

    }

    @Nested
    inner class `Cover sections in Scene` {

        @Test
        fun `Scene does not exist`() {
            assertThrows<SceneDoesNotExist> {
                coverSectionsInScene()
            } shouldBe sceneDoesNotExist(scene.id.uuid)
        }

        @Test
        fun `Character not in scene`() {
            sceneRepository.givenScene(scene)

            assertThrows<SceneDoesNotIncludeCharacter> {
                coverSectionsInScene()
            } shouldBe characterNotInScene(scene.id, character.id)
        }

        @Test
        fun `Character arc section doesn't exist`() {
            sceneRepository.givenScene(scene.givenCharacter(character))
            val characterArcSectionIds = List(5) { UUID.randomUUID() }

            assertThrows<CharacterArcSectionDoesNotExist> {
                coverSectionsInScene(*characterArcSectionIds.toTypedArray())
            } shouldBe {
                assertEquals(characterArcSectionIds.first(), it.characterArcSectionId)
            }
        }

        @Test
        fun `Some Character arc sections exist`() {
            sceneRepository.givenScene(scene.givenCharacter(character))
            val characterArc = makeCharacterArc(character.id).also(characterArcRepository::givenCharacterArc)
            val characterArcSectionIds = characterArc.arcSections.map { it.id.uuid } +
                    List(2) { UUID.randomUUID() }

            assertThrows<CharacterArcSectionDoesNotExist> {
                coverSectionsInScene(*characterArcSectionIds.toTypedArray())
            } shouldBe { error ->
                assertTrue(characterArcSectionIds.any { it == error.characterArcSectionId })
            }
        }

        @Test
        fun `happy path`() {
            sceneRepository.givenScene(scene.givenCharacter(character))
            val characterArc = makeCharacterArc(character.id).also(characterArcRepository::givenCharacterArc)
            val characterArcSections = characterArc.arcSections

            coverSectionsInScene(*characterArcSections.map { it.id.uuid }.toTypedArray())

            with(savedScene!!) {
                assertTrue(isSameEntityAs(scene))
                assertTrue(characterArcSections.all { isCharacterArcSectionCovered(it.id) })
            }
            with(result as CoverCharacterArcSectionsInScene.ResponseModel) {
                val baseSections = characterArcSections.associateBy { it.id.uuid }
                assertEquals(
                    baseSections.keys,
                    sectionsCoveredByScene.map { it.characterArcSectionId }.toSet()
                )
                sectionsCoveredByScene.forEach {
                    it.sceneId.mustEqual(scene.id.uuid) { "Unexpected sceneId for CharacterArcSectionCoveredByScene" }
                    assertEquals(
                        character.id.uuid,
                        it.characterId
                    ) { "Unexpected characterId for CharacterArcSectionCoveredByScene" }
                    val baseSection = baseSections.getValue(it.characterArcSectionId)
                    it shouldBe characterArcSectionCoveredByScene(baseSection, characterArc, scene.id.uuid)
                }
                assert(sectionsUncovered.isEmpty()) { "No sections should have been uncovered" }
            }
        }

        @Test
        fun `Uncover sections at the same time`() {
            val characterArc = makeCharacterArc(character.id).also(characterArcRepository::givenCharacterArc)
            val characterArcSections = characterArc.arcSections
            scene.givenCharacter(character)
                .givenCoveredSection(characterArcSections.first())
                .also(sceneRepository::givenScene)

            coverSectionsInScene(
                *characterArcSections.drop(1).map { it.id.uuid }.toTypedArray(),
                removeArcSectionsIds = listOf(characterArcSections.first().id.uuid)
            )

            with(savedScene!!) {
                assertTrue(isSameEntityAs(scene))
                assertTrue(characterArcSections.drop(1).all { isCharacterArcSectionCovered(it.id) })
                characterArcSections.first().id
                    .let(this::isCharacterArcSectionCovered)
                    .let { assertFalse(it) { "Character Arc Section was not uncovered from scene" } }
            }
            with(result as CoverCharacterArcSectionsInScene.ResponseModel) {
                val baseSections = characterArcSections.drop(1).associateBy { it.id.uuid }
                assertEquals(
                    baseSections.keys,
                    sectionsCoveredByScene.map { it.characterArcSectionId }.toSet()
                )
                sectionsCoveredByScene.forEach {
                    it.sceneId.mustEqual(scene.id.uuid) { "Unexpected sceneId for CharacterArcSectionCoveredByScene" }
                    assertEquals(
                        character.id.uuid,
                        it.characterId
                    ) { "Unexpected characterId for CharacterArcSectionCoveredByScene" }
                    val baseSection = baseSections.getValue(it.characterArcSectionId)
                    it shouldBe characterArcSectionCoveredByScene(baseSection, characterArc, scene.id.uuid)
                }
                sectionsUncovered.single().run {
                    characterArcSectionId.mustEqual(characterArcSections.first().id.uuid) { "Unexpected characterArcSectionId for CharacterArcSectionUncoveredInScene" }
                    sceneId.mustEqual(scene.id.uuid) { "Unexpected sceneId for CharacterArcSectionUncoveredInScene" }
                    characterId.mustEqual(character.id.uuid) { "Unexpected characterId for CharacterArcSectionUncoveredInScene" }
                }
            }
        }

        private fun coverSectionsInScene(
            vararg characterArcSectionIds: UUID,
            removeArcSectionsIds: List<UUID> = listOf()
        ) {
            val request = CoverCharacterArcSectionsInScene.RequestModel(
                scene.id.uuid,
                character.id.uuid,
                removeArcSectionsIds,
                *characterArcSectionIds
            )
            runBlocking {
                useCase.invoke(request, output)
            }
        }

    }

}

fun availableCharacterArcSectionsForCharacterInScene(
    sceneId: UUID,
    characterId: UUID,
    additionalAssertions: (AvailableCharacterArcSectionsForCharacterInScene) -> Unit = {}
) = fun(actual: Any?) {
    actual as AvailableCharacterArcSectionsForCharacterInScene
    assertEquals(sceneId, actual.sceneId) { "Unexpected sceneId for AvailableCharacterArcSectionsForCharacterInScene" }
    assertEquals(
        characterId,
        actual.characterId
    ) { "Unexpected characterId for AvailableCharacterArcSectionsForCharacterInScene" }
    additionalAssertions(actual)
}