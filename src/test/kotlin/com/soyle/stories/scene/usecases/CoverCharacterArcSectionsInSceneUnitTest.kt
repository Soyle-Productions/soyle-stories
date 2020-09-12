package com.soyle.stories.scene.usecases

import arrow.core.k
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
import com.soyle.stories.common.*
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.doubles.CharacterArcSectionRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.scene.CharacterNotInScene
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.characterNotInScene
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.sceneDoesNotExist
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.AvailableCharacterArcSectionsForCharacterInScene
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CoverCharacterArcSectionsInScene
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CoverCharacterArcSectionsInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

/**
 * Summary:
 *  abs
 *
 * Preconditions:
 * 1. A scene has been created
 * 2. The character has been included in the scene
 *
 * Basic Course of Events:
 * 1. The user indicates that they want to cover character arc sections for a character in this scene
 * 2. The software responds by listing all the available character arcs for this character and marks any previously
 * covered character arc sections
 * 3. The user specifies which character arc sections should be covered by this scene for this character
 * 4. The software covers the specified character arc sections in this scene for this character
 *
 * Alternative Paths:
 * 1. In step 3, the user requests making a new character arc for this character first.
 *     1. The software responds by requesting the name of the new character arc and presenting a list of
 *     soon-to-be-created character arc sections
 *     2. The user inputs the name of the new character arc and selects one or more of the soon-to-be-created character
 *     arc sections
 *     3. The software creates the new character arc and the default character arc sections for the character then
 *     covers the specified, newly-created character arc sections in this scene for this character
 * 2. In step 1b, the user requests making a new character arc section while making the new character arc.
 *     1. The software responds by presenting a list of available character arc section types.
 *     2. The user selects one of the presented character arc section types.
 *     3. The user may repeat steps 2 - 2.2 multiple times.
 *     4. The software creates the new character arc, the default character arc sections, and the selected types of
 *     character arc sections for the character, then covers the specified, newly-created character arc sections in this
 *     scene for this character
 * 3. In step 3, the user specifies which character arc sections to uncover.  The post conditions are the same, but the
 * software also uncovers the specified character arc sections
 * 4. In step 3, the user requests making a new character arc section for one of the listed character arcs.
 *     1. The software responds by presenting a list of available character arc section types for that character arc.
 *     2. The user selects one of the presented character arc section types.
 *     3. The software creates the specified character arc section type for the specified character arc and covers it in
 *     this scene for this character.
 */
class CoverCharacterArcSectionsInSceneUnitTest {

    // Preconditions
    val scene = Scene(Project.Id(), "", StoryEvent.Id())
    val character = makeCharacter()

