package com.soyle.stories.desktop.config.character

import com.soyle.stories.character.deleteCharacterArc.DeleteCharacterArcNotifier
import com.soyle.stories.character.renameCharacter.CharacterRenamedNotifier
import com.soyle.stories.characterarc.baseStoryStructure.*
import com.soyle.stories.characterarc.changeSectionValue.ChangedCharacterArcSectionValueNotifier
import com.soyle.stories.characterarc.characterList.CharacterListController
import com.soyle.stories.characterarc.characterList.CharacterListModel
import com.soyle.stories.characterarc.characterList.CharacterListPresenter
import com.soyle.stories.characterarc.characterList.CharacterListViewListener
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogController
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogState
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogViewListener
import com.soyle.stories.characterarc.deleteCharacterDialog.DeleteCharacterDialogController
import com.soyle.stories.characterarc.deleteCharacterDialog.DeleteCharacterDialogState
import com.soyle.stories.characterarc.deleteCharacterDialog.DeleteCharacterDialogViewListener
import com.soyle.stories.characterarc.eventbus.ChangeThematicSectionValueNotifier
import com.soyle.stories.characterarc.eventbus.RenameCharacterArcNotifier
import com.soyle.stories.characterarc.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionNotifier
import com.soyle.stories.characterarc.planNewCharacterArc.CreatedCharacterArcNotifier
import com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionNotifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.renameLocation.LocationRenamedNotifier
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeNotifier

object Presentation {

    init {
        scoped<ProjectScope> {

            characterList()
            deleteCharacterDialog()

            createCharacterArcSectionDialog()

        }

        baseStoryStructureTool()
    }

    private fun InProjectScope.characterList() = provide<CharacterListViewListener> {
        val characterListPresenter = CharacterListPresenter(
            get<CharacterListModel>(),
            get()
        )

        characterListPresenter listensTo get<CreatedCharacterArcNotifier>()
        characterListPresenter listensTo get<CharacterIncludedInThemeNotifier>()
        characterListPresenter listensTo get<DeleteCharacterArcNotifier>()
        characterListPresenter listensTo get<RenameCharacterArcNotifier>()
        characterListPresenter listensTo get<CharacterRenamedNotifier>()

        CharacterListController(
            projectId.toString(),
            applicationScope.get(),
            get(),
            characterListPresenter,
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    private fun InProjectScope.deleteCharacterDialog()
    {
        provide<DeleteCharacterDialogViewListener> {
            DeleteCharacterDialogController(
                get<DeleteCharacterDialogState>(),
                get()
            )
        }
    }

    private fun InProjectScope.createCharacterArcSectionDialog()
    {
        provide<CreateArcSectionDialogViewListener> {
            CreateArcSectionDialogController(
                applicationScope.get(),
                get(),
                get(),
                get(),
                get<CreateArcSectionDialogState>()
            )
        }
    }

    private fun baseStoryStructureTool()
    {
        scoped<BaseStoryStructureScope> {
            provide<BaseStoryStructureViewListener> {
                val presenter = BaseStoryStructurePresenter(
                    get<BaseStoryStructureModel>(),
                    projectScope.get<LocationRenamedNotifier>(),
                    projectScope.get<ChangeThematicSectionValueNotifier>(),
                    projectScope.get<LinkLocationToCharacterArcSectionNotifier>(),
                    projectScope.get<UnlinkLocationFromCharacterArcSectionNotifier>(),
                    projectScope.get()
                ).also {
                    it listensTo projectScope.get<ChangedCharacterArcSectionValueNotifier>()
                }

                BaseStoryStructureController(
                    projectScope.applicationScope.get(),
                    type.themeId.toString(),
                    type.characterId.toString(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    presenter
                )
            }

        }
    }

}