package com.soyle.stories.character.createArcSection

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.arc.section.addSectionToArc.AddSectionToCharacterArc
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.CoverCharacterArcSectionsInScene
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.CreateCharacterArcSectionAndCoverInScene
import kotlinx.coroutines.Job
import java.util.*

class CreateArcSectionControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val addSectionToCharacterArc: AddSectionToCharacterArc,
    private val addSectionToCharacterArcOutput: AddSectionToCharacterArc.OutputPort,
    private val coverCharacterArcSectionsInScene: CoverCharacterArcSectionsInScene,
    private val coverCharacterArcSectionsInSceneOutput: CoverCharacterArcSectionsInScene.OutputPort,
    private val createCharacterArcSectionAndCoverInScene: CreateCharacterArcSectionAndCoverInScene,
    private val createCharacterArcSectionAndCoverInSceneOutputPort: CreateCharacterArcSectionAndCoverInScene.OutputPort
) : CreateArcSectionController {

    override fun createArcSection(
        characterId: Character.Id,
        themeId: Theme.Id,
        sectionTemplateId: CharacterArcTemplateSection.Id,
        value: String
    ): Job {
        val request = AddSectionToCharacterArc.RequestModel(
            characterId, themeId, sectionTemplateId
        )
        return threadTransformer.async {
            addSectionToCharacterArc(request, addSectionToCharacterArcOutput)
        }
    }

    override fun createArcSectionAndCoverInScene(
        characterId: String,
        themeId: String,
        sectionTemplateId: String,
        value: String,
        sceneId: String
    ) {
        threadTransformer.async {
            createCharacterArcSectionAndCoverInScene.invoke(
                CreateCharacterArcSectionAndCoverInScene.RequestModel(
                    UUID.fromString(themeId),
                    UUID.fromString(characterId),
                    UUID.fromString(sceneId),
                    UUID.fromString(sectionTemplateId),
                    value
                ),
                createCharacterArcSectionAndCoverInSceneOutputPort
            )
        }
    }

}