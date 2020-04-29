/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 5:56 PM
 */
package com.soyle.stories.di.characterarc

import com.soyle.stories.characterarc.baseStoryStructure.*
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped

internal object BaseStoryStructureModule {

    init {

        scoped<BaseStoryStructureScope> {

            provide {
                ViewBaseStoryStructureController(
                  projectScope.applicationScope.get(),
                  projectScope.get(),
                  BaseStoryStructurePresenter(
                    get<BaseStoryStructureModel>(),
                    projectScope.get()
                  )
                )
            }

            provide<BaseStoryStructureViewListener> {
                BaseStoryStructureController(
                  themeId,
                  characterId,
                  get(),
                  projectScope.get()
                )
            }

        }

    }


}