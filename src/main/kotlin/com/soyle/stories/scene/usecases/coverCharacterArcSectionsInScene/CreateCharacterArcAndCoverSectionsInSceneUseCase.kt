package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.characterarc.CharacterArcNameCannotBeBlank
import com.soyle.stories.characterarc.CharacterArcTemplateSectionDoesNotExist
import com.soyle.stories.characterarc.repositories.CharacterArcTemplateRepository
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.CreatedCharacterArc
import com.soyle.stories.entities.*
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CreateCharacterArcAndCoverSectionsInScene.*
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import java.util.*

class CreateCharacterArcAndCoverSectionsInSceneUseCase(
    private val characterRepository: CharacterRepository,
    private val characterArcRepository: CharacterArcRepository,
    private val sceneRepository: SceneRepository,
    private val themeRepository: ThemeRepository,
    private val characterArcTemplateRepository: CharacterArcTemplateRepository
) : CreateCharacterArcAndCoverSectionsInScene {

    override suspend fun listCharacterArcSectionTypesForNewArc(output: OutputPort) {
        getDefaultCharacterArcTemplate()
            .sections.groupBy(CharacterArcTemplateSection::isRequired)
            .run {
                CharacterArcSectionTypes(
                    getOrDefault(true, listOf()).convertEachToResponseModel(),
                    getOrDefault(false, listOf()).convertEachToResponseModel()
                )
            }
            .let { output.receiveCharacterArcSectionTypes(it) }
    }

    override suspend fun invoke(request: RequestModel, output: OutputPort) {
        val character = getCharacter(request.characterId)
        val scene = sceneRepository.getSceneOrError(request.sceneId)

        val theme = createNewTheme(character, request.name)
        val arc = createNewCharacterArc(character, theme, request)

        val coveredSections = coverRequestedSectionsInScene(request.coverSectionsWithTemplateIds, arc, scene)

        ResponseModel(
            CreatedTheme(theme),
            CreatedCharacterArc(arc),
            coveredSections
        )
            .let { output.characterArcCreatedAndSectionsCovered(it) }
    }

    private suspend fun getCharacter(characterId: UUID): Character =
        characterRepository.getCharacterById(Character.Id(characterId))
            ?: throw CharacterDoesNotExist(characterId)

    private suspend fun createNewTheme(
        character: Character,
        name: String
    ): Theme {
        val theme = Theme(character.projectId, name)
            .withCharacterIncluded(character.id, character.name, null)
            .withCharacterPromoted(character.id)
        themeRepository.addTheme(theme)
        return theme
    }

    private suspend fun createNewCharacterArc(
        character: Character,
        theme: Theme,
        request: RequestModel
    ): CharacterArc {
        val arcTemplate = getDefaultCharacterArcTemplate()
        if (request.name.isBlank()) throw CharacterArcNameCannotBeBlank(request.characterId, UUID.randomUUID())
        val arc = CharacterArc.planNewCharacterArc(character.id, theme.id, request.name, arcTemplate)
            .withAdditionalRequestedArcSectionTemplates(request, arcTemplate)
        characterArcRepository.addNewCharacterArc(arc)
        return arc
    }

    private suspend fun coverRequestedSectionsInScene(
        requestedTemplateIds: List<UUID>,
        arc: CharacterArc,
        scene: Scene,
    ): List<CharacterArcSectionCoveredByScene> {
        val createdArcSectionsByTemplateId = arc.arcSections.associateBy { it.template.id.uuid }

        val updatedScene = requestedTemplateIds.fold(scene) { nextScene, templateUUID ->
            val arcSection = createdArcSectionsByTemplateId.getValue(templateUUID)
            nextScene.withCharacterArcSectionCovered(arcSection)
        }
        sceneRepository.updateScene(updatedScene)
        return requestedTemplateIds.map {
            CharacterArcSectionCoveredByScene(
                scene.id.uuid,
                arc.characterId.uuid,
                arc.themeId.uuid,
                createdArcSectionsByTemplateId.getValue(it).id.uuid
            )
        }
    }


    private fun CharacterArc.withAdditionalRequestedArcSectionTemplates(
        request: RequestModel,
        template: CharacterArcTemplate
    ): CharacterArc {
        val templateSections = template.sections.associateBy { it.id.uuid }
        request.coverSectionsWithTemplateIds.forEach {
            if (it !in templateSections) throw CharacterArcTemplateSectionDoesNotExist(it)
        }
        val unRequiredTemplatesToCreate =
            request.coverSectionsWithTemplateIds.toSet() - arcSections.map { it.template.id.uuid }

        return if (unRequiredTemplatesToCreate.isNotEmpty()) {
            unRequiredTemplatesToCreate.fold(this) { nextArc, templateUUID ->
                nextArc.withArcSection(
                    templateSections.getValue(templateUUID)
                )
            }
        } else this
    }

    private suspend fun getDefaultCharacterArcTemplate() = characterArcTemplateRepository.getDefaultTemplate()

    private fun List<CharacterArcTemplateSection>.convertEachToResponseModel() = map {
        CharacterArcSectionType(
            it.id.uuid, it.name
        )
    }

}