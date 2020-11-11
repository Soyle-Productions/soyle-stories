package com.soyle.stories.characterarc.createArcSectionDialog

import tornadofx.Stylesheet
import tornadofx.cssclass
import tornadofx.importStylesheet


class CreateArcSectionDialogStyles : Stylesheet() {

    companion object {
        val sectionTypeSelection by cssclass()
        val description by cssclass()

        init {
            importStylesheet<CreateArcSectionDialogStyles>()
        }
    }

    init {

    }

}