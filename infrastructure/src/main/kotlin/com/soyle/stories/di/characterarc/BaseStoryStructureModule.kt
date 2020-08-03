package com.soyle.stories.di.characterarc

import com.soyle.stories.characterarc.baseStoryStructure.*
import com.soyle.stories.characterarc.changeSectionValue.ChangedCharacterArcSectionValueNotifier
import com.soyle.stories.characterarc.eventbus.ChangeThematicSectionValueNotifier
import com.soyle.stories.characterarc.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionNotifier
import com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionNotifier
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure
import com.soyle.stories.characterarc.viewBaseStoryStructure.ViewBaseStoryStructureController
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations

internal object BaseStoryStructureModule {

    init {

        scoped<BaseStoryStructureScope> {

            provide(ViewBaseStoryStructure.OutputPort::class, ListAllLocations.OutputPort::class) {
                BaseStoryStructurePresenter(
                    get<BaseStoryStructureModel>(),
                    projectScope.get<ChangeThematicSectionValueNotifier>(),
                    projectScope.get<LinkLocationToCharacterArcSectionNotifier>(),
                    projectScope.get<UnlinkLocationFromCharacterArcSectionNotifier>(),
                    projectScope.get()
                ).also {
                    it listensTo projectScope.get<ChangedCharacterArcSectionValueNotifier>()
                }
            }

            provide {
                ViewBaseStoryStructureController(
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    get()
                )
            }

            provide<BaseStoryStructureViewListener> {
                BaseStoryStructureController(
                    projectScope.applicationScope.get(),
                    type.themeId.toString(),
                    type.characterId.toString(),
                    projectScope.get(),
                    get(),
                    get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get()
                )
            }

        }

    }


}