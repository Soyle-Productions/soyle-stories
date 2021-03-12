package com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterArcSectionDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene.CoverCharacterArcSectionsInScene.*
import java.util.*

class CoverCharacterArcSectionsInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val characterArcRepository: CharacterArcRepository
) : CoverCharacterArcSectionsInScene, GetAvailableCharacterArcsForCharacterInScene {

    override suspend fun invoke(
        sceneId: UUID,
        characterId: UUID,
        output: GetAvailableCharacterArcsForCharacterInScene.OutputPort
    ) {
        val scene = getScene(sceneId, characterId)
        val arcs = characterArcRepository.listCharacterArcsForCharacter(Character.Id(characterId))

        output.availableCharacterArcSectionsForCharacterInSceneListed(
            AvailableCharacterArcSectionsForCharacterInScene(sceneId, characterId, arcs.map { arc ->
                CharacterArcUsedInScene(
                    characterId,
                    arc.id.uuid,
                    arc.themeId.uuid,
                    arc.name,
                    arc.arcSections.map {
                        ArcSectionUsedInScene(
                            it.id.uuid,
                            it.template.name,
                            it.value,
                            scene.isCharacterArcSectionCovered(it.id),
                            it.template.allowsMultiple
                        )
                    }
                )
            })
        )
    }

    override suspend fun invoke(request: RequestModel, output: OutputPort) {
        val scene = getScene(request.sceneId, request.characterId)
        val sectionsByArcs = getCharacterArcSections(request)
        val sections = sectionsByArcs.values.flatten()

        val removeSectionIdSet = request.removeSections.toSet()
        val updatedScene = scene.withCharacterArcSectionsCovered(sections.filter { it.id.uuid !in removeSectionIdSet })
            .withCharacterArcSectionsUncovered(sections.filter { it.id.uuid in removeSectionIdSet })
        sceneRepository.updateScene(updatedScene)

        val coveredSections = sectionsByArcs.flatMap { (arc, sections) ->
            sections.map { section ->
                CharacterArcSectionCoveredByScene(
                    scene.id.uuid,
                    request.characterId,
                    section.themeId.uuid,
                    arc.id.uuid,
                    section.id.uuid,
                    section.template.name,
                    section.value,
                    arc.name,
                    section.template.allowsMultiple
                )
            }
        }
        val responseModel = ResponseModel(coveredSections, request.removeSections.map {
            CharacterArcSectionUncoveredInScene(scene.id.uuid, request.characterId, it)
        })
        output.characterArcSectionsCoveredInScene(responseModel)
    }

    private fun Scene.withCharacterArcSectionsCovered(sections: List<CharacterArcSection>): Scene {
        return sections.fold(this) { scene, arcSection ->
            scene.withCharacterArcSectionCovered(arcSection)
        }
    }

    private fun Scene.withCharacterArcSectionsUncovered(sections: List<CharacterArcSection>): Scene {
        return sections.fold(this) { scene, arcSection ->
            scene.withoutCharacterArcSectionCovered(arcSection)
        }
    }

    private suspend fun getCharacterArcSections(request: RequestModel): Map<CharacterArc, List<CharacterArcSection>> {
        val requestedIdSet = (request.sections + request.removeSections).map(CharacterArcSection::Id).toSet()
        val sections = characterArcRepository.getCharacterArcsContainingArcSections(requestedIdSet)
            .asSequence()
            .map { arc ->
                arc to arc.arcSections.filter { it.id in requestedIdSet }
            }.toMap()
        verifyAllRequestedCharacterArcSectionsExist(requestedIdSet, sections.values.flatten())
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

    private suspend fun getScene(sceneId: UUID, characterId: UUID): Scene {
        val scene = sceneRepository.getSceneOrError(sceneId)

        if (!scene.includesCharacter(Character.Id(characterId))) throw SceneDoesNotIncludeCharacter(Scene.Id(sceneId), Character.Id(characterId))
        return scene
    }
}