package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.CharacterNotInScene
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError
import java.util.*
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CoverCharacterArcSectionsInScene.*
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository

class CoverCharacterArcSectionsInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val characterArcRepository: CharacterArcRepository,
    private val characterArcSectionRepository: CharacterArcSectionRepository
) : CoverCharacterArcSectionsInScene {

    override suspend fun listAvailableCharacterArcsForCharacterInScene(
        sceneId: UUID,
        characterId: UUID,
        output: OutputPort
    ) {
        val scene = getScene(sceneId, characterId)
        val arcs = getCharacterArcsForCharacter(Character.Id(characterId))

        output.availableCharacterArcSectionsForCharacterInSceneListed(
            AvailableCharacterArcSectionsForCharacterInScene(sceneId, characterId, arcs.map { (arc, sections) ->
                CharacterArcUsedInScene(
                    characterId,
                    arc.themeId.uuid,
                    arc.name,
                    sections.map {
                        ArcSectionUsedInScene(
                            it.id.uuid,
                            it.template.name,
                            it.value,
                            scene.isCharacterArcSectionCovered(it.id)
                        )
                    }
                )
            })
        )
    }

    override suspend fun coverSectionsInScene(request: RequestModel.CoverSections, output: OutputPort) {
        val scene = getScene(request.sceneId, request.characterId)
        val sections = getCharacterArcSections(request)

        val updatedScene = scene.withCharacterArcSectionsCovered(Character.Id(request.characterId), sections)
        sceneRepository.updateScene(updatedScene)

        val coveredSections = sections.map {
            CharacterArcSectionCoveredByScene(scene.id.uuid, request.characterId, it.themeId.uuid, it.id.uuid)
        }
        val responseModel = ResponseModel(coveredSections)
        output.characterArcSectionsCoveredInScene(responseModel)
    }

    private fun Scene.withCharacterArcSectionsCovered(characterId: Character.Id, sections: List<CharacterArcSection>): Scene {
        return sections.fold(this) { scene, arcSection ->
            scene.withCharacterArcSectionCovered(characterId, arcSection)
        }
    }

    private suspend fun getCharacterArcSections(request: RequestModel.CoverSections): List<CharacterArcSection> {
        val requestedIdSet = request.sections.map(CharacterArcSection::Id).toSet()
        val sections = characterArcSectionRepository.getCharacterArcSectionsById(requestedIdSet)
        verifyAllRequestedCharacterArcSectionsExist(requestedIdSet, sections)
        return sections
    }

    private fun verifyAllRequestedCharacterArcSectionsExist(
        requestedIds: Set<CharacterArcSection.Id>,
        existingSections: List<CharacterArcSection>
    ) {
        val existingIdSet = existingSections.map { it.id }.toSet()
        requestedIds.minus(existingIdSet).firstOrNull()?.let {
            throw CharacterArcSectionDoesNotExist(it.uuid)
        }
    }

    private suspend fun getCharacterArcsForCharacter(characterId: Character.Id): List<Pair<CharacterArc, List<CharacterArcSection>>> {
        val allSections = characterArcSectionRepository.getCharacterArcSectionsForCharacter(characterId).groupBy {
            it.themeId
        }
        return characterArcRepository.listCharacterArcsForCharacter(characterId).map {
            it to allSections.getOrDefault(it.themeId, listOf())
        }
    }

    private suspend fun getScene(sceneId: UUID, characterId: UUID): Scene {
        val scene = sceneRepository.getSceneOrError(sceneId)

        if (!scene.includesCharacter(Character.Id(characterId))) throw CharacterNotInScene(sceneId, characterId)
        return scene
    }
}