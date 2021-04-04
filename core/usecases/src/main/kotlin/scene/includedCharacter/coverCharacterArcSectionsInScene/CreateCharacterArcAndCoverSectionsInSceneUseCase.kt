package com.soyle.stories.usecase.scene.includedCharacter.coverCharacterArcSectionsInScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcTemplate
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.*
import com.soyle.stories.usecase.character.arc.planNewCharacterArc.CreatedCharacterArc
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.includedCharacter.coverCharacterArcSectionsInScene.CreateCharacterArcAndCoverSectionsInScene.*
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.createTheme.CreatedTheme
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

        val theme = createNewTheme(character, request.name.value)
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
            .withCharacterIncluded(character.id, character.name.value, null)
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
        val arc = CharacterArc.planNewCharacterArc(character.id, theme.id, request.name.value, arcTemplate)
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
            val createdArcSection = createdArcSectionsByTemplateId.getValue(it)
            CharacterArcSectionCoveredByScene(
                scene.id.uuid,
                arc.characterId.uuid,
                arc.themeId.uuid,
                arc.id.uuid,
                createdArcSection.id.uuid,
                createdArcSection.template.name,
                createdArcSection.value,
                arc.name,
                createdArcSection.template.allowsMultiple
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