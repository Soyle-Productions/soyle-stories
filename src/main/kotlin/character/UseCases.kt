package com.soyle.stories.desktop.config.character

import com.soyle.stories.characterarc.addArcSectionToMoralArgument.AddArcSectionToMoralArgumentController
import com.soyle.stories.characterarc.addArcSectionToMoralArgument.AddArcSectionToMoralArgumentControllerImpl
import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgumentController
import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgumentControllerImpl
import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgumentOutput
import com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgumentController
import com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgumentControllerImpl
import com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgumentOutput
import com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument.AddCharacterArcSectionToMoralArgument
import com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument.AddCharacterArcSectionToMoralArgumentUseCase
import com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument.ListAvailableArcSectionTypesToAddToMoralArgument
import com.soyle.stories.characterarc.usecases.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgument
import com.soyle.stories.characterarc.usecases.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgumentUseCase
import com.soyle.stories.characterarc.usecases.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgument
import com.soyle.stories.characterarc.usecases.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgumentUseCase
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




            provide<MoveCharacterArcSectionInMoralArgument> {
                MoveCharacterArcSectionInMoralArgumentUseCase(get())
            }

            provide<MoveCharacterArcSectionInMoralArgumentController> {
                MoveCharacterArcSectionInMoralArgumentControllerImpl(
                    applicationScope.get(), get(), get()
                )
            }

            provide<MoveCharacterArcSectionInMoralArgument.OutputPort> {
                MoveCharacterArcSectionInMoralArgumentOutput(get())
            }



            provide<RemoveCharacterArcSectionFromMoralArgument> {
                RemoveCharacterArcSectionFromMoralArgumentUseCase(get())
            }

            provide<RemoveCharacterArcSectionFromMoralArgumentController> {
                RemoveCharacterArcSectionFromMoralArgumentControllerImpl(
                    applicationScope.get(), get(), get()
                )
            }

            provide<RemoveCharacterArcSectionFromMoralArgument.OutputPort> {
                RemoveCharacterArcSectionFromMoralArgumentOutput(get(), get())
            }

        }
    }

}