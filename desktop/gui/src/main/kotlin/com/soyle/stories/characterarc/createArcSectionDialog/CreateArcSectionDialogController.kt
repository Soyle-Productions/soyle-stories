package com.soyle.stories.characterarc.createArcSectionDialog

import com.soyle.stories.character.createArcSection.CreateArcSectionController
import com.soyle.stories.characterarc.changeSectionValue.ChangeSectionValueController
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.gui.View
import com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene.GetAvailableCharacterArcSectionTypesForCharacterArc

class CreateArcSectionDialogController private constructor(
    private val threadTransformer: ThreadTransformer,
    private val getAvailableCharacterArcSectionTypesForCharacterArc: GetAvailableCharacterArcSectionTypesForCharacterArc,
    private val createArcSectionController: CreateArcSectionController,
    private val changeArcSectionController: ChangeSectionValueController,
    private val presenter: CreateArcSectionDialogPresenter
) : CreateArcSectionDialogViewListener {

    constructor(
        threadTransformer: ThreadTransformer,
        getAvailableCharacterArcSectionTypesForCharacterArc: GetAvailableCharacterArcSectionTypesForCharacterArc,
        createArcSectionController: CreateArcSectionController,
        changeArcSectionController: ChangeSectionValueController,
        view: View.Nullable<CreateArcSectionDialogViewModel>
    ) : this(
        threadTransformer,
        getAvailableCharacterArcSectionTypesForCharacterArc,
        createArcSectionController,
        changeArcSectionController,
        CreateArcSectionDialogPresenter(view)
    )

    override fun getValidState(themeId: Theme.Id, characterId: Character.Id) {
        threadTransformer.async {
            getAvailableCharacterArcSectionTypesForCharacterArc.invoke(
                themeId.uuid,
                characterId.uuid,
                presenter
            )
        }
    }

    override fun createArcSection(
        characterId: Character.Id,
        themeId: Theme.Id,
        sectionTemplateId: CharacterArcTemplateSection.Id,
        description: String
    ) {
        createArcSectionController.createArcSection(characterId, themeId, sectionTemplateId, description)
            .invokeOnCompletion {
                if (it != null) presenter.complete()
            }
    }

    override fun modifyArcSection(
        characterId: Character.Id,
        themeId: Theme.Id,
        arcSectionId: CharacterArcSection.Id,
        description: String
    ) {
        changeArcSectionController.changeValueOfArcSection(
            themeId.uuid.toString(), characterId.uuid.toString(), arcSectionId.uuid.toString(), description
        )
            .invokeOnCompletion {
                if (it != null) presenter.complete()
            }
    }

}