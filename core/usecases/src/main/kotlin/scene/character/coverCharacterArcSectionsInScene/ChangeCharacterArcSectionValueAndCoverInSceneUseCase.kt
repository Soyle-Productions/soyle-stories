package com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangedCharacterArcSectionValue
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.theme.ThemeRepository

class ChangeCharacterArcSectionValueAndCoverInSceneUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository,
    private val sceneRepository: SceneRepository
) : ChangeCharacterArcSectionValueAndCoverInScene {

    override suspend fun invoke(
        request: ChangeCharacterArcSectionValueAndCoverInScene.RequestModel,
        output: ChangeCharacterArcSectionValueAndCoverInScene.OutputPort
    ) {
        val theme = themeRepository.getThemeOrError(Theme.Id(request.themeId))
        val characterInTheme = theme.getIncludedCharacterById(Character.Id(request.characterId))
            ?: throw CharacterNotInTheme(theme.id.uuid, request.characterId)
        if (characterInTheme !is MajorCharacter) throw CharacterIsNotMajorCharacterInTheme(request.characterId, theme.id.uuid)

        val characterArc = characterArcRepository.getCharacterArcOrError(characterInTheme.id.uuid, theme.id.uuid)
        val arcSection = characterArc.arcSections.find { it.id.uuid == request.arcSectionId }!!
            .withValue(request.value)
        val scene = sceneRepository.getSceneOrError(request.sceneId)

        characterArcRepository.replaceCharacterArcs(
            characterArc.withArcSectionsMapped {
                if (it.id == arcSection.id) arcSection
                else it
            }
        )
        sceneRepository.updateScene(
            scene.withCharacterArcSectionCovered(arcSection)
        )

        output.characterArcSectionValueChangedAndAddedToScene(
            ChangeCharacterArcSectionValueAndCoverInScene.ResponseModel(
                ChangedCharacterArcSectionValue(
                    arcSection.id.uuid,
                    characterInTheme.id.uuid,
                    theme.id.uuid,
                    null,
                    arcSection.value
                ),
                CharacterArcSectionCoveredByScene(
                    scene.id.uuid,
                    characterArc.characterId.uuid,
                    characterArc.themeId.uuid,
                    characterArc.id.uuid,
                    arcSection.id.uuid,
                    arcSection.template.name,
                    arcSection.value,
                    characterArc.name,
                    arcSection.template.allowsMultiple
                )
            )
        )

    }

}