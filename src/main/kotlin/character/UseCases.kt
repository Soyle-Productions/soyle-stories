package com.soyle.stories.desktop.config.character

import com.soyle.stories.characterarc.addArcSectionToMoralArgument.AddArcSectionToMoralArgumentController
import com.soyle.stories.characterarc.addArcSectionToMoralArgument.AddArcSectionToMoralArgumentControllerImpl
import com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument.AddCharacterArcSectionToMoralArgument
import com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument.AddCharacterArcSectionToMoralArgumentUseCase
import com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument.ListAvailableArcSectionTypesToAddToMoralArgument
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.addCharacterArcSectionToMoralArgument.AddCharacterArcSectionToMoralArgumentOutput

object UseCases {

    init {
        scoped<ProjectScope> {
            provide(
                ListAvailableArcSectionTypesToAddToMoralArgument::class,
                AddCharacterArcSectionToMoralArgument::class
            ) {
                AddCharacterArcSectionToMoralArgumentUseCase(get())
            }

            provide<AddArcSectionToMoralArgumentController> {
                AddArcSectionToMoralArgumentControllerImpl(
                    applicationScope.get(),
                    get(),
                    get()
                )
            }

            provide<AddCharacterArcSectionToMoralArgument.OutputPort> {
                AddCharacterArcSectionToMoralArgumentOutput(get())
            }

        }
    }

}