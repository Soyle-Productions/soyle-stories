package com.soyle.stories.characterarc.createArcSectionDialog

import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.character.CharacterArcTemplateSection

data class CreateArcSectionDialogViewModel(
    val defaultTitle: String,
    val modifyingExistingTitle: String,
    val sectionTypeSelectionFieldLabel: String,
    val sectionTypeSelectionNoSelectionLabel: String,
    val descriptionFieldLabel: String,
    val sectionTypeOptions: List<SectionTypeOption>?,
    val done: Boolean,
    /**
     * The message presented to the user if they have selected an [SectionTypeOption.AlreadyUsed] and have modified the
     * description at all and then chose a different [SectionTypeOption].
     *
     * Usually something along the lines of "The changes you've made to the description will not be saved.  Are you sure?"
     */
    val confirmUnsavedDescriptionChanges: String,
    val defaultPrimaryButtonLabel: String,
    val modifyingPrimaryButtonLabel: String
)

sealed class SectionTypeOption {
    abstract val sectionTypeId: CharacterArcTemplateSection.Id
    abstract val sectionTypeName: String

    companion object {
        operator fun invoke(sectionTypeId: CharacterArcTemplateSection.Id, sectionTypeName: String): SectionTypeOption =
            Available(sectionTypeId, sectionTypeName)
    }

    private class Available(
        override val sectionTypeId: CharacterArcTemplateSection.Id,
        override val sectionTypeName: String
    ) : SectionTypeOption()

    class AlreadyUsed(
        override val sectionTypeId: CharacterArcTemplateSection.Id,
        override val sectionTypeName: String,
        val existingSectionId: CharacterArcSection.Id,
        val description: String,
        val message: String
    ) : SectionTypeOption()
}