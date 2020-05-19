/**
 * Created by Brendan
 * Date: 3/3/2020
 * Time: 8:33 AM
 */
package com.soyle.stories.di.characterarc

import com.soyle.stories.characterarc.characterComparison.*
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped

internal object CharacterComparisonModule {
    init {

        scoped<CharacterComparisonScope> {

            provide<CharacterComparisonViewListener> {

                val characterComparisonPresenter= CharacterComparisonPresenter(
                  get<CharacterComparisonModel>(),
                  themeId,
                  projectScope.get(),
                  projectScope.get()
                )

                CharacterComparisonViewListenerImpl(
                  CharacterComparisonController(
                    themeId,
                    projectScope.get(),
                    characterComparisonPresenter
                  ),
                  projectScope.get(),
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
        }

    }
}