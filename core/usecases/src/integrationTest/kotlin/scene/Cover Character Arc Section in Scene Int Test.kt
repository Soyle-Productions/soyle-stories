package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.character.Drive
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.character.makeCharacterArc
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.character.arc.section.addSectionToArc.AddSectionToCharacterArc
import com.soyle.stories.usecase.character.arc.section.addSectionToArc.AddSectionToCharacterArcUseCase
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.includedCharacter.coverCharacterArcSectionsInScene.AvailableCharacterArcSectionsForCharacterInScene
import com.soyle.stories.usecase.scene.includedCharacter.coverCharacterArcSectionsInScene.CoverCharacterArcSectionsInScene
import com.soyle.stories.usecase.scene.includedCharacter.coverCharacterArcSectionsInScene.CoverCharacterArcSectionsInSceneUseCase
import com.soyle.stories.usecase.scene.includedCharacter.coverCharacterArcSectionsInScene.GetAvailableCharacterArcsForCharacterInScene
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class `Cover Character Arc Section in Scene Int Test` {

    private val character = makeCharacter()
    private val scene = makeScene(projectId = character.projectId)
        .withCharacterIncluded(character).scene
    private val characterArc = makeCharacterArc(character.id)

    private val sceneRepository = SceneRepositoryDouble(listOf(scene))
    private val characterArcRepository = CharacterArcRepositoryDouble()
        .apply { givenCharacterArc(characterArc) }

    private val coverCharacterArcSectionsInScene: CoverCharacterArcSectionsInScene =
        CoverCharacterArcSectionsInSceneUseCase(sceneRepository, characterArcRepository)
    private val addSectionToCharacterArc: AddSectionToCharacterArc =
        AddSectionToCharacterArcUseCase(characterArcRepository)
    private val listAvailableArcsForCharacterInScene: GetAvailableCharacterArcsForCharacterInScene =
        CoverCharacterArcSectionsInSceneUseCase(sceneRepository, characterArcRepository)

    @Test
    fun `covered sections are listed as covered`() = runBlocking {
        val coveredSection = coverCharacterArcSection(characterArc.arcSections.first().id)
            .sectionsCoveredByScene.single()
        val listedSections = listAvailableArcSections()
        listedSections.find { it.characterArcId == characterArc.id.uuid }!!
            .find { it.arcSectionId == coveredSection.characterArcSectionId }!!
            .usedInScene.mustEqual(true)
    }

    @Test
    fun `can cover listed section`() = runBlocking {
        val listedSections = listAvailableArcSections()
        val coveredSection = coverCharacterArcSection(CharacterArcSection.Id(listedSections.first().first().arcSectionId))
            .sectionsCoveredByScene.single()
        coveredSection.characterArcId.mustEqual(listedSections.first().characterArcId)
        coveredSection.characterArcSectionId.mustEqual(listedSections.first().first().arcSectionId)
    }

    @Test
    fun `created section can be covered`() = runBlocking {
        val section = createCharacterArcSection().sectionAddedToCharacterArc
        val coveredSection = coverCharacterArcSection(CharacterArcSection.Id(section.characterArcSectionId))
            .sectionsCoveredByScene.single()
        coveredSection.characterArcSectionId.mustEqual(section.characterArcSectionId)
    }

    @Test
    fun `uncovered section are listed as uncovered`() = runBlocking {
        val sectionId = characterArc.arcSections.first().id
        coverCharacterArcSection(sectionId)
        uncoverCharacterArcSection(sectionId)
        val listedSections = listAvailableArcSections()
        listedSections.find { it.characterArcId == characterArc.id.uuid }!!
            .find { it.arcSectionId == sectionId.uuid }!!
            .usedInScene.mustEqual(false)
    }

    private suspend fun createCharacterArcSection(): AddSectionToCharacterArc.ResponseModel {
        val output = object : AddSectionToCharacterArc.OutputPort {
            lateinit var result: AddSectionToCharacterArc.ResponseModel
            override suspend fun receiveAddSectionToCharacterArcResponse(response: AddSectionToCharacterArc.ResponseModel) {
                result = response
            }
        }
        val request = AddSectionToCharacterArc.RequestModel(character.id, characterArc.themeId, Drive.id)
        addSectionToCharacterArc(request, output)
        return output.result
    }

    private suspend fun coverCharacterArcSection(sectionId: CharacterArcSection.Id): CoverCharacterArcSectionsInScene.ResponseModel {
        val output = object : CoverCharacterArcSectionsInScene.OutputPort {
            lateinit var result: CoverCharacterArcSectionsInScene.ResponseModel
            override suspend fun characterArcSectionsCoveredInScene(response: CoverCharacterArcSectionsInScene.ResponseModel) {
                result = response
            }
        }
        val request = CoverCharacterArcSectionsInScene.RequestModel(
            scene.id.uuid, character.id.uuid, removeSections = emptyList(), sectionId.uuid
        )
        coverCharacterArcSectionsInScene(request, output)
        return output.result
    }

    private suspend fun uncoverCharacterArcSection(sectionId: CharacterArcSection.Id): CoverCharacterArcSectionsInScene.ResponseModel {
        val output = object : CoverCharacterArcSectionsInScene.OutputPort {
            lateinit var result: CoverCharacterArcSectionsInScene.ResponseModel
            override suspend fun characterArcSectionsCoveredInScene(response: CoverCharacterArcSectionsInScene.ResponseModel) {
                result = response
            }
        }
        val request = CoverCharacterArcSectionsInScene.RequestModel(
            scene.id.uuid, character.id.uuid, removeSections = listOf(sectionId.uuid)
        )
        coverCharacterArcSectionsInScene(request, output)
        return output.result
    }

    private suspend fun listAvailableArcSections(): AvailableCharacterArcSectionsForCharacterInScene {
        val output = object : GetAvailableCharacterArcsForCharacterInScene.OutputPort {
            lateinit var result: AvailableCharacterArcSectionsForCharacterInScene
            override suspend fun availableCharacterArcSectionsForCharacterInSceneListed(response: AvailableCharacterArcSectionsForCharacterInScene) {
                result = response
            }
        }
        listAvailableArcsForCharacterInScene.invoke(scene.id.uuid, character.id.uuid, output)
        return output.result
    }

}