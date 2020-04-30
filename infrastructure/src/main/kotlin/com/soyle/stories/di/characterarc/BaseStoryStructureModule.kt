/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 5:56 PM
 */
package com.soyle.stories.di.characterarc

import com.soyle.stories.characterarc.baseStoryStructure.*
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations

internal object BaseStoryStructureModule {

    init {

        scoped<BaseStoryStructureScope> {

            provide(ViewBaseStoryStructure.OutputPort::class, ListAllLocations.OutputPort::class) {
                BaseStoryStructurePresenter(
                  get<BaseStoryStructureModel>(),
                  projectScope.get(),
                  projectScope.get()
                )
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
                  themeId,
                  characterId,
                  projectScope.get(),
                  get(),
                  get(),
                  projectScope.get()
                )
            }

        }

    }


}