package com.soyle.stories.desktop.config.character

import com.soyle.stories.character.removeCharacterFromStory.RemoveCharacterFromStoryOutput
import com.soyle.stories.character.renameCharacter.RenameCharacterController
import com.soyle.stories.character.renameCharacter.RenameCharacterControllerImpl
import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStoryUseCase
import com.soyle.stories.usecase.character.renameCharacter.RenameCharacter
import com.soyle.stories.usecase.character.renameCharacter.RenameCharacterUseCase
import com.soyle.stories.characterarc.addArcSectionToMoralArgument.AddArcSectionToMoralArgumentController
import com.soyle.stories.characterarc.addArcSectionToMoralArgument.AddArcSectionToMoralArgumentControllerImpl
import com.soyle.stories.characterarc.eventbus.RenameCharacterOutput
import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgumentController
import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgumentControllerImpl
import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgumentOutput
import com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgumentController
import com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgumentControllerImpl
import com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgumentOutput
import com.soyle.stories.usecase.character.addCharacterArcSectionToMoralArgument.AddCharacterArcSectionToMoralArgument
import com.soyle.stories.usecase.character.addCharacterArcSectionToMoralArgument.AddCharacterArcSectionToMoralArgumentUseCase
import com.soyle.stories.usecase.character.addCharacterArcSectionToMoralArgument.ListAvailableArcSectionTypesToAddToMoralArgument
import com.soyle.stories.usecase.character.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgument
import com.soyle.stories.usecase.character.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgumentUseCase
import com.soyle.stories.usecase.character.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgument
import com.soyle.stories.usecase.character.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgumentUseCase
import com.soyle.stories.usecase.character.viewBaseStoryStructure.ViewBaseStoryStructure
import com.soyle.stories.usecase.character.viewBaseStoryStructure.ViewBaseStoryStructureUseCase
import com.soyle.stories.characterarc.viewBaseStoryStructure.ViewBaseStoryStructureController
import com.soyle.stories.desktop.config.InProjectScope
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

            renameCharacter()
            removeCharacterFromStory()
            viewBaseStoryStructure()

        }
    }

    private fun InProjectScope.renameCharacter() {
        provide<RenameCharacter> {
            RenameCharacterUseCase(get(), get(), get())
        }
        provide(RenameCharacter.OutputPort::class) {
            RenameCharacterOutput(get(), get())
        }
        provide<RenameCharacterController> {
            RenameCharacterControllerImpl(applicationScope.get(), get(), get())
        }
    }

    private fun InProjectScope.removeCharacterFromStory() {
        provide<RemoveCharacterFromStory> {
            RemoveCharacterFromStoryUseCase(get(), get(), get())
        }

        provide(RemoveCharacterFromStory.OutputPort::class) {
            RemoveCharacterFromStoryOutput(get(), get(), get())
        }
    }

    private fun InProjectScope.viewBaseStoryStructure() {
        provide {
            ViewBaseStoryStructureController(
                applicationScope.get(),
                get()
            )
        }
        provide<ViewBaseStoryStructure> {
            ViewBaseStoryStructureUseCase(get(), get())
        }
    }

}