package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import com.soyle.stories.characterarc.repositories.getCharacterArcOrError
import com.soyle.stories.characterarc.usecases.changeCharacterArcSectionValue.ChangedCharacterArcSectionValue
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError
import com.soyle.stories.scene.usecases.getSceneDetails.CoveredArcSectionInScene
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.repositories.getThemeOrError

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
                CoveredArcSectionInScene(
                    arcSection.id.uuid,
                    arcSection.template.name,
                    arcSection.value,
                    arcSection.template.allowsMultiple,
                    characterArc.id.uuid,
                    characterArc.name
                )
            )
        )

    }

}