    // Post-conditions
    private var savedScene: Scene? = null
    private var createdCharacterArc: CharacterArc? = null
    private var createdCharacterArcSections: List<CharacterArcSection>? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::savedScene::set)
    private val characterArcRepository = CharacterArcRepositoryDouble()
    private val characterArcSectionRepository = CharacterArcSectionRepositoryDouble()
    private val useCase: CoverCharacterArcSectionsInScene =
        CoverCharacterArcSectionsInSceneUseCase(sceneRepository, characterArcRepository, characterArcSectionRepository)
    private var result: Any? = null

    val output = object : CoverCharacterArcSectionsInScene.OutputPort {
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
        fun `Scene does not exist`() {
            assertThrows<SceneDoesNotExist> {
                listAvailableCharacterArcsForCharacterInScene()
            } shouldBe sceneDoesNotExist(scene.id.uuid)
        }

        @Test
        fun `Character not in scene`() {
            givenSceneExists()
            assertThrows<CharacterNotInScene> {
                listAvailableCharacterArcsForCharacterInScene()
            } shouldBe characterNotInScene(scene.id.uuid, character.id.uuid)
        }

        @Test
        fun `No character arcs exist for character`() {
            givenSceneExists(withCharacterIncluded = true)
            listAvailableCharacterArcsForCharacterInScene()
            result shouldBe availableCharacterArcSectionsForCharacterInScene(scene.id.uuid, character.id.uuid) {
                assertTrue(it.isEmpty())
            }
        }

        @Test
        fun `character has character arcs`() {
            givenSceneExists(withCharacterIncluded = true)
            val characterArcCount = 5
            givenCharacterHasCharacterArcs(count = characterArcCount)
            listAvailableCharacterArcsForCharacterInScene()
            result shouldBe availableCharacterArcSectionsForCharacterInScene(scene.id.uuid, character.id.uuid) {
                assertEquals(characterArcCount, it.size)
                val uniqueThemeIds = it.asSequence().map { it.themeId }.toSet()
                assertEquals(characterArcCount, uniqueThemeIds.size)
                it.forEach { availableArc ->
                    val baseArc = characterArcRepository.characterArcs[Theme.Id(availableArc.themeId) to character.id]!!
                    assertEquals(baseArc.name, availableArc.characterArcName)
                    assertTrue(availableArc.isEmpty())
                }
            }
        }

        @Test
        fun `character arcs have sections`() {
            givenSceneExists(withCharacterIncluded = true)
            val baseArc = givenCharacterHasCharacterArcs(count = 1).single()
            val baseSections = givenCharacterArcHasSections(baseArc, Desire, MoralNeed, Plan)
            listAvailableCharacterArcsForCharacterInScene()
            result shouldBe availableCharacterArcSectionsForCharacterInScene(scene.id.uuid, character.id.uuid) {
                val availableArc = it.single()
                assertEquals(baseArc.name, availableArc.characterArcName)
                assertEquals(baseSections.map { it.id.uuid }.toSet(), availableArc.map { it.arcSectionId }.toSet())
                availableArc.forEach { section ->
                    val baseSection = baseSections.single { it.id.uuid == section.arcSectionId }
                    assertEquals(baseSection.template.name, section.templateName)
                    assertEquals(baseSection.value, section.sectionValue)
                    assertFalse(section.usedInScene)
                }
            }
        }

        @Test
        fun `some arc sections covered by scene already`() {
            givenSceneExists(withCharacterIncluded = true)
            val baseArc = givenCharacterHasCharacterArcs(count = 1).single()
            val baseSections = givenCharacterArcHasSections(baseArc, Desire, MoralNeed, Plan)
            givenSceneCoversSections(baseSections.first())
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
                }
            }
        }

        private fun listAvailableCharacterArcsForCharacterInScene() {
            runBlocking {
                useCase.listAvailableCharacterArcsForCharacterInScene(scene.id.uuid, character.id.uuid, output)
            }
        }

    }

    @Nested
    inner class `Cover sections in Scene`
    {

        @Test
        fun `Scene does not exist`() {
            assertThrows<SceneDoesNotExist> {
                coverSectionsInScene()
            } shouldBe sceneDoesNotExist(scene.id.uuid)
        }

        @Test
        fun `Character not in scene`() {
            givenSceneExists()
            assertThrows<CharacterNotInScene> {
                coverSectionsInScene()
            } shouldBe characterNotInScene(scene.id.uuid, character.id.uuid)
        }

        @Test
        fun `Character arc section doesn't exist`() {
            givenSceneExists(withCharacterIncluded = true)
            val characterArcSectionIds = List(5) { UUID.randomUUID() }
            assertThrows<CharacterArcSectionDoesNotExist> {
                coverSectionsInScene(*characterArcSectionIds.toTypedArray())
            } shouldBe {
                assertEquals(characterArcSectionIds.first(), it.characterArcSectionId)
            }
        }

        @Test
        fun `Some Character arc sections exist`() {
            givenSceneExists(withCharacterIncluded = true)
            val characterArcSectionIds = givenCharacterArcSectionsExist(3).map { it.id.uuid } +
                    List(2) { UUID.randomUUID() }
            assertThrows<CharacterArcSectionDoesNotExist> {
                coverSectionsInScene(*characterArcSectionIds.toTypedArray())
            } shouldBe {
                assertEquals(characterArcSectionIds[3], it.characterArcSectionId)
            }
        }

        @Test
        fun `happy path`() {
            givenSceneExists(withCharacterIncluded = true)
            val characterArcSections = givenCharacterArcSectionsExist(5)

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
                    assertEquals(scene.id.uuid, it.sceneId) { "Unexpected sceneId for CharacterArcSectionCoveredByScene" }
                    assertEquals(character.id.uuid, it.characterId) { "Unexpected characterId for CharacterArcSectionCoveredByScene" }
                    val baseSection = baseSections.getValue(it.characterArcSectionId)
                    assertEquals(baseSection.themeId.uuid, it.themeId) { "Unexpected themeId for CharacterArcSectionCoveredByScene" }
                }
            }
        }


        private fun coverSectionsInScene(vararg characterArcSectionIds: UUID)
        {
            val request = CoverCharacterArcSectionsInScene.RequestModel.CoverSections(
                scene.id.uuid,
                character.id.uuid,
                *characterArcSectionIds
            )
            runBlocking {
                useCase.coverSectionsInScene(request, output)
            }
        }

    }

    private fun givenSceneExists(withCharacterIncluded: Boolean = false, vararg additionalCharacters: Character) {
        sceneRepository.scenes[scene.id] = scene.run {
            if (withCharacterIncluded) {
                withCharacterIncluded(character).let {
                    additionalCharacters.fold(it) { next, char ->
                        next.withCharacterIncluded(char)
                    }
                }
            }
            else this
        }
    }

    private fun givenCharacterHasCharacterArcs(count: Int): List<CharacterArc> {
        return List(count) {
            val arc = CharacterArc(
                character.id,
                CharacterArcTemplate.default(),
                Theme.Id(),
                "Character Arc ${str()}"
            )
            characterArcRepository.givenCharacterArc(arc)
            arc
        }
    }

    private fun givenCharacterArcHasSections(arc: CharacterArc, vararg sectionTemplates: CharacterArcTemplateSection): List<CharacterArcSection>
    {
        return sectionTemplates.map {
            CharacterArcSection(
                CharacterArcSection.Id(UUID.randomUUID()),
                arc.characterId,
                arc.themeId,
                it,
                null,
                "Section Value ${str()}"
            )
        }.onEach {
            characterArcSectionRepository.characterArcSections[it.id] = it
        }
    }

    private fun givenCharacterArcSectionsExist(count: Int, forCharacter: Character.Id = character.id): List<CharacterArcSection>
    {
        return List(count) {
            CharacterArcSection(
                CharacterArcSection.Id(UUID.randomUUID()),
                forCharacter,
                Theme.Id(),
                CharacterArcTemplate.default().sections.random(),
                null,
                "Section Value ${str()}"
            )
        }.onEach {
            characterArcSectionRepository.characterArcSections[it.id] = it
        }
    }

    private fun givenSceneCoversSections(vararg sections: CharacterArcSection)
    {
        sceneRepository.scenes[scene.id] = sections.fold(sceneRepository.scenes[scene.id] ?: scene) { scene, section ->
            scene.withCharacterArcSectionCovered(section.characterId, section)
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
    assertEquals(characterId, actual.characterId) { "Unexpected characterId for AvailableCharacterArcSectionsForCharacterInScene" }
    additionalAssertions(actual)
}