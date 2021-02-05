package com.soyle.stories.characterarc.createArcSectionDialog

import com.soyle.stories.common.Model
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

class CreateArcSectionDialogState : Model<ProjectScope, CreateArcSectionDialogViewModel>(ProjectScope::class) {
    override val applicationScope: ApplicationScope
        get() = scope.applicationScope

    val title = bind(CreateArcSectionDialogViewModel::defaultTitle)
    val modifyingExistingTitle = bind(CreateArcSectionDialogViewModel::modifyingExistingTitle)
    val sectionTypeSelectionFieldLabel = bind(CreateArcSectionDialogViewModel::sectionTypeSelectionFieldLabel)
    val sectionTypeSelectionNoSelectionLabel = bind(CreateArcSectionDialogViewModel::sectionTypeSelectionNoSelectionLabel)
    val descriptionFieldLabel = bind(CreateArcSectionDialogViewModel::descriptionFieldLabel)
    val confirmUnsavedDescriptionChanges = bind(CreateArcSectionDialogViewModel::confirmUnsavedDescriptionChanges)
    val defaultPrimaryButtonLabel = bind(CreateArcSectionDialogViewModel::defaultPrimaryButtonLabel)
    val modifyingPrimaryButtonLabel = bind(CreateArcSectionDialogViewModel::modifyingPrimaryButtonLabel)

    val selectedType = SimpleObjectProperty<SectionTypeOption?>(null)
    val description = SimpleStringProperty("")

    val sectionTypeOptions = bind(CreateArcSectionDialogViewModel::sectionTypeOptions)

    fun reset() {
        selectedType.set(null)
        description.set("")
    }

}