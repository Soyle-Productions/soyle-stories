package com.soyle.stories.characterarc.createArcSectionDialog

import com.soyle.stories.gui.View
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.AvailableCharacterArcSectionTypesForCharacterArc
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.GetAvailableCharacterArcSectionTypesForCharacterArc

class CreateArcSectionDialogPresenter(
    private val view: View.Nullable<CreateArcSectionDialogViewModel>
) : GetAvailableCharacterArcSectionTypesForCharacterArc.OutputPort {

    override suspend fun receiveAvailableCharacterArcSectionTypesForCharacterArc(response: AvailableCharacterArcSectionTypesForCharacterArc) {
        view.update {
            CreateArcSectionDialogViewModel(
                defaultTitle = "Create Character Arc Section",
                modifyingExistingTitle = "Modify Character Arc Section Value",
                sectionTypeSelectionFieldLabel = "Type",
                sectionTypeSelectionNoSelectionLabel = "- Select Section Type -",
                descriptionFieldLabel = "Description",
                sectionTypeOptions = response.map {
                    val existingSection = it.existingSection
                    if (existingSection != null && ! it.multiple) {
                        SectionTypeOption.AlreadyUsed(
                            it.templateSectionId.toString(),
                            it.name,
                            existingSection.first.toString(),
                            existingSection.second,
                            "This moral argument already includes a ${it.name}.  By selecting it, you will instead modify the value of the existing section and link it to this scene."
                        )
                    } else {
                        SectionTypeOption(
                            it.templateSectionId.toString(),
                            it.name
                        )
                    }
                },
                confirmUnsavedDescriptionChanges = "You have modified the description.  If you change the type, you will lose the changes you've made.",
                defaultPrimaryButtonLabel = "Create",
                modifyingPrimaryButtonLabel = "Change"
            )
        }
    }

}